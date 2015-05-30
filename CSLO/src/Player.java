import org.newdawn.slick.geom.Circle;


@SuppressWarnings("serial")
public class Player extends Circle {
	private int mouseX;
	private int mouseY;
	private final static int RADIUS = 12;
	
	private boolean moveW;
	private boolean moveA;
	private boolean moveS;
	private boolean moveD;
	private boolean mouse1;
	private boolean mouse2;

	public Player(){
		super(-1, -1, RADIUS);
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
	public void setMouse1(byte b) {
		mouse1 = (b != 0);
	}
	public void setMouse2(byte b) {
		mouse2 = (b != 0);
	}

}
