import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.LinkedList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.gui.GUIContext;

public class CSLO extends BasicGame
{
	//Base Dimension of game (it's a square)
	public static final int GAMEDIM = 400;
	
	//Target Screen Resolution
	public static final int RESX= 800;
	public static final int RESY= 800;
	
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
	
	private Image cursor;
	private Image bullet;
	private Animation rocket;
	private final static int rocketWidth= 20;
	private final static int rocketHeight= 8;
	
	private String serverIP;
	private CMainMenu preLobby;
	
	enum GameState{
		//menu for setting name, color, etc.
		PRELOBBY,
		//In a Lobby
		INLOBBY,
		//In game : setup
		SETUP,
		FIGHT,
		END
	}
	
	private static GameState gs = GameState.PRELOBBY;
	
	
	//static int netProj = 0;	
	
	//Bullet Coord.
	private static class BulletCoord{
		short id;
		boolean bullet;
		float x;
		float y;
		float xVel;
		float yVel;
		float rot;
	}
	
	//List of all bullets the client knows about.
	private static LinkedList<BulletCoord> bullets = new LinkedList<BulletCoord>();
	
    public CSLO() 
    {
        super("Cold War INDEV");

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
		rocket = new Animation(new SpriteSheet("data/weapon/rocket.png",20,8),70);
		rocket.setAutoUpdate(true);
    	cursor.setFilter(Image.FILTER_NEAREST);
    	bullet.setFilter(Image.FILTER_NEAREST);
    	container.setMouseGrabbed(true);
    	CState.worldMap = new WorldMap("data/maps/Map1.tmx");
    	CWFont.initFontSheet();
    	preLobby = new CMainMenu(container);
    }
 
    @Override
    public void update(GameContainer container, int delta) throws SlickException
    {
    	switch(gs)
    	{
			case INLOBBY:
				break;
			case PRELOBBY:
				updateInputs(container.getInput());
				preLobby.doLogic(container,delta);
				if(preLobby.isDone()){
					gs = GameState.FIGHT;
					serverIP = preLobby.getServerIP();
					preLobby.getName();
					initServer();
				}
				break;
			case SETUP:
			case END:
			case FIGHT:
				//fetch user inputs
				updateInputs(container.getInput());
				//send to da server
				sendInputPacket(container.getInput());
				//get inputs back.
				readState();
				moveBullets(delta);
			break;
    	}
    }
 
    public void render(GameContainer container, Graphics g) throws SlickException
    {
    	g.scale((float)RESX/GAMEDIM,(float)RESY/GAMEDIM);
    	
    	//TODO Divide further. Setup, Connect, Gameplay all 3 distinct.
    	switch(gs)
    	{

			case PRELOBBY:
				preLobby.draw(cursor,g, (GUIContext)container);
				break;
			case SETUP:
			case END:
			case FIGHT:
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
						if(t.bullet)
							g.drawImage(bullet,t.x - (mapOffsetX)  ,t.y - (mapOffsetY) );
						else{
							//will have to pull rotatation
							Image i = rocket.getCurrentFrame();
							i.setRotation((float)Math.toDegrees(t.rot));
							g.drawImage(i,t.x +- (mapOffsetX)+- rocketWidth/2 ,t.y +- (mapOffsetY)+ - rocketHeight/2);
						}
					}
				}
				g.drawImage(cursor,CState.scaledMouseX-3 , CState.scaledMouseY-3 );
    	}
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
    	CState.mouse1 = in.isMouseButtonDown(0);
    	CState.mouse2 = in.isMouseButtonDown(1);

    }
    
    public static void readState(){
    	try 
    	{
    		//TODO Count byte payload
    		socket.setSoTimeout(100);
    		byte[] inputbfr = new byte[5000];
    		DatagramPacket packet = new DatagramPacket(inputbfr, 5000);
    		socket.receive(packet);
    		final ByteArrayInputStream bais=new ByteArrayInputStream(inputbfr);
    		final DataInputStream dais=new DataInputStream(bais);
    		
    		byte playerCount = dais.readByte();
    		for (int i = 0; i != playerCount; i++){
    			CState.players[i].setX(dais.readFloat());
    			CState.players[i].setY(dais.readFloat());
       			CState.players[i].setRotation(dais.readFloat());
    			CState.players[i].setFrame(dais.readByte());
    		}
    		
	    	int newProjCount = dais.readShort();
	    	//netProj += newProjCount;
	    	for(int i = 0; i != newProjCount; i++){
	    		BulletCoord t = new BulletCoord();
	    		t.bullet = dais.readBoolean();
	    		t.id = dais.readShort();
	    		t.x = (float)dais.readShort();
	    		t.y = (float)dais.readShort();
	    		t.xVel = dais.readFloat();
	    		t.yVel = dais.readFloat();
	    		if(!t.bullet){
	    			t.rot = dais.readFloat();
	    		}
	    		bullets.add(t);
	    	}

	    	int oldProjCount = dais.readShort();
	    	
	     	//netProj -= oldProjCount;
			for(int i = 0; i != oldProjCount; i++){
				removeBulletById(dais.readShort());
			}
			

	    	int tileCount = dais.readShort();
	    	
	     	//netProj -= oldProjCount;
			for(int i = 0; i != tileCount; i++){
				CState.worldMap.setTileId(dais.readShort(),dais.readShort(),CState.worldMap.getWallLayerIndex(),dais.readShort());
			}
			
			
		} catch (Exception e){
	//System.out.println("Client Error: " + e.getMessage());
		}
    }
    
    public static void removeBulletById(short id){
		Iterator<BulletCoord> iterator = bullets.iterator();
		while (iterator.hasNext()) {
			if(iterator.next().id == id)
				iterator.remove();
		}
    	
    }
    
    void moveBullets(int ms){
    	for(BulletCoord b : bullets){
    		b.x += b.xVel * ((double)ms);
    		b.y += b.yVel * ((double)ms);
    	}
    }
    
    void initServer()
    {
        try{
        	serverName = InetAddress.getByName(serverIP);
        	socket = new DatagramSocket(statePort);
        	socket.setReceiveBufferSize(50000);
        	socket.setSendBufferSize(50000);
    	  } catch (Exception e){
        	System.out.println("Could not create Socket");
        }	
    }
}