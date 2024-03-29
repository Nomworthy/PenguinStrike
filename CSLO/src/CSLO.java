import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.LinkedList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.AppletGameContainer.Container;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
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
	private static byte clientID = -1;
	
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
	private Image explosionImage;
	
	private Animation rocket;
	private final static int rocketWidth= 20;
	private final static int rocketHeight= 8;
	
	private String serverIP;
	private CMainMenu preLobby;
	
	private long currentTimeStamp;
	
	public static final byte HANDSHAKE = -127;
	public static final byte TEAMREQUEST = -126;
	public static final byte BUYREQUEST = -125;
	public static final byte MAPREQUEST = -124;
	
	static final int maxPlayerCount = 10;

	
	private static boolean buyMenu = false;
	
	private LinkedList<String> ipList = new LinkedList<String>();
	
	enum GameState{
		//menu for setting name, color, etc.
		PRELOBBY,
		//In a Lobby
		INLOBBY,
		//In game : setup
		SETUP,
		FIGHT,
		END,
		TEAMMENU
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
	
	private static class Explosion{
		float x;
		float y;
		int frame;
	}
	
	//List of all bullets the client knows about.
	private static LinkedList<BulletCoord> bullets = new LinkedList<BulletCoord>();
	private static LinkedList<Explosion> explosions = new LinkedList<Explosion>();
	
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
    	CState.players = new CPlayer[]{new CPlayer(),new CPlayer(),new CPlayer(),new CPlayer(),
    								new CPlayer(),new CPlayer(),new CPlayer(),new CPlayer()};
    	cursor = new Image("data/gui/mouse.png");
    	bullet = new Image("data/weapon/bullet.png");
    	explosionImage = new Image("data/weapon/explosion.png");
    
    	
		rocket = new Animation(new SpriteSheet("data/weapon/rocket.png",20,8),70);
		rocket.setAutoUpdate(true);
    	cursor.setFilter(Image.FILTER_NEAREST);
    	bullet.setFilter(Image.FILTER_NEAREST);
    	container.setMouseGrabbed(true);
    	CState.worldMap = new WorldMap("data/maps/Map2.tmx");
    	CWFont.initFontSheet();
    	preLobby = new CMainMenu(container);
    	explosions = new LinkedList<Explosion>();
    	Weapon.load();
    }
 

	@Override
    public void update(GameContainer container, int delta) throws SlickException
    {
		//clean!
    	switch(gs)
    	{
			case INLOBBY:
				break;
			case PRELOBBY:
				updateInputs(container.getInput());
				preLobby.doLogic(container,delta,container.getInput());
				if(preLobby.isDone()){
					gs = GameState.TEAMMENU;
					serverIP = preLobby.getServerIP();
					preLobby.getName();
					initServer(!CState.mouse2,preLobby.getPrimaryCol(), preLobby.getSecondaryCol());
				}
				break;
			case SETUP:
			case END:
			case FIGHT:
			case TEAMMENU:
				//fetch user inputs
				updateInputs(container.getInput());
				
				toggleBuyMenu(container.getInput());
				if(buyMenu)
				{
					byte buy = requestBuy(container);
					if(buy >= 0)
					{
						//Dirty, awful way to do this UNTIL we have a proper menu.
						sendBuyRequest(buy);
						readState();
						break;
					}
				}
				
				
				inventoryMenu(container.getInput());
				
				//send to da server
				if(gs != GameState.TEAMMENU)
				{
					sendInputPacket(container.getInput());
					
				}
				else 
				{
					boolean mPressed = container.getInput().isMousePressed(0) ;
			    	if(mPressed && isOnPenguinButton())
			    	{
			    		gs = GameState.FIGHT;
						switchTeam(true);
			    	}
			    		
			    	if(mPressed && isOnBearButton())
			    	{
			    		gs = GameState.FIGHT;
			    		switchTeam(false);
			    	}
					
				}
			//	else
			//		sendMenuPacket(container.getInput());
				//get inputs back.
				readState();
				//TODO Fix dirty hardcoding
				double delta2 = 30.0000000;
				moveBullets(delta2);
			break;
				
    	}
    }
 


	private byte requestBuy(GameContainer container) {
		Input i = container.getInput();
		
		if(i.isKeyPressed(Input.KEY_LCONTROL))
		{
			if(i.isKeyPressed(Input.KEY_1))
				return 50;
			if(i.isKeyPressed(Input.KEY_2))
				return 51;
			if(i.isKeyPressed(Input.KEY_3))
				return 52;
			if(i.isKeyPressed(Input.KEY_4))
				return 53;
			if(i.isKeyPressed(Input.KEY_5))
				return 54;
			if(i.isKeyPressed(Input.KEY_6))
				return 55;
		}
		
		if(i.isKeyPressed(Input.KEY_1))
			return 0;
		if(i.isKeyPressed(Input.KEY_2))
			return 1;
		if(i.isKeyPressed(Input.KEY_3))
			return 2;
		if(i.isKeyPressed(Input.KEY_4))
			return 3;
		if(i.isKeyPressed(Input.KEY_5))
			return 4;
		if(i.isKeyPressed(Input.KEY_6))
			return 5;
		
		return -1;
	}

	private void inventoryMenu(Input input) {
		
		
		
		if(input.isKeyPressed(Input.KEY_1) && CState.players[clientID].getInventory()[0] != null)
		{
			CState.invPointer = 0;
		}
		
		if(input.isKeyPressed(Input.KEY_2) && CState.players[clientID].getInventory()[1] != null)
		{
			CState.invPointer = 1;
		}
		
		if(input.isKeyPressed(Input.KEY_3) && CState.players[clientID].getInventory()[2] != null)
		{
			CState.invPointer = 2;
		}
		
		if(input.isKeyPressed(Input.KEY_4) && CState.players[clientID].getInventory()[3] != null)
		{
			CState.invPointer = 3;
		}
		
		if(input.isKeyPressed(Input.KEY_5) && CState.players[clientID].getInventory()[4] != null)
		{
			CState.invPointer = 4;
		}
		
		if(input.isKeyPressed(Input.KEY_6) && CState.players[clientID].getInventory()[5] != null)
		{
			CState.invPointer = 5;
		}
		
		if(input.isKeyPressed(Input.KEY_7) && CState.players[clientID].getInventory()[6] != null)
		{
			CState.invPointer = 6;
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
			case TEAMMENU:

				
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
				
				if(buyMenu)
				{
					mapOffsetX = playerXBase;
					mapOffsetY = playerYBase;	
					
				}
				
				//manhandle camera
				if(gs == GameState.TEAMMENU)
				{
					mapOffsetX = 400;
					mapOffsetY = 400;			
				} 
				
				CState.worldMap.drawWorldMap(mapOffsetX,mapOffsetY);
				
				if(CState.buildMode && gs != GameState.TEAMMENU)
				{
					//TILE : (CState.scaledMouseX + mapOffsetX) / 8) 
					int tileX = ((((CState.scaledMouseX + mapOffsetX) / 8)));
					int tileY = ((((CState.scaledMouseY + mapOffsetY) / 8)));
					
				//	CWFont.draw(g, -mapOffsetX + (8*tileX) + "", 30 , 30, 1, Color.red);
				//	CWFont.draw(g, -mapOffsetY + (8*tileY) + "", 30 , 50, 1, Color.red);
					
					g.setColor(Color.black);
					g.drawRect(-mapOffsetX + (8*tileX), -mapOffsetY + (8*tileY), 8, 8);
					
					g.setColor(new Color(0f,0f,1f,.2f));
					g.fillRect(-mapOffsetX + (8*tileX), -mapOffsetY + (8*tileY), 8, 8);
					
				}		
 
				for(int i = 0; i != CState.players.length; i++){
					CPlayer p = CState.players[i];
					p.draw(g,mapOffsetX,mapOffsetY);
				}
    	
				if(bullets != null){
					for(BulletCoord t : bullets)
					{
						if(t.bullet)
						{
							g.drawImage(bullet,t.x - (mapOffsetX)  ,t.y - (mapOffsetY) );
						}
						else{
							//will have to pull rotatation
							Image i = rocket.getCurrentFrame();
							i.setRotation((float)Math.toDegrees(t.rot));
							g.drawImage(i,t.x +- (mapOffsetX)+- rocketWidth/2 ,t.y +- (mapOffsetY)+ - rocketHeight/2);
							g.setColor(Color.blue);
							g.drawRect(t.x - (mapOffsetX)  ,t.y - (mapOffsetY),1,1);
						}
					}
				}
				
				if(explosions != null)
				{
					for(Explosion e : explosions)
					{
						//g.drawImage(explosionImage, e.x - 15, e.y - 15, 30*e.frame,0, 30*(1+e.frame), 30);
						float baseX =  e.x + -(mapOffsetX);
						float baseY = e.y + -(mapOffsetY);
						g.drawImage(explosionImage,baseX - 30, baseY - 30, baseX + 30, baseY + 30, 60*e.frame,0, 60*(1+e.frame), 60);
						
					}
				}
				
				
				if(gs == GameState.TEAMMENU)
				{
					drawTeamMenu(g);
				}

				g.setColor(new Color(0f,0f,0f,0.5f));
				g.fillRect(300, 350, 100, 50);
				
				if(CState.players[clientID] != null)
				{
			
					CWFont.draw(g, "Life:   "+CState.players[clientID].getHP(), 305, 355, 1, new Color (1f,1f,1f,0.5f));
					
					if(CState.players[clientID].getInventory()[CState.invPointer] != null)
						CWFont.draw(g, "Ammo:   "+CState.players[clientID].getInventory()[CState.invPointer].getBulletsLeft()+"/"+CState.players[clientID].getInventory()[CState.invPointer].getMagsLeft(), 305, 365, 1, new Color (1f,1f,1f,0.5f));
					
					CWFont.draw(g, "Round X  $" + CState.players[clientID].getMoney(), 305, 375, 1, new Color (1f,1f,1f,0.5f));
					//Not op'd
					String state;
					if(CState.buildMode)
						state = "Build Time ";
					else
						state = "Fight Time ";
					
					CWFont.draw(g, state + CState.time, 305, 385, 1, new Color (1f,1f,1f,0.5f));
					
				}
				
				//24 * 2 (26 * 7) + 2 = 184
				g.setColor(new Color(0f,0f,1f,0.3f));
				
				for(int i = 0; i != 7; i ++)
				{
					g.fillRect(118 + (i * 26), 400 - 26, 24, 24);
				}
				
				
				g.fillRect(116, 400 - 28, 184, 26);
				g.setColor(new Color(0f,0f,0f,0.3f));
				
				int i = 0;
				for(Weapon c : CState.players[clientID].getInventory())
				{
					if(c != null)
						c.draw(118+(26*i), 374);
					i++;
				}
				
				
				if(buyMenu)
					CBuyMenu.draw(g);
				
				
				g.drawImage(cursor,CState.scaledMouseX-5 , CState.scaledMouseY-5 );
    	}
    }
    
    private void drawTeamMenu(Graphics g) {
    	//Pick what side you are on, also view players
    	g.setColor(Color.darkGray);
    	g.fillRect(100, 150, 200, 100);
    	g.setColor(Color.blue);
    	g.fillRect(110, 160, 180, 30);
    	g.setColor(Color.red);
    	g.fillRect(110, 210, 180, 30);
    	g.setColor(Color.yellow);
    	
    	if(isOnPenguinButton())
    	{
        	g.drawRect(110, 160, 180, 30);	
    	}
    		
    	if(isOnBearButton())
    	{
    	   g.drawRect(110, 210, 180, 30);	
    	}
    	    		
    	
    	CWFont.draw(g, "PENGUINS", 120, 170, 2, Color.black);
    	CWFont.draw(g, "POLAR BEARS", 120, 220, 2, Color.black);
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
			daos.writeByte(CState.invPointer);
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
    		
    		CState.buildMode = dais.readBoolean();
			CState.time = dais.readShort();
    		
    		byte playerCount = dais.readByte();
    		
    		for (int i = 0; i != playerCount; i++){
    			
    			CState.players[i].setHP(dais.readByte());
    			CState.players[i].setX(dais.readFloat());
    			CState.players[i].setY(dais.readFloat());
       			CState.players[i].setRotation(dais.readFloat());
    			CState.players[i].setFrame(dais.readByte());
    			CState.players[i].setTeam(dais.readBoolean());
    			CState.players[i].setMoney(dais.readShort());
    			
    			CState.players[i].setCol(dais.readShort(),dais.readShort(),dais.readShort() ,dais.readShort(),dais.readShort(),dais.readShort());
    			
				//Save space by not transmitting null weapons.
				for(int w = 0; w != 7; w++)
				{
					byte type = dais.readByte();
					if(type == -1)
					{
						CState.players[i].getInventory()[w] = null;
						continue;
					}else
					{
						byte ammo1 = dais.readByte();
						byte ammo2 = dais.readByte();
						CState.players[i].getInventory()[type] = new Weapon(Weapon.WeaponType.values()[type],ammo1,ammo2);
					}
				}
    			CState.players[i].setWeaponDraw(dais.readByte());
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
    
    //LINEAR TIME
    public static void removeBulletById(short id)
    {
		Iterator<BulletCoord> iterator = bullets.iterator();
		while (iterator.hasNext()) {
			BulletCoord b = iterator.next();
			if(b.id == id)
			{
				
				if(!b.bullet)
				{
					
					Explosion e = new Explosion();
					e.x = b.x;
					e.y = b.y;
					e.frame = 0;
					explosions.add(e);
					
				}
					
					
				iterator.remove();
			}
		}
    	
    }
    
    void moveBullets(double ms){
    	
    	for(BulletCoord b : bullets){
    		b.x += b.xVel * (ms);
    		b.y += b.yVel * (ms);
    	}
    	
    	
		Iterator<Explosion> iterator = explosions.iterator();
		while (iterator.hasNext())
		{
			Explosion e = iterator.next();
			e.frame++;
			if(e.frame == 4)		
				iterator.remove();
		}    	
    }
    
    void initServer(boolean fixedDirMode, Color primary, Color secondary)
    {
        try
        {
        	serverName = InetAddress.getByName(serverIP);
        	socket = new DatagramSocket(statePort);
        	socket.setReceiveBufferSize(50000);
        	socket.setSendBufferSize(50000);
        	
        	final ByteArrayOutputStream baos=new ByteArrayOutputStream();
    		final DataOutputStream daos=new DataOutputStream(baos);
    			
    		daos.writeByte(HANDSHAKE);
    		
    		daos.writeBoolean(fixedDirMode);
    		
    		daos.writeShort(primary.getRed());
    		daos.writeShort(primary.getGreen());
    		daos.writeShort(primary.getBlue());

    		daos.writeShort(secondary.getRed());
    		daos.writeShort(secondary.getGreen());
    		daos.writeShort(secondary.getBlue());
    		
    		daos.close();
    		
    	
    		final byte[] bytes=baos.toByteArray();
        	socket.send(new DatagramPacket(bytes,bytes.length,serverName,CSLO.inputPort));
        
        	socket.setSoTimeout(100);
        	byte[] inputbfr = new byte[1];
        	DatagramPacket packet = new DatagramPacket(inputbfr, 1);
        	socket.receive(packet); 		
        	clientID = inputbfr[0];
        	
        } catch (Exception e)
        
        {
        	gs = GameState.PRELOBBY;
        	socket.close();
        }
    			

      
    }
    
    void switchTeam(boolean pen)
    {
        try
        {
        	
        	final ByteArrayOutputStream baos=new ByteArrayOutputStream();
    		final DataOutputStream daos=new DataOutputStream(baos);
    			
    		daos.writeByte(TEAMREQUEST);
      		daos.writeByte(clientID);
    		daos.writeBoolean(pen);
    		daos.close();
    		
    	
    		final byte[] bytes=baos.toByteArray();
        	socket.send(new DatagramPacket(bytes,bytes.length,serverName,CSLO.inputPort));
        
        	socket.setSoTimeout(100);
        	byte[] inputbfr = new byte[1];
        	DatagramPacket packet = new DatagramPacket(inputbfr, 1);
        	socket.receive(packet); 		
        	
        	if(inputbfr[0] == TEAMREQUEST){
        		return;
        	} else {
        		System.out.println("Server ignored request!");
        	}
        	
        } catch (Exception e)
        
        {
        	gs = GameState.PRELOBBY;
        	socket.close();
        }
    			

      
    }
    
    boolean isOnPenguinButton()
    {
    	return(CState.scaledMouseX > 110 && CState.scaledMouseX < 110+180 && 
    			CState.scaledMouseY > 160 && CState.scaledMouseY < 160+30);
    }
    
    boolean isOnBearButton()
    {
    	return(CState.scaledMouseX > 110 && CState.scaledMouseX < 110+180 && 
    	    	   CState.scaledMouseY > 210 && CState.scaledMouseY < 210+30);
    }
    
    void toggleBuyMenu(Input i)
    {
    	if(i.isKeyPressed(Input.KEY_B))
    		buyMenu = !buyMenu;
    }
    
    void sendBuyRequest(int weapon)
    { 	
    	try {
    		
	     	final ByteArrayOutputStream baos=new ByteArrayOutputStream();
			final DataOutputStream daos=new DataOutputStream(baos);
				
			daos.writeByte(BUYREQUEST);
	  
			daos.writeByte(clientID);
			daos.writeByte(weapon);
			daos.close();
			
			final byte[] bytes=baos.toByteArray();
	    	socket.send(new DatagramPacket(bytes,bytes.length,serverName,CSLO.inputPort));
	    	
		} catch (IOException e) {
			e.printStackTrace();
		}

    }

}