import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.newdawn.slick.Input;


public class Server {
	//our socket for receiving player data
	private static DatagramSocket sock;
	public static InetAddress clientName;
	public static long lastTimeStamp;
	
	public static void main(String args[]) throws SocketException, UnknownHostException{

		clientName = InetAddress.getByName("localhost");
		sock = new DatagramSocket(CSLO.inputPort);
		lastTimeStamp = System.nanoTime();
    	WorldState.instWorldState();

		
		while(true){
			byte[] inputbfr = new byte[9];
			DatagramPacket packet = new DatagramPacket(inputbfr, 9);
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
		if(WorldState.players[0].getMoveW()){
			WorldState.players[0].setY(WorldState.players[0].getY() - WorldState.players[0].getSpeed() * ms);
		}
		if(WorldState.players[0].getMoveA()){
			WorldState.players[0].setX(WorldState.players[0].getX() - WorldState.players[0].getSpeed() * ms);
		}
		if(WorldState.players[0].getMoveS()){
			WorldState.players[0].setY(WorldState.players[0].getY() + WorldState.players[0].getSpeed() * ms);
		}
		if(WorldState.players[0].getMoveD()){
			WorldState.players[0].setX(WorldState.players[0].getX() + WorldState.players[0].getSpeed() * ms);
		}
	}
	
	private static void writeState(){
		final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		final DataOutputStream daos=new DataOutputStream(baos);
		try{
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

	public static void readClientInputs(byte[] inpack){
		
		//1 Identifier
		int clientID = inpack[0];
		//2 MOUSE_X 
		//3 MOUSE_Y
		WorldState.players[clientID].setMouseX(inpack[1]);
		WorldState.players[clientID].setMouseY(inpack[2]);
		WorldState.players[clientID].setMoveW(inpack[3]);
		WorldState.players[clientID].setMoveA(inpack[4]);
		WorldState.players[clientID].setMoveS(inpack[5]);
		WorldState.players[clientID].setMoveD(inpack[6]);
		WorldState.players[clientID].setMouse1(inpack[7]);
		WorldState.players[clientID].setMouse2(inpack[8]);	
	}
		
		
	}