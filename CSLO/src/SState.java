import java.util.LinkedList;
import java.util.ListIterator;

import org.newdawn.slick.SlickException;

public class SState {
	public static WorldMap map;
	public static SPlayer[] players = new SPlayer[4];
	public static LinkedList<SProjectile> proj = new LinkedList<SProjectile>();
	
	public static void instWorldState(){
    //	try {
			//WorldState.map = new WorldMap("data/maps/Map1.tmx");
	//	} catch (SlickException e) {
	//		e.printStackTrace();
	//	}
		for(int i = 0; i != players.length; i++){
			players[i] = new SPlayer();
		}
	}
	
	public static void physics(float ms){
		ListIterator<SProjectile> listIterator = proj.listIterator();
		while (listIterator.hasNext()) {
			SProjectile p = listIterator.next();
			p.moveBullet(ms);
			if(!p.isLive())
				listIterator.remove();
		}
	}

	public static void addProjectile(SProjectile p) {
		proj.add(p);
	}
}
