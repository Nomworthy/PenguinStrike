
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;


@SuppressWarnings("serial")
public class Player extends Circle {
	private int mouseX;
	private int mouseY;
	private final static int RADIUS = 12;
	private Animation sprite;
	
	private boolean moveW;
	private boolean moveA;
	private boolean moveS;
	private boolean moveD;
	private boolean mouse1;
	private boolean mouse2;
	
	private int xOffset = 4;
	private int yOffset = 4;

	public Player(){
		super(0, 0, RADIUS);
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

	public void setMoveW(byte b) {
		moveW = (b != 0);
	}
	public void setMoveA(byte b) {
		moveA = (b != 0);
	}
	public void setMoveS(byte b) {
		moveS = (b != 0);
	}
	public void setMoveD(byte b) {
		moveD = (b != 0);
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
	
	public void setMouse1(byte b) {
		mouse1 = (b != 0);
	}
	public void setMouse2(byte b) {
		mouse2 = (b != 0);
	}
	
	public void draw(Graphics g, int worldX, int worldY){
		if(moveW || moveA || moveS || moveD){
			sprite.setAutoUpdate(true);
		} else {
			sprite.setCurrentFrame(0);
		}

		Image i =sprite.getCurrentFrame();
		i.setRotation((float)(180 +(Math.toDegrees(Math.atan2(mouseY - (getCenterY() - worldY),mouseX - (getCenterX() - worldX))))));
		g.drawAnimation(sprite, 300,300);
		
		g.drawImage(i,x +- xOffset +- worldX, y +- yOffset +- worldY);
		g.setColor(Color.orange);
		System.out.println(x);
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
}
