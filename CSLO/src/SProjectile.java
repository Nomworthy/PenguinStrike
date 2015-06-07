import org.newdawn.slick.geom.Shape;


public abstract class SProjectile{

	//self explaining
	private float xVel;
	private float yVel;
	//holds position and shape of bullet
	private Shape shape;
	//is the bullet live? (able to hit people)
	private boolean live = true;
	
	public SProjectile(Shape s, float xVel, float yVel) 
	{
		this.shape = s;
		this.xVel = xVel;
		this.yVel = yVel;
	}
	
	//propagate logic
	public void moveBullet(float ms)
	{
		shape.setX(shape.getX() + (xVel*ms));
		shape.setY(shape.getY() + (yVel*ms));
		live = !SState.map.checkCollide(shape);
	}

	//what to do when bullet hits someone
	public abstract void onCollide();
	
	//shape of bullet.
	public Shape getShape(){
		return shape;
	}

	public boolean isLive() {
		return live;
	}
}
