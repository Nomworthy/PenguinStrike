
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;


public class CSLO extends BasicGame
{
	//Base Dimension of game (it's a square)
	public static final int GAMEDIM = 400;
	
	//Target Screen Resolution
	public static final int RESX= 1000;
	public static final int RESY= 1000;
	
	//ID of this client. TODO: be set by the server.
	private static byte clientID = 1;
	
	//IP address of server.
	private static InetAddress serverName;
	
	//Port to send input information to.
	public static int inputPort = 1337;
	//Port to send gamestate information to.
	public static int statePort = 1338;
	
	//Our socket to talk to the server.
	private static DatagramSocket socket;
	
	//Game Cursor.
	private Image cursor;
	//Bullet Image.
	private Image bullet;
	
	private String serverIP = "localhost";
	
	//Bullet Coord.
	private static class BulletCoord{
		int x;
		int y;
	}
	
	//List of all bullets the client knows about.
	private static LinkedList<BulletCoord> bullets;
	
    public CSLO() 
    {
        super("Cold War INDEV");
        try{
        	serverName = InetAddress.getByName(serverIP);
        	socket = new DatagramSocket(statePort);
    	  } catch (Exception e){
        	System.out.println("Could not create Socket");
        }
    }
 
    //Creates the game window.
    public static void main(String[] arguments)
    {
        try
        {
            AppGameContainer app = new AppGameContainer(new CSLO());
            app.setDisplayMode(RESX, RESY, false);
            app.start();
        }
        catch (SlickException e)
        {
            e.printStackTrace();
        }
    }
 
    //Called once after the window is created.
    @Override
    public void init(GameContainer container) throws SlickException
    {
    	CState.players = new CPlayer[]{ new CPlayer(),new CPlayer(),new CPlayer(),new CPlayer()};
    	cursor = new Image("data/gui/mouse.png");
    	bullet = new Image("data/weapon/bullet.png");
    	cursor.setFilter(Image.FILTER_NEAREST);
    	bullet.setFilter(Image.FILTER_NEAREST);
    	container.setMouseGrabbed(true);
    	CState.worldMap = new WorldMap("data/maps/Map1.tmx");
    }
 
    @Override
    public void update(GameContainer container, int delta) throws SlickException
    {
    	//fetch user inputs
    	updateInputs(container.getInput());
    	//send to da server
    	sendInputPacket(container.getInput());
    	//get inputs back.
    	readState();
    }
 
    public void render(GameContainer container, Graphics g) throws SlickException
    {
    	g.scale((float)RESX/GAMEDIM,(float)RESY/GAMEDIM);
    	CPlayer self = CState.players[clientID];
    	//if the player were centered on screen, where is the map drawn?
    	int playerXBase = (int) (self.getX() + self.RADIUS - GAMEDIM/2);
    	int playerYBase = (int) (self.getY() + self.RADIUS - GAMEDIM/2);
    	
    	//factor in mouse.
    	int deltaX = (CState.scaledMouseX - ((GAMEDIM/2)));
    	int deltaY = (CState.scaledMouseY - ((GAMEDIM/2)));
    	
    	//draw the map
       	int mapOffsetX = playerXBase + deltaX;
    	int mapOffsetY = playerYBase + deltaY;
    	CState.worldMap.drawWorldMap(mapOffsetX,mapOffsetY);
 
    	for(int i = 0; i != CState.players.length; i++){
    		CPlayer p = CState.players[i];
    		p.draw(g,mapOffsetX,mapOffsetY);
    	}
    	
    	if(bullets != null){
    		for(BulletCoord t : bullets)
    		{
    			g.drawImage(bullet,t.x - (mapOffsetX) ,t.y - (mapOffsetY));
    		}
    	}
    	
    	g.drawImage(cursor,CState.scaledMouseX-3 , CState.scaledMouseY-3 );
    }
    
    public static void sendInputPacket(Input in){
		final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		final DataOutputStream daos=new DataOutputStream(baos);
		try
		{
			daos.writeByte(clientID);
			daos.writeShort((short)(CState.scaledMouseX));
			daos.writeShort((short)(CState.scaledMouseY));
			daos.writeBoolean(CState.moveW);
			daos.writeBoolean(CState.moveA);
			daos.writeBoolean(CState.moveS);
			daos.writeBoolean(CState.moveD);
			daos.writeBoolean(CState.mouse1);
			daos.writeBoolean(CState.mouse2);
			daos.close();
			final byte[] bytes=baos.toByteArray();
    		socket.send(new DatagramPacket(bytes,bytes.length,serverName,CSLO.inputPort));
		} catch(Exception e)
		{
    			System.out.println(e.getMessage());
    	}
    }
    	
    
    public static void updateInputs(Input in)
    {
    	CState.scaledMouseX = ((int)((double)in.getMouseX() * ((double)GAMEDIM/(double)RESX)));
    	CState.scaledMouseY = ((int)((double)in.getMouseY() * ((double)GAMEDIM/(double)RESY)));
    	CState.moveW = in.isKeyDown(Input.KEY_W);
    	CState.moveA = in.isKeyDown(Input.KEY_A);
    	CState.moveS = in.isKeyDown(Input.KEY_S);
    	CState.moveD = in.isKeyDown(Input.KEY_D);
    	CState.mouse1 = in.isMousePressed(0);
    	CState.mouse2 = in.isMousePressed(1);

    }
    
    public static void readState(){
    	try 
    	{
    		//TODO Count byte payload
    		socket.setSoTimeout(20);
    		byte[] inputbfr = new byte[5000];
    		DatagramPacket packet = new DatagramPacket(inputbfr, 5000);
    		socket.receive(packet);
    		final ByteArrayInputStream bais=new ByteArrayInputStream(inputbfr);
    		final DataInputStream dais=new DataInputStream(bais);
    		int playerCount = dais.readInt();
    		for (int i = 0; i != playerCount; i++){
    			CState.players[i].setX(dais.readFloat());
    			CState.players[i].setY(dais.readFloat());
       			CState.players[i].setRotation(dais.readFloat());
    			CState.players[i].setFrame(dais.readInt());
    		}
    		
	    	int projCount = dais.readInt();
    		bullets = new LinkedList<BulletCoord>();
	    	for(int i = 0; i != projCount; i++){
	    		BulletCoord t = new BulletCoord();
	    		t.x = dais.readInt();
	    		t.y = dais.readInt();
	    		bullets.add(t);
	    	}
		} catch (Exception e){
	//System.out.println("Client Error: " + e.getMessage());
		}
    }

}