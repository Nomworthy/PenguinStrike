
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;


@SuppressWarnings("serial")
//representaion of a penguin
//TODO: Divide into client and self
public class Player extends Circle {
	//this player's mouse X and Y.
	private int mouseX;
	private int mouseY;
	//the radius.
	private final static int RADIUS = 12;
	private Animation sprite;
	
	private boolean moveW;
	private boolean moveA;
	private boolean moveS;
	private boolean moveD;
	private boolean mouse1;
	private boolean mouse2;
	
	private boolean heldMouse;
	
	//Offset to the top left circle of penguin.
	private int xOffset = 4;
	private int yOffset = 4;
	
	//TODOO
	public float rotation;
	public int myFrame;

	
	public Player(){
		super(500, 500, RADIUS);
		try {
			sprite = new Animation(new SpriteSheet("data/penguins/penguin.png",31,31),100);
		} catch (Exception e) {
		}
	}

	public int getMouseX() {
		return mouseX;
	}

	public void setMouseX(int mouseX) {
		this.mouseX = mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public void setMouseY(int mouseY) {
		this.mouseY = mouseY;
	}

	
	public void setMoveW(boolean b) {
		moveW = b;
	}
	public void setMoveA(boolean b) {
		moveA = b;
	}
	public void setMoveS(boolean b) {
		moveS = b;
	}
	public void setMoveD(boolean b) {
		moveD = b;
	}
	
	public void setMouse1(boolean b) {
		mouse1 = b;
	}
	public void setMouse2(boolean b) {
		mouse2 = b;
	}
	
	public boolean getMouse1() {
		return mouse1;
	}
	public boolean getMouse2() {
		return mouse2;
	}
	
	public void drawInterpolate(Graphics g, int worldX, int worldY){
		if(moveW || moveA || moveS || moveD){
			sprite.setAutoUpdate(true);
		} else {
			sprite.setCurrentFrame(0);
		}

		Image i =sprite.getCurrentFrame();
		i.setRotation((float)(180 +(Math.toDegrees(Math.atan2(mouseY - (getCenterY() - worldY),mouseX - (getCenterX() - worldX))))));
		g.drawAnimation(sprite, CSLO.GAMEDIM,CSLO.GAMEDIM);
		
		g.drawImage(i,x +- xOffset +- worldX, y +- yOffset +- worldY);
		g.setColor(Color.orange);
		g.drawRect(x,y, 1, 1);
		g.draw(new Circle(-worldX ,-worldY  , RADIUS));
	}

	public boolean getMoveW() {
		return moveW;
	}
	
	public boolean getMoveA() {
		return moveA;
	}
	
	public boolean getMoveS() {
		return moveS;
	}
	
	public boolean getMoveD() {
		return moveD;
	}

	public float getSpeed() {
		return .1f;
	}

	public boolean isHeldMouse() {
		return heldMouse;
	}

	public void setHeldMouse(boolean heldMouse) {
		this.heldMouse = heldMouse;
	}

	
//TODOOOO
	public void drawOtherClient(Graphics g, int wx, int wy) {
		sprite.setCurrentFrame(myFrame);
		Image i =sprite.getCurrentFrame();
		i.setRotation(rotation);
		g.drawImage(i,x +- xOffset +- wx, y +- yOffset +- wy);
	}

}
