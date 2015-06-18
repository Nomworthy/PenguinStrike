import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;

public class SRocket extends SProjectile {
	public final static int rocketWidth= 20;
	public final static int rocketHeight= 8;
	//private this
	public float rotation;
	
	public SRocket(float x, float y, float xVel, float yVel, float angle,short ID) {
		super((new Rectangle(x,y,rocketWidth,rocketHeight)) .transform(Transform.createRotateTransform(angle,x, y)), xVel, yVel,ID);
		rotation = angle;
	}

	@Override
	public void onCollide() {
	}

}
