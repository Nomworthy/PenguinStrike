import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class CWeapon {
	
	private byte bulletsLeft;
	private byte magsLeft;
	private WeaponType wType;
	
	public enum WeaponType
	{
		KNIFE,
		PISTOL,
		SMG,
		SHOTGUN,
		RIFLE,
		RLAUNCHER,
		BANDAGE,
		GRENADE
	}

	public static Image knife;
	public static Image pistol;
	public static Image smg;
	public static Image shotgun;
	public static Image rifle;
	public static Image rLauncher;
	public static Image bandage;
	public static Image grenade;
	
	public static void load()
	{
		try {
			knife = new Image("data/weapon/grd_knife.png");
			knife.setFilter(Image.FILTER_NEAREST);
			pistol = new Image("data/weapon/grd_pistol.png");
			pistol.setFilter(Image.FILTER_NEAREST);
			smg = new Image("data/weapon/grd_smg.png");
			smg.setFilter(Image.FILTER_NEAREST);
			shotgun = new Image("data/weapon/grd_shotgun.png");
			shotgun.setFilter(Image.FILTER_NEAREST);
			rifle = new Image("data/weapon/grd_rifle.png");
			rifle.setFilter(Image.FILTER_NEAREST);
			rLauncher= new Image("data/weapon/grd_rlauncher.png");
			rLauncher.setFilter(Image.FILTER_NEAREST);
			grenade = new Image("data/weapon/grd_grenade.png");
			grenade.setFilter(Image.FILTER_NEAREST);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	
	public CWeapon(WeaponType w, byte b, byte m )
	{
		bulletsLeft = b;
		magsLeft = m;
		wType = w;
	}
	
	public void draw(int x, int y) {
		
		switch (wType)
		{
		//case BANDAGE:
	//		bandage.draw(x,y);
		//	break;
		case GRENADE:
			grenade.draw(x,y);
			break;
		case KNIFE:
			knife.draw(x,y);
			break;
		case PISTOL:
			pistol.draw(x,y);
			break;
		case RIFLE:
			rifle.draw(x,y);
			break;
		case RLAUNCHER:
			rLauncher.draw(x,y);
			break;
		case SHOTGUN:
			shotgun.draw(x,y);
			break;
		case SMG:
			smg.draw(x,y);
			break;
		default:
			break;
		
		
		}
		
	}
	
}
