import java.util.LinkedList;
import java.util.ListIterator;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Shape;

public class SState {
	public static WorldMap map;
	public static SPlayer[] players = new SPlayer[CSLO.maxPlayerCount];
	public static int playerCount = 0;
	public static LinkedList<SProjectile> proj = new LinkedList<SProjectile>();
	public static LinkedList<SProjectile> newProj = new LinkedList<SProjectile>();
	public static LinkedList<SProjectile> oldProj = new LinkedList<SProjectile>();
	public static short bulletCounter = 0;
	
	public static final float buildTimeMax = 60;
	public static final float fightTimeMax = 60;
	
	public static boolean buildMode = true;
	public static float time = 10;
	
	public static void physics(float ms){
		ListIterator<SProjectile> listIterator = proj.listIterator();
		while (listIterator.hasNext()) {
			SProjectile p = listIterator.next();
			p.moveBullet(ms);
			if(!p.isLive()){
				p.onCollide();
				oldProj.add(p);
				listIterator.remove();
			}
		}
	}

	public static void addProjectile(SProjectile p) {
		proj.add(p);
	}
	
	public static short nextBulletId(){
		return bulletCounter++;
	}
	

	public static boolean noPlayerHere(Shape shape)
	{
		for(SPlayer p : SState.players)
		{
			if(p != null)
			{
				if(p.intersects(shape))
				{
					return false;
				}
			}
		}
		return true;
	}
}
