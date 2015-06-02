
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

//all the client knows about a player.
public class CPlayer{
	//Offset to the top left circle of penguin.
	private int xOffset = 4;
	private int yOffset = 4;
	//BAD STYLE:RADIUS NEEDS DEFINED FOR SERVER AND THIS
	public final int RADIUS = 12;
	private float rotation;
	private int frame;
	private float x;
	private float y;
	private Animation sprite;

	public CPlayer(){
		try {
			sprite = new Animation(new SpriteSheet("data/penguins/penguin.png",31,31),100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setX(float x){
		this.x = x;
	}
	
	public void setY(float y){
		this.y = y;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	
	public void setFrame(int f){
		frame = f;
	}
	
	public void setRotation(float r){
		rotation = r;
	}
	
	public void draw(Graphics g, int wx, int wy) {
		sprite.setCurrentFrame(frame);
		Image i =sprite.getCurrentFrame();
		i.setRotation(rotation);
		g.drawImage(i,x +- xOffset +- wx, y +- yOffset +- wy);
	}
}
