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
	//Timestamp to keep track of logic steps
	public static long currentTimeStamp;
	
	private static long tickRateNano =30000000L;
	
	public static final int GARANDACC = 4;
	public static final int GARANDCLIP = 7;

	public static final int GARANDCOST = 500;
	public static final int GARANDPOWER = 50;
	
	public static final float GARANDSPEED = .46f;
	
	private static float GARANDSPREAD = (float) Math.toRadians(GARANDACC);

	
	public static final int homeSpawnX = 500;
	public static final int homeSpawnY = 500;
	
	public static final int awaySpawnX = 309*8;
	public static final int awaySpawnY = 178*8;

	public static final float ROCKETSPEED = .2f;
	
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
			if(p != null)
			{

			boolean easyMove = p.getFixedMoveDir();
			
				
			double rotation = (Math.atan2(p.getMouseY() - (CSLO.GAMEDIM/2), p.getMouseX() - (CSLO.GAMEDIM/2)));
			p.setRot((float) Math.toDegrees(rotation + Math.PI));
		
			
			if(p.getMouse1() && !p.isHeldMouse()){
				
				//shooting a bullet, have to move it.
				float gunXAdd = (float) (((float) (1.1f*p.getSpeed()*ms) + 2f + p.radius) *  Math.cos(-Math.PI + -Math.toRadians(p.getRot()) +- .38));
				float gunYAdd =  (float) (((float) (1.1f*p.getSpeed()*ms) + 2f + p.radius) * Math.sin(-Math.toRadians(p.getRot()) +- .38));	
		
				//PROBLEM!	double BRotation= (Math.atan2(p.getMouseY() - ((CSLO.GAMEDIM/2) + gunYAdd), p.getMouseX() - ((CSLO.GAMEDIM/2) + gunXAdd)));
			
				//Bullet inaccuracy
				//depends on who fired it.
				float spread = (float) (Math.random()*GARANDSPREAD) - (GARANDSPREAD/2f);
				
				SProjectile newP = new SBullet(p.getCenterX()+gunXAdd,p.getCenterY()+gunYAdd,(float)Math.cos(rotation+spread)*GARANDSPEED,(float)Math.sin(rotation+spread)*GARANDSPEED,SState.nextBulletId());
				SState.addProjectile(newP);
				SState.newProj.add(newP);
				
				
			}
			
			if(p.getMouse2() && !p.isHeldMouse()){
				
				SProjectile newP = new SRocket(p.getCenterX(),p.getCenterY(),(float)Math.cos(rotation)*ROCKETSPEED,(float)Math.sin(rotation)*ROCKETSPEED,(float)rotation,SState.nextBulletId());	
				SState.addProjectile(newP);
				SState.newProj.add(newP);
			
				//don't want to accidenally blow up the player!
				newP.getShape().setX(newP.getShape().getX() + ((float)Math.cos(rotation) * ((1.1f*p.getSpeed()*ms) + 10f)));
				newP.getShape().setY(newP.getShape().getY() + ((float)Math.sin(rotation) * ((1.1f*p.getSpeed()*ms) + 10f)));
				
				
			}
			p.setHeldMouse(p.getMouse1() || p.getMouse2());
			
		if(!easyMove)
		{
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
		else
		{
			double moveSpeed = 1.0;
			//half move speed
			if((p.getMoveW() || p.getMoveS()) && (p.getMoveA() || p.getMoveD()))
			{
				moveSpeed = .70710678118;
			}
				
			//ez mode
			if(p.getMoveW()){
				p.setCoords((float)(p.getX()), (float)(p.getY() - (p.getSpeed() * moveSpeed * ms)));
			} 
			if(p.getMoveA()){
				p.setCoords((float)(p.getX()  - (p.getSpeed() * moveSpeed * ms)), (float)(p.getY()));
			} 
			if(p.getMoveS()){
				p.setCoords((float)(p.getX()), (float)(p.getY() + (p.getSpeed() * moveSpeed * ms)));
			} 
			if(p.getMoveD()){
				p.setCoords((float)(p.getX() + (p.getSpeed() * moveSpeed * ms)), (float)(p.getY()));
	
			}
			
			if(!p.getMoveW() && !p.getMoveA() && !p.getMoveS() && !p.getMoveD())
			{
				p.setFrame(0);
			} else
			{
				p.doFrameLogic(ms);
				
			}
	
		}
	}}
		
	SState.physics(ms);	
	
	SState.map.cleanDirtyTiles();
	}
	
	private static void writeState(){
		final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		final DataOutputStream daos=new DataOutputStream(baos);
		try{
			
			daos.writeByte(SState.playerCount);
			
			for(SPlayer p : SState.players){
				if(p != null)
				{
					daos.writeFloat(p.getX());
					daos.writeFloat(p.getY());
					daos.writeFloat(p.getRot());
					daos.writeByte(p.getFrame());
					daos.writeBoolean(p.getTeam());
					for(int i = 0; i != 6; i++)
					{
						daos.writeShort(p.sendColorArray()[i]);
					}
				}
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
				if(a != null)
				{
					DatagramPacket p= new DatagramPacket(bytes,bytes.length,a,CSLO.statePort);
					sock.send(p);
				}
			}
    	} catch (Exception e){
    		e.printStackTrace();}
	}

	public static void readClientInputs(byte[] inpack, DatagramPacket packet) throws IOException{

		final ByteArrayInputStream bais=new ByteArrayInputStream(inpack);
		final DataInputStream dais=new DataInputStream(bais);
    	
		//1 Identifier
		int clientID = dais.readByte();
		
		if(clientID == CSLO.HANDSHAKE){
			System.out.println("TEST");
			final ByteArrayOutputStream baos=new ByteArrayOutputStream();
			final DataOutputStream daos=new DataOutputStream(baos);
				
			daos.writeByte(SState.playerCount);
			daos.close();
			final byte[] b = baos.toByteArray();
			DatagramPacket p= new DatagramPacket(b,b.length,packet.getAddress(),CSLO.statePort);
			sock.send(p);
			clientNames[SState.playerCount] = packet.getAddress();
			SState.players[SState.playerCount] = new SPlayer(dais.readBoolean(), dais.readShort(),dais.readShort(),dais.readShort(),dais.readShort(),dais.readShort(),dais.readShort());
			SState.playerCount++;
			
		} else if(clientID == CSLO.TEAMREQUEST){
			
			//read team request
			final ByteArrayOutputStream baos=new ByteArrayOutputStream();
			final DataOutputStream daos=new DataOutputStream(baos);
				
			daos.writeByte(CSLO.TEAMREQUEST);
			daos.close();
			
			byte playerID = dais.readByte();
			
			//Transmit COLOR1, COLOR2, TEAM. 
			SState.players[playerID].setTeam(dais.readBoolean());
			
			
			final byte[] b = baos.toByteArray();
			DatagramPacket p= new DatagramPacket(b,b.length,packet.getAddress(),CSLO.statePort);
			sock.send(p);
		} else 
			
			
		{
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

	public void render(GameContainer arg0, Graphics arg1) throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GameContainer arg0) throws SlickException {
		try {
			clientNames = new InetAddress[CSLO.maxPlayerCount];
			sock = new DatagramSocket(CSLO.inputPort);
			sock.setReceiveBufferSize(10000);
			sock.setSendBufferSize(10000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lastTimeStamp = System.nanoTime();
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
				readClientInputs(inputbfr,packet);	
			} catch (Exception e){
				//we read them all!
				break;
			}
		}
		
		doGameLogic(tickRateNano);

		currentTimeStamp = System.nanoTime();
		long execTime = currentTimeStamp - lastTimeStamp;
		//I want to send every .05 s
		if(execTime < tickRateNano)
		{
			try {
				
				Thread.sleep((tickRateNano-execTime)/1000000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lastTimeStamp = currentTimeStamp;
		writeState();
		SState.map.purgeDirtyTiles();
		
	}
		
}