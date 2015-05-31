import org.newdawn.slick.geom.Shape;


public abstract class Projectile{

	private float xVel;
	private float yVel;
	private Shape shape;
	private boolean live = true;
	
	public Projectile(Shape s, float xVel, float yVel) 
	{
		this.shape = s;
		this.xVel = xVel;
		this.yVel = yVel;
	}
	
	public void moveBullet(float ms){
		shape.setX(shape.getX() + (xVel*ms));
		shape.setY(shape.getY() + (yVel*ms));
		//TODO: Check for collisions.
		live = !WorldState.map.checkCollide(shape);
	}

	public abstract void onCollide();
	public Shape getShape(){
		return shape;
	}

	public boolean isLive() {
		return live;
	}
}
