import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;




public class CBuyMenu {

	
	public static void draw(Graphics g)
	{

		g.setColor(new Color(.5f,.5f,1f,.9f));
		g.fillRect(50, 50, 300, 300);
		
		g.setColor(Color.black);
		

		CWFont.draw(g, "STORAGE", 271, 51, 1, Color.black);


		CWFont.draw(g, "NOT IMPLEMENTED", 251, 61, 1, Color.black);
		
		/**
		CWFont.draw(g, "CORBAIN      x99", 251, 61, 1, Color.black);
		CWFont.draw(g, "R. LAUNCHER  x99", 251, 71, 1, Color.black);
		CWFont.draw(g, "UZI          x99", 251, 81, 1, Color.black);
		CWFont.draw(g, "VEDDER       x99", 251, 91, 1, Color.black);
		CWFont.draw(g, "9MM MAGAZINE x99", 251, 101, 1, Color.black);
		CWFont.draw(g, "SHOTGUNSHELL x99", 251, 111, 1, Color.black);
		CWFont.draw(g, "USSR GRENADE x99", 251, 121, 1, Color.black);
		CWFont.draw(g, "TM47         x99", 251, 131, 1, Color.black);
		*/
		
		g.setColor(Color.black);
		g.drawRect(250,50,100,299);
		g.drawRect(50,300,200,50);
		g.drawRect(50,300,200,10);
		
		g.drawRect(60, 60, 180, 25);

		g.drawRect(60, 90, 180, 25);
		
		g.drawRect(60, 120, 180, 25);

		g.drawRect(60, 150, 180, 25);
		
		g.drawRect(60, 180, 180, 25);
		
		g.drawRect(60, 210, 180, 25);

		g.drawRect(60, 240, 180, 25);
		
		//g.drawRect(60, 270, 180, 25);
		
		CWFont.draw(g, "DESCRIPTION", 121, 301, 1, Color.black);

		CWFont.draw(g, "KNIFE    $20", 70, 67 + (0*30), 2, Color.black);
		CWFont.draw(g, "RIORDAS  $100", 70, 67 + (1*30), 2, Color.black);
		CWFont.draw(g, "VECDER   $250", 70, 67 + (2*30), 2, Color.black);
		CWFont.draw(g, "CORBAIN  $300", 70, 67 + (3*30), 2, Color.black);
		CWFont.draw(g, "CHENEY   $500", 70, 67 + (4*30), 2, Color.black);
		CWFont.draw(g, "MURELLO  $800", 70, 67 + (5*30), 2, Color.black);
		CWFont.draw(g, "BAND-AID $70", 70, 67 + (6*30), 2, Color.black);
		
	
		//description
		if(CState.scaledMouseX > 60 && CState.scaledMouseX < 60 + 180) 
		{
			drawDescription(1+(int)((CState.scaledMouseY - 60.0) / 30.0),g);

		}
			

		
		
	}
	
	static void drawDescription(int i,Graphics g)
	{
		switch(i)
		{
		case 1:
			CWFont.draw(g, "A pretty average knife,".toUpperCase(), 100, 311, 1, Color.black);
			CWFont.draw(g, "used by cheap bastards.".toUpperCase(), 100, 321, 1, Color.black);
			CWFont.draw(g, "Power: 33%  Range: 10px".toUpperCase(), 100, 331, 1, Color.black);
			CWFont.draw(g, "Rate: 2 Stabs per Second".toUpperCase(), 100, 341, 1, Color.black);	
		break;
		case 2:
			CWFont.draw(g, "A 1916 classic. Used".toUpperCase(), 100, 311, 1, Color.black);
			CWFont.draw(g, "by drunken pranksters. ".toUpperCase(), 100, 321, 1, Color.black);
			CWFont.draw(g, "Power: 20%    Acc: 10 deg".toUpperCase(), 100, 331, 1, Color.black);
			CWFont.draw(g, "Spd: 30  SemiAuto  10 rds".toUpperCase(), 100, 341, 1, Color.black);	
		break;
		case 3:
			CWFont.draw(g, "An American SMG. A nice".toUpperCase(), 100, 311, 1, Color.black);
			CWFont.draw(g, "even flow of bullets.".toUpperCase(), 100, 321, 1, Color.black);
			CWFont.draw(g, "Power: 10%    Acc: 15 deg".toUpperCase(), 100, 331, 1, Color.black);
			CWFont.draw(g, "Spd: 25  FullAuto  30 rds".toUpperCase(), 100, 341, 1, Color.black);	
		break;
		case 4:
			CWFont.draw(g, "Just a Shotgun. I swear, ".toUpperCase(), 100, 311, 1, Color.black);
			CWFont.draw(g, "you should buy this gun.".toUpperCase(), 100, 321, 1, Color.black);
			CWFont.draw(g, "Power: 11@10% Acc: 25 deg".toUpperCase(), 100, 331, 1, Color.black);
			CWFont.draw(g, "Spd: 20  Pump       7 rds".toUpperCase(), 100, 341, 1, Color.black);	
		break;
		case 5:
			CWFont.draw(g, "This semi-auto rifle will ".toUpperCase(), 100, 311, 1, Color.black);
			CWFont.draw(g, "get you elected president.".toUpperCase(), 100, 321, 1, Color.black);
			CWFont.draw(g, "POWER: "+Server.GARANDPOWER+"%     ACC: "+Server.GARANDACC+" DEG".toUpperCase(), 100, 331, 1, Color.black);
			CWFont.draw(g, "VEL: "+(int)(100.0*Server.GARANDSPEED)+"  SEMIAUTO   "+Server.GARANDCLIP+" RDS", 100, 341, 1, Color.black);		
			break;
		case 6:
			CWFont.draw(g, "Shoots rockets. You will".toUpperCase(), 100, 311, 1, Color.black);
			CWFont.draw(g, "score kills in your name.".toUpperCase(), 100, 321, 1, Color.black);
			CWFont.draw(g, "Power: ~200%   Acc: 5 deg".toUpperCase(), 100, 331, 1, Color.black);
			CWFont.draw(g, "Spd: 30  SingleShot 3 rds".toUpperCase(), 100, 341, 1, Color.black);		
			break;
		case 7:
			CWFont.draw(g, "A band aid. Use to heal".toUpperCase(), 100, 311, 1, Color.black);
			CWFont.draw(g, "you or your dumb friends".toUpperCase(), 100, 321, 1, Color.black);
			CWFont.draw(g, "Heals: 40%      ".toUpperCase(), 100, 331, 1, Color.black);		
		break;
		}
	}
}
