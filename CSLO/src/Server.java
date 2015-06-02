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
				InetAddress.getByName("192.168.0.22"),
				InetAddress.getByName("192.168.0.103")};
		sock = new DatagramSocket(CSLO.inputPort);
		lastTimeStamp = System.nanoTime();
    	SState.instWorldState();
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
		
		for(SPlayer p : SState.players)
		{
			double rotation = (Math.atan2(p.getMouseY() - (CSLO.GAMEDIM/2), p.getMouseX() - (CSLO.GAMEDIM/2)));
			p.setRot((float) Math.toDegrees(rotation + Math.PI));
			
			if(p.getMouse1() && !p.isHeldMouse()){
				SState.addProjectile(new SBullet(p.getCenterX(),p.getCenterY(),(float)Math.cos(rotation)/4f,(float)Math.sin(rotation)/4f));
			}
			p.setHeldMouse(p.getMouse1());
			
			//TODO BAD STYLE
			if(p.getMoveW()){
				p.setY((float)(p.getY() + Math.sin(rotation) * p.getSpeed() * ms));
				p.setX((float)(p.getX() + Math.cos(rotation) * p.getSpeed() * ms));	
				p.doFrameLogic(ms);
		} else
		if(p.getMoveA()){
			rotation = rotation + (3*Math.PI)/2;
			p.setY((float)(p.getY() + Math.sin(rotation) * p.getSpeed() * ms));
			p.setX((float)(p.getX() + Math.cos(rotation) * p.getSpeed() * ms));	
			p.doFrameLogic(ms);
		} else
		if(p.getMoveS()){
			rotation = rotation + Math.PI;
			p.setY((float)(p.getY() + Math.sin(rotation) * p.getSpeed() * ms));
			p.setX((float)(p.getX() + Math.cos(rotation) * p.getSpeed() * ms));	
			p.doFrameLogic(ms);
		} else
		if(p.getMoveD()){
			rotation = rotation + Math.PI/2;
			p.setY((float)(p.getY() + Math.sin(rotation) * p.getSpeed() * ms));
			p.setX((float)(p.getX() + Math.cos(rotation) * p.getSpeed() * ms));	
			p.doFrameLogic(ms);
		} else {
			p.setFrame(0);
		}
		
		}
		
	SState.physics(ms);	

	}
	
	private static void writeState(){
		final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		final DataOutputStream daos=new DataOutputStream(baos);
		try{
			daos.writeInt(SState.players.length);
			for(SPlayer p : SState.players){
				daos.writeFloat(p.getX());
				daos.writeFloat(p.getY());
				daos.writeFloat(p.getRot());
				daos.writeInt(p.getFrame());
			}
			
			daos.writeInt(SState.proj.size());
			//TODO: Adhoc for bullets ONLY
			for(SProjectile p : SState.proj){
				//say where to draw bullets.
				daos.writeInt((int) (p.getShape().getX()));
				daos.writeInt((int) (p.getShape().getY()));
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
		SPlayer p = SState.players[clientID];
		p.setMouseX(dais.readShort());
		p.setMouseY(dais.readShort());
		p.setMoveW(dais.readBoolean());
		p.setMoveA(dais.readBoolean());
		p.setMoveS(dais.readBoolean());
		p.setMoveD(dais.readBoolean());
		p.setMouse1(dais.readBoolean());
		p.setMouse2(dais.readBoolean());
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