import java.util.LinkedList;
import java.util.ListIterator;

import org.newdawn.slick.SlickException;

public class WorldState {
	public static WorldMap map;
	public static Player[] players = new Player[4];
	public static LinkedList<Projectile> proj = new LinkedList<Projectile>();
	
	public static void instWorldState(){
    //	try {
			//WorldState.map = new WorldMap("data/maps/Map1.tmx");
	//	} catch (SlickException e) {
	//		e.printStackTrace();
	//	}
		for(int i = 0; i != players.length; i++){
			players[i] = new Player();
		}
	}
	
	public static void physics(float ms){
		ListIterator<Projectile> listIterator = proj.listIterator();
		while (listIterator.hasNext()) {
			Projectile p = listIterator.next();
			p.moveBullet(ms);
			if(!p.isLive())
				listIterator.remove();
		}
	}

	public static void addProjectile(Projectile p) {
		proj.add(p);
	}
}
