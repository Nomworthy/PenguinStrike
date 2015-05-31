import org.newdawn.slick.geom.Circle;

public class Bullet extends Projectile {
	public final static int bulletSize = 2;
	public final static int offSet = 1;
	public Bullet(float xCenter, float yCenter, float xVel, float yVel) {
		super(new Circle(xCenter,yCenter,bulletSize), xVel, yVel);
	}

	@Override
	public void onCollide() {
	}

}
