public class CState {
	//We got some CPlayers: holding X,Y,Rotation,Animation.
	public static CPlayer players[];
	//We have a WorldMap: Has tile states and such.
	public static WorldMap worldMap;
	//We have bullets
	public static int scaledMouseX;
	public static int scaledMouseY;
	public static boolean moveW;
	public static boolean moveA;
	public static boolean moveS;
	public static boolean moveD;
	public static boolean mouse1;
	public static boolean mouse2;
	
	public static CWeapon[] inventory;
	public static int invPointer = 1;
	
	public static byte  hp = 100;
	public static byte  ammo1 = 0;
	public static byte  ammo2 = 0;
	public static short money = 1000;
	
	public static void initInventory()
	{
		inventory = new CWeapon[7];
		inventory[0] = new CWeapon(CWeapon.WeaponType.KNIFE,(byte)0,(byte)0);
		inventory[1] = new CWeapon(CWeapon.WeaponType.PISTOL,(byte)6,(byte)3);
		inventory[2] = new CWeapon(CWeapon.WeaponType.SMG,(byte)30,(byte)3);
		inventory[3] = new CWeapon(CWeapon.WeaponType.SHOTGUN,(byte)5,(byte)20);
		inventory[4] = new CWeapon(CWeapon.WeaponType.RIFLE,(byte)10,(byte)3);
		inventory[5] = new CWeapon(CWeapon.WeaponType.RLAUNCHER,(byte)1,(byte)3);
		inventory[6] = new CWeapon(CWeapon.WeaponType.BANDAGE,(byte)1,(byte)2);
	}
}
