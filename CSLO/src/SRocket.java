import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;

public class SRocket extends SProjectile {
	public final static int rocketWidth= 20;
	public final static int rocketHeight= 8;
	public final static double damage = 2000;
	//private this
	public float rotation;
	
	public SRocket(float x, float y, float xVel, float yVel, float angle,short ID) {
		super((new Rectangle(x,y,rocketWidth,rocketHeight)) .transform(Transform.createRotateTransform(angle,x, y)), xVel, yVel,ID);
		rotation = angle;
	}

	@Override
	public void onCollide() {

		int cX = (int) ((( getShape().getCenterX() + ((rocketWidth/2)*Math.cos(rotation))) /SState.map.TILESIZE));
		int cY = (int) ((( getShape().getCenterY() + ((rocketWidth/2)*Math.sin(rotation))) /SState.map.TILESIZE));
		
		for(int x = cX-2; x <= cX+2; x++)
		{
			for(int y = cY-2; y <= cY+2;y++)
			{
				SState.map.damageTile(x,y,damage);
				
			}
		}
		
	}

}
