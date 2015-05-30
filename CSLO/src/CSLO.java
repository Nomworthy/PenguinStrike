/*  
* To change this template, choose Tools | Templates  
* and open the template in the editor.  
*/  
 
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 * @author panos
 */
public class CSLO extends BasicGame
{
	public static final int GAMEDIM = 240;
	public static final int RESX= 1366;
	public static final int RESY= 768;
	private static byte clientID = 0;
	private static InetAddress serverName;
	public static int inputPort = 1337;
	public static int statePort = 1338;
	private static DatagramSocket socket;
	private Image cursor;
	
    public CSLO()
    {
        super("CSLO");
        try{
        	serverName = InetAddress.getByName("localhost");
        	socket = new DatagramSocket(statePort);
        } catch (Exception e){
        	System.out.println("Could not create Socket");
        }
    }
 
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
    	updateInputs(container.getInput());
    	sendInputPackage(container.getInput());
    	readState();
    	
     	
    }
 
    public void render(GameContainer container, Graphics g) throws SlickException
    {
    	g.scale((float)RESX/GAMEDIM,(float)RESY/GAMEDIM);
    	//if the player were centered on screen, draw as such.
    	int playerXBase = (int) (WorldState.players[clientID].getCenterX() - GAMEDIM/2);
    	int playerYBase = (int) (WorldState.players[clientID].getCenterY() - GAMEDIM/2);
    	
    	//an offset.
    	int deltaX = (WorldState.players[clientID].getMouseX() - ((GAMEDIM/2)));
    	int deltaY = (WorldState.players[clientID].getMouseY() - ((GAMEDIM/2)));
    	WorldState.map.drawWorldMap(playerXBase + deltaX,playerYBase + deltaY);
    	WorldState.players[0].draw(g,playerXBase + deltaX,playerYBase + deltaY);	
    	g.drawImage(cursor,WorldState.players[clientID].getMouseX()-3 , WorldState.players[clientID].getMouseY()-3 );
    	//WorldState.map.basicRender();
    }
    
    public static void sendInputPackage(Input in){

    	byte[] inputpkg = new byte[9];
    	inputpkg[0] = clientID;
    	inputpkg[1] = (byte)(in.getMouseX() - 127);	
    	inputpkg[2] = (byte)(in.getMouseY() - 127);	
    	inputpkg[3] = in.isKeyDown(Input.KEY_W) ? (byte)1 : (byte)0;	
    	inputpkg[4] = in.isKeyDown(Input.KEY_A) ? (byte)1 : (byte)0;	
    	inputpkg[5] = in.isKeyDown(Input.KEY_S) ? (byte)1 : (byte)0;	
    	inputpkg[6] = in.isKeyDown(Input.KEY_D) ? (byte)1 : (byte)0;	
    	inputpkg[7] = in.isMouseButtonDown(0) ? (byte)1 : (byte)0;	
    	inputpkg[8] = in.isMouseButtonDown(1) ? (byte)1 : (byte)0;	
    	try{
    		socket.send(new DatagramPacket(inputpkg,9,serverName,inputPort));
    	} catch (Exception e){
    		System.out.println("Could not send inputs");
    	}
    }
    
    public static void updateInputs(Input in){
    	WorldState.players[clientID].setMouseX((int)((double)in.getMouseX() * ((double)GAMEDIM/(double)RESX)));
    	WorldState.players[clientID].setMouseY((int)((double)in.getMouseY() * ((double)GAMEDIM/(double)RESY)));
    	WorldState.players[clientID].setMoveW(    	in.isKeyDown(Input.KEY_W) );
    	WorldState.players[clientID].setMoveS(    	in.isKeyDown(Input.KEY_S) );
    	WorldState.players[clientID].setMoveA(    	in.isKeyDown(Input.KEY_A) );
    	WorldState.players[clientID].setMoveD(    	in.isKeyDown(Input.KEY_D) );

    }
    
    public static void readState(){
    	try {
    		socket.setSoTimeout(20);
    	byte[] inputbfr = new byte[8];
		DatagramPacket packet = new DatagramPacket(inputbfr, 8);
		socket.receive(packet);
    	final ByteArrayInputStream bais=new ByteArrayInputStream(inputbfr);
    	final DataInputStream dais=new DataInputStream(bais);
    	
			WorldState.players[clientID].setX(dais.readFloat());
	    	WorldState.players[clientID].setY(dais.readFloat());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
   
    
}