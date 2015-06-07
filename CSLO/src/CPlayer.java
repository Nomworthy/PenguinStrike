
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

//all the client knows about a player.
public class CPlayer{
	//Offset to the top left circle of penguin.
	private int xOffset = 5;
	private int yOffset = 5;
	//BAD STYLE:RADIUS NEEDS DEFINED FOR SERVER AND THIS
	public final int RADIUS = 11;
	private float rotation;
	private int frame;
	private float x;
	private float y;
	private Animation spriteJacket;
	private Animation spriteHands;
	private Animation spriteUnderShirt;
	private Animation spriteHead;

	private Color jacketCol;
	private Color underShirtCol;
	
	public CPlayer(){
		try {
			spriteJacket = new Animation(new SpriteSheet("data/penguins/wJacket.png",31,31),100);
			spriteHands = new Animation(new SpriteSheet("data/penguins/wHands.png",31,31),100);
			spriteUnderShirt = new Animation(new SpriteSheet("data/penguins/wUnder.png",31,31),100);
			spriteHead = new Animation(new SpriteSheet("data/penguins/wHead.png",31,31),100);
			jacketCol = new Color((float)Math.random(),(float)Math.random(),(float)Math.random());
			underShirtCol = new Color((float)Math.random(),(float)Math.random(),(float)Math.random());
			
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

		spriteJacket.setCurrentFrame(frame);
		spriteHead.setCurrentFrame(frame);
		spriteUnderShirt.setCurrentFrame(frame);
		spriteHands.setCurrentFrame(frame);
		Image j =spriteJacket.getCurrentFrame();
		Image ha =spriteHands.getCurrentFrame();
		Image u =spriteUnderShirt.getCurrentFrame();
		Image he =spriteHead.getCurrentFrame();
		j.setRotation(rotation);
		ha.setRotation(rotation);
		u.setRotation(rotation);
		he.setRotation(rotation);
		
		
		g.drawImage(j,x +- xOffset +- wx, y +- yOffset +- wy,jacketCol);
		g.drawImage(ha,x +- xOffset +- wx, y +- yOffset +- wy);
		g.drawImage(u,x +- xOffset +- wx, y +- yOffset +- wy,underShirtCol);
		g.drawImage(he,x +- xOffset +- wx, y +- yOffset +- wy);
	}
}
