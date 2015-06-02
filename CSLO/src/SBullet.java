import org.newdawn.slick.geom.Circle;

public class SBullet extends SProjectile {
	public final static int bulletSize = 2;
	public SBullet(float xCenter, float yCenter, float xVel, float yVel) {
		super(new Circle(xCenter,yCenter,bulletSize), xVel, yVel);
	}

	@Override
	public void onCollide() {
	}

}
