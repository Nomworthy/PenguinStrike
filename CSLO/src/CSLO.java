
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
	private static byte clientID = 0;
	
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
	
    public CSLO() 
    {
        super("PenguinStrike");
        try{
        	serverName = InetAddress.getByName("localhost");
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
    	WorldState.instWorldState();
    	WorldState.map = new WorldMap("data/maps/Map1.tmx");
    	cursor = new Image("data/gui/mouse.png");
    	cursor.setFilter(Image.FILTER_NEAREST);
    	container.setMouseGrabbed(true);
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
    	//if the player were centered on screen, where is the map drawn?
    	int playerXBase = (int) (WorldState.players[clientID].getCenterX() - GAMEDIM/2);
    	int playerYBase = (int) (WorldState.players[clientID].getCenterY() - GAMEDIM/2);
    	//factor in mouse.
    	int deltaX = (WorldState.players[clientID].getMouseX() - ((GAMEDIM/2)));
    	int deltaY = (WorldState.players[clientID].getMouseY() - ((GAMEDIM/2)));
    	
    	WorldState.map.drawWorldMap(playerXBase + deltaX,playerYBase + deltaY);
    	WorldState.players[0].draw(g,playerXBase + deltaX,playerYBase + deltaY);	
    	g.drawImage(cursor,WorldState.players[clientID].getMouseX()-3 , WorldState.players[clientID].getMouseY()-3 );
    }
    
    public static void sendInputPacket(Input in){
		final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		final DataOutputStream daos=new DataOutputStream(baos);
		try
		{
			daos.writeByte(clientID);
			daos.writeShort((short)(WorldState.players[clientID].getMouseX()));
			daos.writeShort((short)(WorldState.players[clientID].getMouseY()));
			daos.writeBoolean(in.isKeyDown(Input.KEY_W));
			daos.writeBoolean((in.isKeyDown(Input.KEY_A)));
			daos.writeBoolean((in.isKeyDown(Input.KEY_S)));
			daos.writeBoolean((in.isKeyDown(Input.KEY_D)));
			daos.writeBoolean(in.isMouseButtonDown(0));		
			daos.writeBoolean(in.isMouseButtonDown(1));	
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
    	WorldState.players[clientID].setMouseX((int)((double)in.getMouseX() * ((double)GAMEDIM/(double)RESX)));
    	WorldState.players[clientID].setMouseY((int)((double)in.getMouseY() * ((double)GAMEDIM/(double)RESY)));
    	WorldState.players[clientID].setMoveW(    	in.isKeyDown(Input.KEY_W) );
    	WorldState.players[clientID].setMoveS(    	in.isKeyDown(Input.KEY_S) );
    	WorldState.players[clientID].setMoveA(    	in.isKeyDown(Input.KEY_A) );
    	WorldState.players[clientID].setMoveD(    	in.isKeyDown(Input.KEY_D) );

    }
    
    public static void readState(){
    	try 
    	{
    		socket.setSoTimeout(20);
    		byte[] inputbfr = new byte[8];
    		DatagramPacket packet = new DatagramPacket(inputbfr, 8);
    		socket.receive(packet);
    		final ByteArrayInputStream bais=new ByteArrayInputStream(inputbfr);
    		final DataInputStream dais=new DataInputStream(bais);
			WorldState.players[clientID].setX(dais.readFloat());
	    	WorldState.players[clientID].setY(dais.readFloat());
		} catch (IOException e){
			e.printStackTrace();
		}
    }
   
    
}