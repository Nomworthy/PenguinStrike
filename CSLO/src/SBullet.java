import org.newdawn.slick.geom.Circle;

public class SBullet extends SProjectile {
	public final static int bulletSize = 2;
	private double wallDamage = 100;
	
	public SBullet(float xCenter, float yCenter, float xVel, float yVel, short ID) {
		super(new Circle(xCenter,yCenter,bulletSize), xVel, yVel,ID);
	}

	@Override
	public void onCollide() {
		int minX = (int) (getShape().getMinX()/SState.map.TILESIZE);
		int maxX = (int) (getShape().getMaxY()/SState.map.TILESIZE);
		int minY = (int) (getShape().getMinY()/SState.map.TILESIZE);
		int maxY = (int) (getShape().getMaxY()/SState.map.TILESIZE);
		
		SState.map.damageTile(minX, minY, wallDamage);
		SState.map.damageTile(maxX, minY, wallDamage);
		SState.map.damageTile(minX, maxY, wallDamage);
		SState.map.damageTile(maxX, maxY, wallDamage);
		
	}

}
