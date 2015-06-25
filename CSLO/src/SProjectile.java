import org.newdawn.slick.geom.Shape;


public abstract class SProjectile{

	private float xVel;
	private float yVel;
	//holds position and shape of bullet
	private Shape shape;
	//is the bullet live? (able to hit people)
	private boolean live = true;
	private short id;
	
	public SProjectile(Shape s, float xVel, float yVel, short id) 
	{
		this.shape = s;
		this.xVel = xVel;
		this.yVel = yVel;
		this.id = id;
	}
	
	//propagate logic
	public void moveBullet(float ms)
	{
		shape.setX(shape.getX() + (xVel*ms));
		shape.setY(shape.getY() + (yVel*ms));
		live = !SState.map.checkCollide(getShape());
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
	
	public short getID(){
		return id;
	}
	
	public float getXVel(){
		return xVel;
	}
	public float getYVel(){
		return yVel;
	}

	
}
