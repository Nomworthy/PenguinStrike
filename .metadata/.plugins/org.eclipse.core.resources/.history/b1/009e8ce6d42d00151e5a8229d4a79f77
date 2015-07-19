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
		//the bigger amount of pixels to move.
		float steps = (Math.abs(xVel*ms) > Math.abs(yVel*ms)) ? Math.abs(xVel*ms) : Math.abs(yVel*ms);
		
		steps += 1;
		
		float deltaX = (xVel*ms) / steps;
		float deltaY = (yVel*ms) / steps;


		for(int i = 0; i != (int)(steps+1); i++)
		{
			shape.setX(shape.getX() + deltaX);
			shape.setY(shape.getY() + deltaY);
		
			for(SPlayer p : SState.players)
			{
	
				if(p != null)
				{
					if(p.intersects(shape))
					{
						p.die();
						live = false;
						return;
					}
				}
			}
			
			if(SState.map.checkCollide(getShape()))
			{
				live = false;
				return;
			}
		}
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
