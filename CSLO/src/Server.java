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
import org.newdawn.slick.geom.Rectangle;


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
	
	public static final short WALLCOST = 20;
	
	public static final int GARANDACC = 3;
	public static final int GARANDCLIP = 7;
	public static final int GARANDCOST = 500;
	public static final int GARANDPOWER = 30;
	public static final int GARANDVAR = 20;
	public static final float GARANDSPEED = .40f;
	
	public static final int PISTOLACC = 14;
	public static final int PISTOLCLIP = 11;
	public static final int PISTOLCOST = 100;
	public static final int PISTOLPOWER = 30;
	public static final int PISTOLVAR = 10;
	public static final float PISTOLSPEED = .25f;
	
	public static final int SHOTGUNACC = 50;
	public static final int SHOTGUNCLIP = 5;
	public static final int SHOTGUNCOST = 300;
	public static final int SHOTGUNPOWER = 5;
	public static final int SHOTGUNVAR= 20;
	public static final float SHOTGUNSPEED = .30f;
	private static final float SHOTGUNCOOLDOWNMAX = 700.0f;
	
	public static final int SMGACC = 30;
	public static final int SMGCLIP = 30;
	public static final int SMGCOST = 400;
	public static final int SMGPOWER = 15;
	public static final int SMGVAR = 10;
	public static final float SMGSPEED = .60f;
	private static final float SMGCOOLDOWNMAX = 150.0f;
	public static final int SHOTGUNPELLETS = 10;
	
	
	public static final float ROCKETSPEED = .2f;
	public static final short ROCKETPOWER = 200;
	public static final int ROCKETCOST = 800;
	
	private static float GARANDSPREAD = (float) Math.toRadians(GARANDACC);
	private static float SHOTGUNSPREAD = (float) Math.toRadians(SHOTGUNACC);
	private static float SMGSPREAD = (float) Math.toRadians(SMGACC);
	private static float PISTOLSPREAD = (float) Math.toRadians(PISTOLACC);
	
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
		System.out.println(SState.playerCount);
		float ms = (float) (l*.000001);
		
		SState.time = SState.time - (.001f * ms);
		
		if(SState.time <= 0)
		{
			SState.buildMode = !SState.buildMode;
			if(SState.buildMode)
				SState.time = SState.buildTimeMax;
			else
				SState.time = SState.fightTimeMax;
			
			for(SPlayer p : SState.players)
			{
				
				if(p != null)
				{
					p.respawn();
				}
			}
			
		}

		
		for(SPlayer p : SState.players)
		{
			if(p != null && p.getHP() > 0)
			{
				
				if(p.getShotgunCoolDown() > 0.0)
				{
					p.setShotgunCoolDown(p.getShotgunCoolDown() - ms);
				}
				
				if(p.getSmgCoolDown() > 0.0)
				{
					p.setSmgCoolDown(p.getSmgCoolDown() - ms);
					
				}

			boolean easyMove = p.getFixedMoveDir();
			
				
			double rotation = (Math.atan2(p.getMouseY() - (CSLO.GAMEDIM/2), p.getMouseX() - (CSLO.GAMEDIM/2)));
			p.setRot((float) Math.toDegrees(rotation + Math.PI));
		
			if(SState.buildMode == true && p.getMouse1() ){
	
					//Bad way to do thangs but i works
					//TODO -- Copy Code
					int playerXBase = (int) (p.getX() + CPlayer.RADIUS - CSLO.GAMEDIM/2);
					int playerYBase = (int) (p.getY() + CPlayer.RADIUS - CSLO.GAMEDIM/2);
				
					//factor in mouse.
					int deltaX = (p.getMouseX() - ((CSLO.GAMEDIM/2)));
					int deltaY = (p.getMouseY() - ((CSLO.GAMEDIM/2)));
				
					//draw the map
					int mapOffsetX = playerXBase + deltaX;
					int mapOffsetY = playerYBase + deltaY;
				
					//TODO: calculate this better
					int tileX = ((((p.getMouseX() + mapOffsetX) / 8)));
					int tileY = ((((p.getMouseY() + mapOffsetY) / 8)));
				
					
					//contains point? Client Side Aesthetic
					if(SState.map.getTileIntegrity(tileX, tileY) < ((WorldMap.STONEPHASE-2)*WorldMap.STONEPHASESTR) && SState.noPlayerHere(new Rectangle(-1 + 8*tileX, -1 +8*tileY,10,10))   && SState.map.buildPermsission(tileX,tileY,p.getTeam())  && p.withdrawMoney(WALLCOST))
					{
						SState.map.constructTile(tileX, tileY);
					}
				
				
			} else if(p.getMouse1() && p.getEquippedWeapon().getType() == Weapon.WeaponType.SMG && p.getSmgCoolDown() <= 0)
			{
				
				float gunXAdd = (float) (((float) (1.1f*p.getSpeed()*ms) + 2f + p.radius) *  Math.cos(-Math.PI + -Math.toRadians(p.getRot()) +- .38));
				float gunYAdd =  (float) (((float) (1.1f*p.getSpeed()*ms) + 2f + p.radius) * Math.sin(-Math.toRadians(p.getRot()) +- .38));	
		
				//PROBLEM!	double BRotation= (Math.atan2(p.getMouseY() - ((CSLO.GAMEDIM/2) + gunYAdd), p.getMouseX() - ((CSLO.GAMEDIM/2) + gunXAdd)));
			
				//Bullet inaccuracy
				//depends on who fired it.
				float thisBulletheldSpread = (float)( (Math.random()*SMGSPREAD) - (SMGSPREAD/2.0));
		
				SProjectile newP = new SBullet(p.getCenterX()+gunXAdd,p.getCenterY()+gunYAdd,(float)Math.cos(rotation+thisBulletheldSpread)*SMGSPEED,(float)Math.sin(rotation+thisBulletheldSpread)*SMGSPEED,SState.nextBulletId(),(short)(SMGPOWER+(Math.random()*SMGVAR)));
				SState.addProjectile(newP);
				SState.newProj.add(newP);
				p.setSmgCoolDown(SMGCOOLDOWNMAX);
				
				
			} else if(p.getEquippedWeapon().getType() == Weapon.WeaponType.KNIFE)
			{
				//lol do nothing
			
			} else if(p.getMouse1() && !p.isHeldMouse() && p.getEquippedWeapon().getType()  != Weapon.WeaponType.SMG){
		
				if(p.getEquippedWeapon().getType()  == Weapon.WeaponType.RLAUNCHER)
				{	
					SProjectile newP = new SRocket(p.getCenterX(),p.getCenterY(),(float)Math.cos(rotation)*ROCKETSPEED,(float)Math.sin(rotation)*ROCKETSPEED,(float)rotation,SState.nextBulletId(),ROCKETPOWER);	
					SState.addProjectile(newP);
					SState.newProj.add(newP);
				
					//don't want to accidenally blow up the player!
					newP.getShape().setX(newP.getShape().getX() + ((float)Math.cos(rotation) * ((1.1f*p.getSpeed()*ms) + 10f)));
					newP.getShape().setY(newP.getShape().getY() + ((float)Math.sin(rotation) * ((1.1f*p.getSpeed()*ms) + 10f)));
				} else 
				{
				
					float heldSpread = 0;
					float heldSpeed = 0;
					short heldPower = 0;
					short heldVar = 0;
					
					if(p.getEquippedWeapon().getType()  == Weapon.WeaponType.SHOTGUN)
					{
						heldSpread = SHOTGUNSPREAD;
						heldSpeed = SHOTGUNSPEED;
						heldPower = SHOTGUNPOWER;
						heldVar = SHOTGUNVAR;
						
					} else if(p.getEquippedWeapon().getType()  == Weapon.WeaponType.RIFLE) 
					{
						heldSpread = GARANDSPREAD;
						heldSpeed = GARANDSPEED;
						heldPower = GARANDPOWER;
						heldVar = GARANDVAR;
					} else {
						heldSpread = PISTOLSPREAD;
						heldSpeed = PISTOLSPEED;
						heldPower = PISTOLPOWER;
						heldVar = PISTOLVAR;
						
					}
				
				
				//shooting a bullet, have to move it.
				float gunXAdd = (float) (((float) (1.1f*p.getSpeed()*ms) + 2f + p.radius) *  Math.cos(-Math.PI + -Math.toRadians(p.getRot()) +- .38));
				float gunYAdd =  (float) (((float) (1.1f*p.getSpeed()*ms) + 2f + p.radius) * Math.sin(-Math.toRadians(p.getRot()) +- .38));	
		
				//PROBLEM!	double BRotation= (Math.atan2(p.getMouseY() - ((CSLO.GAMEDIM/2) + gunYAdd), p.getMouseX() - ((CSLO.GAMEDIM/2) + gunXAdd)));
			
				//Bullet inaccuracy
				//depends on who fired it.
				float thisBulletheldSpread = (float)( (Math.random()*heldSpread) - (heldSpread/2.0));
				
				if(p.getEquippedWeapon().getType()  !=Weapon.WeaponType.SHOTGUN)
				{
					SProjectile newP = new SBullet(p.getCenterX()+gunXAdd,p.getCenterY()+gunYAdd,(float)Math.cos(rotation+thisBulletheldSpread)*heldSpeed,(float)Math.sin(rotation+thisBulletheldSpread)*heldSpeed,SState.nextBulletId(),(short)(heldPower+(Math.random()*heldVar)));
					SState.addProjectile(newP);
					SState.newProj.add(newP);
				}
				
				if(p.getEquippedWeapon().getType() == Weapon.WeaponType.SHOTGUN && p.getShotgunCoolDown() <= 0.0)
				{
					for(int i = 0; i != SHOTGUNPELLETS; i ++)
					{
						thisBulletheldSpread = (float) ((Math.random()*heldSpread) - (heldSpread/2.0));
						SProjectile newPs = new SBullet(p.getCenterX()+gunXAdd,p.getCenterY()+gunYAdd,(float)Math.cos(rotation+thisBulletheldSpread)*heldSpeed,(float)Math.sin(rotation+thisBulletheldSpread)*heldSpeed,SState.nextBulletId(),(short)(heldPower+(Math.random()*heldVar)));
						SState.addProjectile(newPs);
						SState.newProj.add(newPs);
					}
					p.setShotgunCoolDown(SHOTGUNCOOLDOWNMAX);
					
				}
				
				
			}
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

			
			daos.writeBoolean(SState.buildMode);
			daos.writeShort((short)SState.time);
			
			daos.writeByte(SState.playerCount);
			
			for(SPlayer p : SState.players){
				if(p != null)
				{

					daos.writeByte(p.getHP());
					daos.writeFloat(p.getX());
					daos.writeFloat(p.getY());
					daos.writeFloat(p.getRot());
					daos.writeByte(p.getFrame());
					daos.writeBoolean(p.getTeam());
					daos.writeShort(p.getMoney());
					
					for(int i = 0; i != 6; i++)
					{
						daos.writeShort(p.sendColorArray()[i]);
					}

					//Save space by not transmitting null weapons.
					for(int i = 0; i != 7; i++)
					{
						Weapon[] w = p.getWeapons();
						
						if(w[i] == null)
						{
							daos.writeByte(-1);
						} else
						{
							daos.writeByte(w[i].getType().ordinal());
							daos.writeByte(w[i].getMagsLeft());
							daos.writeByte(w[i].getBulletsLeft());
						}
					}
					daos.writeByte(p.getWeaponDraw().ordinal());
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
		} else if(clientID == CSLO.BUYREQUEST){
			byte trueid = dais.readByte();
			byte weapon = dais.readByte();
			//give a weapon for weaponPointer.
			short cost = 0;
			
			switch(weapon)
			{
				case 0:cost = 0;break;
				case 1:cost = PISTOLCOST;break;
				case 2:cost = SMGCOST;break;
				case 3:cost = SHOTGUNCOST;break;
				case 4:cost = SMGCOST;break;
				case 5:cost = GARANDCOST;break;
				case 6:cost = ROCKETCOST;break;
			}
			
			//If can afford and don't own weapons
			if(SState.players[trueid].getWeapons()[weapon] == null && SState.players[trueid].withdrawMoney(cost) )
				SState.players[trueid].getWeapons()[weapon] = new Weapon(Weapon.WeaponType.values()[weapon],(byte)0,(byte)0);
			
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
		p.setWeaponPointer(dais.readByte());
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
		//load map based on what is chosen by the server.
		SState.map = new WorldMap("data/maps/Map2.tmx");
		//then, when a client joins, tell them the map.
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