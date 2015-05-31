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


public class Server {
	//our socket for receiving player data
	private static DatagramSocket sock;
	
	public static InetAddress clientName;
	public static long lastTimeStamp;
	
	public static void main(String args[]) throws SocketException, UnknownHostException{
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
		
		clientName = InetAddress.getByName("localhost");
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
		Player p = (WorldState.players[0]);
		double rotation = (Math.atan2(p.getMouseY() - (CSLO.GAMEDIM/2), p.getMouseX() - (CSLO.GAMEDIM/2)));;
		float ms = (float) (l*.000001);
		if(WorldState.players[0].getMoveW()){
			WorldState.players[0].setY((float)(WorldState.players[0].getY() + Math.sin(rotation) * WorldState.players[0].getSpeed() * ms));
			
			WorldState.players[0].setX((float)(WorldState.players[0].getX() + Math.cos(rotation) * WorldState.players[0].getSpeed() * ms));
			
		}
	}
	
	private static void writeState(){
		final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		final DataOutputStream daos=new DataOutputStream(baos);
		try{
			System.out.println(WorldState.players[0].getX() + " " + WorldState.players[0].getY());
	    daos.writeFloat(WorldState.players[0].getX());
	    daos.writeFloat(WorldState.players[0].getY());
	    daos.close();
	    final byte[] bytes=baos.toByteArray();
    	try{
    		sock.send(new DatagramPacket(bytes,bytes.length,clientName,CSLO.statePort));
    	} catch(Exception e){
    		//
    	}
    	
		} catch (Exception e){;}
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
		
		
	}