import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.newdawn.slick.Input;

//BASICGAME needed to parse TileMap!
public class Server {
	//our socket for receiving player data
	private static DatagramSocket sock;
	//List of client names
	public static InetAddress clientNames[];
	public static long lastTimeStamp;
	
	public static void main(String args[]) throws SocketException, UnknownHostException{
		addKillSwitch();
		clientNames = new InetAddress[]{
				InetAddress.getByName("localhost"),
				InetAddress.getByName("192.168.0.119")};
		sock = new DatagramSocket(CSLO.inputPort);
		lastTimeStamp = System.nanoTime();
    	WorldState.instWorldState();
    	
		while(true){
			byte[] inputbfr = new byte[100];
			DatagramPacket packet = new DatagramPacket(inputbfr, 100);
			sock.setSoTimeout(5);
			while(true){
				try{
					sock.receive(packet);
					readClientInputs(inputbfr);	
				} catch (Exception e){
					//we read them all!
					break;
				}
			}
			long currentTimeStamp = System.nanoTime();
			doGameLogic(currentTimeStamp - lastTimeStamp);
			lastTimeStamp = System.nanoTime();
			writeState();
			//now we have the most availablke input, do game logic.
		
		}
	}
		
	private static void doGameLogic(long l) {
		float ms = (float) (l*.000001);
		
		for(Player p : WorldState.players){
			double rotation = (Math.atan2(p.getMouseY() - (CSLO.GAMEDIM/2), p.getMouseX() - (CSLO.GAMEDIM/2)));;
		
		
			if(p.getMouse1() && !p.isHeldMouse()){
				WorldState.addProjectile(new Bullet(p.getCenterX(),p.getCenterY(),(float)Math.cos(rotation)/4f,(float)Math.sin(rotation)/4f));
			}
			p.setHeldMouse(p.getMouse1());
			
			if(p.getMoveW()){
				p.setY((float)(p.getY() + Math.sin(rotation) * p.getSpeed() * ms));
				p.setX((float)(p.getX() + Math.cos(rotation) * p.getSpeed() * ms));	
		} else
		if(p.getMoveA()){
			rotation = rotation + (3*Math.PI)/2;
			p.setY((float)(p.getY() + Math.sin(rotation) * p.getSpeed() * ms));
			p.setX((float)(p.getX() + Math.cos(rotation) * p.getSpeed() * ms));	
		} else
		if(p.getMoveS()){
			rotation = rotation + Math.PI;
			p.setY((float)(p.getY() + Math.sin(rotation) * p.getSpeed() * ms));
			p.setX((float)(p.getX() + Math.cos(rotation) * p.getSpeed() * ms));	
		} else
		if(p.getMoveD()){
			rotation = rotation + Math.PI/2;
			p.setY((float)(p.getY() + Math.sin(rotation) * p.getSpeed() * ms));
			p.setX((float)(p.getX() + Math.cos(rotation) * p.getSpeed() * ms));	
		}
		
		}
	WorldState.physics(ms);	

	}
	
	private static void writeState(){
		final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		final DataOutputStream daos=new DataOutputStream(baos);
		try{
			daos.writeInt(WorldState.players.length);
			for(Player p : WorldState.players){
				daos.writeFloat(p.getX());
				daos.writeFloat(p.getY());
				daos.writeFloat(p.rotation);
				daos.writeInt(p.myFrame);
			}
			
			daos.writeInt(WorldState.proj.size());
			//TODO: Adhoc for bullets ONLY
			for(Projectile p : WorldState.proj){
				//say where to draw bullets.
				daos.writeInt((int) (p.getShape().getX() - Bullet.offSet));
				daos.writeInt((int) (p.getShape().getY() - Bullet.offSet));
			}
			
			daos.close();
			final byte[] bytes=baos.toByteArray();
			for(InetAddress a : clientNames){
				sock.send(new DatagramPacket(bytes,bytes.length,a,CSLO.statePort));
			}
    	} catch (Exception e){
    		e.printStackTrace();}
	}

	public static void readClientInputs(byte[] inpack) throws IOException{

		final ByteArrayInputStream bais=new ByteArrayInputStream(inpack);
		final DataInputStream dais=new DataInputStream(bais);
    	
		//1 Identifier
		int clientID = dais.readByte();
		//2 MOUSE_X 
		//3 MOUSE_Y
		WorldState.players[clientID].setMouseX(dais.readShort());
		WorldState.players[clientID].setMouseY(dais.readShort());
		WorldState.players[clientID].setMoveW(dais.readBoolean());
		WorldState.players[clientID].setMoveA(dais.readBoolean());
		WorldState.players[clientID].setMoveS(dais.readBoolean());
		WorldState.players[clientID].setMoveD(dais.readBoolean());
		WorldState.players[clientID].setMouse1(dais.readBoolean());
		WorldState.players[clientID].setMouse2(dais.readBoolean());
		dais.close();
	}
		
	public static void addKillSwitch(){
		JButton kill = new JButton("Kill Server");
		kill.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent evt) {
			  sock.close();
			  return;
		  }
		});
		JFrame frame = new JFrame("El Server");
		frame.add(kill);
		frame.pack();
		frame.setVisible(true);
		
	}
		
}