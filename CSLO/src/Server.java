import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;


public class Server {
	//our socket for receiving player data
	private static DatagramSocket sock;
	public static InetAddress serverName;
	public static long lastTimeStamp;
	
	public static void main(String args[]) throws SocketException{

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
			//now we have the most availablke input, do game logic.
		}
	}
		
	private static void doGameLogic(long l) {
		System.out.println(WorldState.players[0].getMouseX());
	}

	public static void readClientInputs(byte[] inpack){
		
		//1 Identifier
		int clientID = inpack[0];
		//2 MOUSE_X 
		//3 MOUSE_Y
		WorldState.players[clientID].setMouseX(inpack[1]);
		WorldState.players[clientID].setMouseY(inpack[2]);
		WorldState.players[clientID].setMoveW(inpack[4]);
		WorldState.players[clientID].setMoveA(inpack[5]);
		WorldState.players[clientID].setMoveS(inpack[6]);
		WorldState.players[clientID].setMoveD(inpack[7]);
		WorldState.players[clientID].setMouse1(inpack[8]);
		WorldState.players[clientID].setMouse2(inpack[9]);	
	}
		
		
	}