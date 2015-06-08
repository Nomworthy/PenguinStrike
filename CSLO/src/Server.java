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
import java.net.UnknownHostException;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;


public class Server extends BasicGame{
	
	//our socket for receiving player data
	private static DatagramSocket sock;
	
	//List of client names
	public static InetAddress clientNames[];
	
	//Timestamp to keep track of logic steps
	public static long lastTimeStamp;
	
	public Server(String title) {
		super(title);
	}
	
	public static void main(String args[]) throws SocketException, UnknownHostException{
		//TODO take a slick approach instead.
		addKillSwitch();
		
	    try
	    {
	    	AppGameContainer app = new AppGameContainer(new Server("ServerMapUtil"));
	    	app.setDisplayMode(1, 1, false);
	    	app.start();
	    }
	    catch (SlickException e)
	    {
	    	e.printStackTrace();
	    }			
	}
		
	
	private static void doGameLogic(long l) {
		float ms = (float) (l*.000001);
		
		for(SPlayer p : SState.players)
		{
			double rotation = (Math.atan2(p.getMouseY() - (CSLO.GAMEDIM/2), p.getMouseX() - (CSLO.GAMEDIM/2)));
			p.setRot((float) Math.toDegrees(rotation + Math.PI));
		
			
			if(p.getMouse1() && !p.isHeldMouse()){
				SProjectile newP = new SBullet(p.getCenterX(),p.getCenterY(),(float)Math.cos(rotation)/3f,(float)Math.sin(rotation)/3f,SState.nextBulletId());
				SState.addProjectile(newP);
				SState.newProj.add(newP);
			}
			
			if(p.getMouse2() && !p.isHeldMouse()){
				SProjectile newP = new SRocket(p.getCenterX(),p.getCenterY(),(float)Math.cos(rotation)/5f,(float)Math.sin(rotation)/5f,(float)rotation,SState.nextBulletId());	
				SState.addProjectile(newP);
				SState.newProj.add(newP);
			}
			p.setHeldMouse(p.getMouse1() || p.getMouse2());
			
			//TODO BAD STYLE
		if(p.getMoveW()){
				p.setCoords((float)(p.getX() + Math.cos(rotation) * p.getSpeed() * ms), (float)(p.getY() + Math.sin(rotation) * p.getSpeed() * ms));
				p.doFrameLogic(ms);
		} else
		if(p.getMoveA()){
			rotation = rotation + (3*Math.PI)/2;
			p.setCoords((float)(p.getX() + Math.cos(rotation) * p.getSpeed() * ms), (float)(p.getY() + Math.sin(rotation) * p.getSpeed() * ms));
			p.doFrameLogic(ms);
		} else
		if(p.getMoveS()){
			rotation = rotation + Math.PI;
			p.setCoords((float)(p.getX() + Math.cos(rotation) * p.getSpeed() * ms), (float)(p.getY() + Math.sin(rotation) * p.getSpeed() * ms));
			p.doFrameLogic(ms);
		} else
		if(p.getMoveD()){
			rotation = rotation + Math.PI/2;
			p.setCoords((float)(p.getX() + Math.cos(rotation) * p.getSpeed() * ms), (float)(p.getY() + Math.sin(rotation) * p.getSpeed() * ms));
			p.doFrameLogic(ms);
		} else {
			p.setFrame(0);
		}
		
		}
		
	SState.physics(ms);	
	
	SState.map.cleanDirtyTiles();
	}
	
	private static void writeState(){
		final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		final DataOutputStream daos=new DataOutputStream(baos);
		try{
			
			daos.writeByte(SState.players.length);
			
			for(SPlayer p : SState.players){
				daos.writeFloat(p.getX());
				daos.writeFloat(p.getY());
				daos.writeFloat(p.getRot());
				daos.writeByte(p.getFrame());
			}
			
			//write new projectiles.
			daos.writeShort(SState.newProj.size());
			//writes whenever a new bullet occurs
			for(SProjectile p : SState.newProj){
				if(p instanceof SBullet){
					daos.writeBoolean(true);
					
					daos.writeShort(p.getID());
					daos.writeShort((int) (p.getShape().getX()));
					daos.writeShort((int) (p.getShape().getY()));
					
					daos.writeFloat(p.getXVel());
					daos.writeFloat(p.getYVel());
				} else {
					//it's a rocket
					daos.writeBoolean(false);
					
					daos.writeShort(p.getID());
					daos.writeShort((int) (p.getShape().getCenterX()));
					daos.writeShort((int) (p.getShape().getCenterY()));
					
					daos.writeFloat(p.getXVel());
					daos.writeFloat(p.getYVel());	
					daos.writeFloat(((SRocket)p).rotation);
				}
			}
			SState.newProj = new LinkedList<SProjectile>();
			
			//kill old projectiles.
			daos.writeShort(SState.oldProj.size());
			//writes whenever a new bullet occurs
			for(SProjectile p : SState.oldProj){
				daos.writeShort(p.getID());
			}
			
			SState.oldProj = new LinkedList<SProjectile>();
			
			
			daos.writeShort(SState.map.getDirtyTiles().size());
			//writes whenever a new bullet occurs
			for(WorldMap.Tile t : SState.map.getDirtyTiles()){
				daos.writeShort(t.x);
				daos.writeShort(t.y);
				daos.writeShort(t.id);
			}
			
			
			daos.close();
			final byte[] bytes=baos.toByteArray();
			for(InetAddress a : clientNames){
				DatagramPacket p= new DatagramPacket(bytes,bytes.length,a,CSLO.statePort);
				sock.send(p);
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
		//we need only send one of these, but yolo it's 4 bits
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

	@Override
	public void render(GameContainer arg0, Graphics arg1) throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GameContainer arg0) throws SlickException {
		try {
			clientNames = new InetAddress[]{
					InetAddress.getByName("localhost")};
			sock = new DatagramSocket(CSLO.inputPort);
			sock.setReceiveBufferSize(10000);
			sock.setSendBufferSize(10000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lastTimeStamp = System.nanoTime();
    	SState.instWorldState();
		SState.map = new WorldMap("data/maps/Map1.tmx");	
	}

	@Override
	public void update(GameContainer arg0, int arg1) throws SlickException {
		byte[] inputbfr = new byte[100];

		DatagramPacket packet = new DatagramPacket(inputbfr, 100);
		try {
			sock.setSoTimeout(5);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		SState.map.purgeDirtyTiles();
		//now we have the most availablke input, do game logic.
	}
		
}