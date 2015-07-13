
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;

@SuppressWarnings("serial")
//Representation of a penguin
public class SPlayer extends Circle {
	
	//this player's mouse X and Y.
	private int mouseX;
	private int mouseY;
	
	//the radius. TODO!
	private final static int RADIUS = 11;
	
	private boolean moveW;
	private boolean moveA;
	private boolean moveS;
	private boolean moveD;
	private boolean mouse1;
	private boolean mouse2;
	private boolean heldMouse;
	
	private boolean fixedDirMode;
	
	private float rotation;
	private int frame;
	private double frameTimeLeft;
	private final double frameTimeLeftMax = 100;
	
	private boolean spectator = true;
	private boolean team;
	
	private short r;
	private short g;
	private short b;
	
	private short sr;
	private short sg;
	private short sb;

	
	public SPlayer(boolean c, short r, short g, short b, short sr, short sg, short sb){
		super(0, 0, RADIUS);
		this.fixedDirMode = c;
		this.r = r;
		this.g =g;
		this.b = b;
		this.sr = sr;
		this.sg = sg;
		this.sb = sb;
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
	
	public void setRot(float f){
		rotation = f;
	}
	
	public void setFrame(int f){
		frame = f;
	}
	public boolean getMouse1() {
		return mouse1;
	}
	public boolean getMouse2() {
		return mouse2;
	}
	
	public float getRot() {
		return rotation;
	}
	public int getFrame() {
		return frame;
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

	public void doFrameLogic(double ms){
		if(frameTimeLeft < 0){
			frame = ++frame % 4;
			frameTimeLeft = frameTimeLeftMax;
		}
		frameTimeLeft = frameTimeLeft - ms;
	}
	
	public void setHeldMouse(boolean heldMouse) {
		this.heldMouse = heldMouse;
	}

	public void setCoords(float newX, float newY){

		float oldX = this.getX();
		float oldY = this.getY();
		
		super.setX(newX);
		super.setY(newY);
		
		if(SState.map.checkCollide(this)){
			//failure!
			super.setX(oldX);
			super.setY(oldY);
		} else {
			return;
		}
		
		//try Y
		super.setY(newY);
		
		if(SState.map.checkCollide(this)){
			//fail!
			super.setY(oldY);
		} else {
			return;
		}
		
		//try X
		super.setX(newX);
		
		if(SState.map.checkCollide(this)){
			//fail!
			super.setX(oldX);
		} else {
			return;
		}
	}

	public void setTeam(boolean readBoolean) {
		spectator = false;
		team = readBoolean;
		if(team)
		{
			
			super.setX(Server.homeSpawnX);
			super.setY(Server.homeSpawnY);
			
		} else
		{
			
			super.setX(Server.awaySpawnX);
			super.setY(Server.awaySpawnY);
			
		}
	}
	
	public boolean isSpectator()
	{
		return spectator;
	}
	
	public boolean getTeam()
	{
		return team;
	}

	public short[] sendColorArray(){
		return new short[]{r,g,b,sr,sg,sb};
	}

	public void die() {
		if(team)
		{
		super.setX(Server.homeSpawnX);
		super.setY(Server.homeSpawnY);
		} else 
		{
		super.setX(Server.awaySpawnX);
		super.setY(Server.awaySpawnY);
		}
		
		
	}

	public boolean getFixedMoveDir() {
		return fixedDirMode;
	}
	
}
