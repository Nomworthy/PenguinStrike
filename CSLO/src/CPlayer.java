
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
	
	public final static int RADIUS = 11;
	private float rotation;
	private int frame;
	private float x;
	private float y;
	private Animation spriteJacket;
	private Animation spriteUnderShirt;
	private Animation spriteHead;
	
	private Image death;
	
	private Animation spriteWeapon;
	
	private Animation[] weapons;
	
	private Color jacketCol;
	private Color underShirtCol;
	
	private boolean oldTeam = false;
	
	private byte HP;
	
	private short money;
	
	private Weapon[] inventory;
	
	public CPlayer(){
		try {
			spriteJacket = new Animation(new SpriteSheet("data/chars/thJacket.png",31,31),100);
			spriteUnderShirt = new Animation(new SpriteSheet("data/chars/thUndershirt.png",31,31),100);
			//depends!
			spriteHead = new Animation(new SpriteSheet("data/chars/thBear.png",31,31),100);
			spriteWeapon = new Animation(new SpriteSheet("data/chars/garand.png",31,31),100);
			
			weapons = new Animation[7];
			//harcoded
			weapons[0] = new Animation(new SpriteSheet("data/chars/knife.png",31,31),100);
			weapons[1] = new Animation(new SpriteSheet("data/chars/pistol.png",31,31),100);
			weapons[2] = new Animation(new SpriteSheet("data/chars/smg.png",31,31),100);
			weapons[3] = new Animation(new SpriteSheet("data/chars/shotgun.png",31,31),100);
			weapons[4] = new Animation(new SpriteSheet("data/chars/garand.png",31,31),100);
			weapons[5] = new Animation(new SpriteSheet("data/chars/rocketLauncher.png",31,31),100);
			
			
			
			jacketCol = new Color(0f,0f,0f);
			underShirtCol = new Color(0f,0f,0f);
			
			inventory = new Weapon[]{null,null,null,null,null,null,null};
			

			death = new Image("data/chars/dead.png");
			
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

		try
		{
			
			if(HP > 0)
			{

			//Temp: drawArc is drawn with repect to current resolution
			g.setLineWidth(3f);
			g.setColor(Color.red);
			g.drawArc(x +- xOffset +- wx + (RADIUS/2),y +- yOffset +- wy + (RADIUS/2),RADIUS*2, RADIUS*2,0,360 * (HP/100f));
			g.setLineWidth(1f);
			
			spriteJacket.setCurrentFrame(frame);
			spriteHead.setCurrentFrame(frame);
			spriteUnderShirt.setCurrentFrame(frame);
			spriteWeapon.setCurrentFrame(frame);
		
			Image j =spriteJacket.getCurrentFrame();
			Image u =spriteUnderShirt.getCurrentFrame();
			Image he =spriteHead.getCurrentFrame();
			Image w =spriteWeapon.getCurrentFrame();
		
			j.setRotation(rotation);
			u.setRotation(rotation);
			he.setRotation(rotation);
			w.setRotation(rotation);
			
		
			g.drawImage(j,x +- xOffset +- wx, y +- yOffset +- wy,jacketCol);
			g.drawImage(u,x +- xOffset +- wx, y +- yOffset +- wy,underShirtCol);
			g.drawImage(he,x +- xOffset +- wx, y +- yOffset +- wy);
			g.drawImage(w,x +- xOffset +- wx, y +- yOffset +- wy);
			
			} else {
				g.drawImage(death,x +- xOffset +- wx, y +- yOffset +- wy);
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
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
				spriteHead = new Animation(new SpriteSheet("data/chars/thPenguin.png",31,31),100);
		} else {
				spriteHead = new Animation(new SpriteSheet("data/chars/thBear.png",31,31),100);
		}
		
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void setHP(byte readByte) {
		HP = readByte;
	}
	
	public byte getHP() {
		return HP;
	}
	
	public short getMoney()
	{
		return money;
	}
	
	public void setMoney(short m)
	{
		money = m;
	}

	public void setWeaponDraw(byte readByte) {
		System.out.println(readByte);
		spriteWeapon = weapons[readByte];
	}

	public Weapon[] getInventory() {
		return inventory;
	}
}
