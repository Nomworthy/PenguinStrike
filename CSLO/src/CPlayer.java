
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

//all the client knows about a player.
public class CPlayer{
	
	//Offset to the top left circle of penguin.
	private int xOffset = 5;
	private int yOffset = 5;
	
	public final int RADIUS = 11;
	private float rotation;
	private int frame;
	private float x;
	private float y;
	private Animation spriteJacket;
	private Animation spriteHands;
	private Animation spriteUnderShirt;
	private Animation spriteHead;
	private Animation spriteWeapon;
	
	private Color jacketCol;
	private Color underShirtCol;
	
	private boolean oldTeam = false;
	
	public CPlayer(){
		try {
			spriteJacket = new Animation(new SpriteSheet("data/chars/thJacket.png",31,31),100);
			spriteHands = new Animation(new SpriteSheet("data/chars/thHands.png",31,31),100);
			spriteUnderShirt = new Animation(new SpriteSheet("data/chars/thUndershirt.png",31,31),100);
			//depends!
			spriteHead = new Animation(new SpriteSheet("data/chars/wBear.png",31,31),100);
			spriteWeapon = new Animation(new SpriteSheet("data/chars/garand.png",31,31),100);
			
			jacketCol = new Color(0f,0f,0f);
			underShirtCol = new Color(0f,0f,0f);
			
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
		spriteWeapon.setCurrentFrame(frame);
		
		Image j =spriteJacket.getCurrentFrame();
		Image ha =spriteHands.getCurrentFrame();
		Image u =spriteUnderShirt.getCurrentFrame();
		Image he =spriteHead.getCurrentFrame();
		Image w =spriteWeapon.getCurrentFrame();
		
		j.setRotation(rotation);
		ha.setRotation(rotation);
		u.setRotation(rotation);
		he.setRotation(rotation);
		w.setRotation(rotation);
		
		
		g.drawImage(j,x +- xOffset +- wx, y +- yOffset +- wy,jacketCol);
		g.drawImage(ha,x +- xOffset +- wx, y +- yOffset +- wy);
		g.drawImage(u,x +- xOffset +- wx, y +- yOffset +- wy,underShirtCol);
		g.drawImage(he,x +- xOffset +- wx, y +- yOffset +- wy);
		g.drawImage(w,x +- xOffset +- wx, y +- yOffset +- wy);
		
	}
	
	public void setCol(short s, short t, short u, short v, short w, short z)
	{
		jacketCol = new Color(s,t,u);
		underShirtCol = new Color(v,w,z);
	}
	
	public void setTeam(boolean team)
	{

		if(team == oldTeam) return;
		
		try {
		if(team)
		{
				spriteHead = new Animation(new SpriteSheet("data/chars/wPenguin.png",31,31),100);
		} else {
				spriteHead = new Animation(new SpriteSheet("data/chars/wBear.png",31,31),100);
		}
		
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
