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
	
	public static int invPointer = 1;
	
	public static byte  hp = 100;
	
	//Doesnt make sense!
	public static byte  ammo1 = 0;
	public static byte  ammo2 = 0;
	
	
	public static boolean buildMode;
	public static short time;
	
}
