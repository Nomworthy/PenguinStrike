/*  
* To change this template, choose Tools | Templates  
* and open the template in the editor.  
*/  
 
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 * @author panos
 */
public class CSLO extends BasicGame
{
	public static final int GAMEDIM = 240;
	private static byte clientID = 0;
	private static InetAddress serverName;
	public static int inputPort = 1337;
	public static int statePort = 1338;
	private static DatagramSocket socket;
	
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
            app.setDisplayMode(GAMEDIM, GAMEDIM, false);
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
    }
 
    @Override
    public void update(GameContainer container, int delta) throws SlickException
    {
    	sendInputPackage(container.getInput());
     	
    }
 
    public void render(GameContainer container, Graphics g) throws SlickException
    {
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
}