//Client Buy Menu: Draws a simple buymenu to the screen.

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class CBuyMenu {

	public static void draw(Graphics g)
	{		
		//Draw the transparent background window.
		g.setColor(new Color(.5f,.5f,1f,.9f));
		g.fillRect(50, 50, 300, 300);

		//Draw the storage section.
		drawStorage(g);
	
		//Draw the buttons for buying stuff
		drawHardCodedBuyButtons(g);
	
		//Draw the box that is supposed to show what you are buying
		drawDescriptionBox(g);
		
		//If hovering over a buy button, draw description for that button.
		if(CState.scaledMouseX > 60 && CState.scaledMouseX < 60 + 180) 
		{
			drawDescription(1+(int)((CState.scaledMouseY - 60.0) / 30.0),g);

		}
	}
	
	//draw all the items in storage. TODO- implement st
	private static void drawStorage(Graphics g) 
	{
		//TODO Team Storage Bar:
		CWFont.draw(g, "STORAGE", 271, 51, 1, Color.black);
		CWFont.draw(g, "NOT IMPLEMENTED", 251, 61, 1, Color.black);
		g.setColor(Color.black);
		g.drawRect(250,50,100,299);
		
		/**
		 Storage list
		CWFont.draw(g, "CORBAIN      x99", 251, 61, 1, Color.black);
		CWFont.draw(g, "R. LAUNCHER  x99", 251, 71, 1, Color.black);
		CWFont.draw(g, "UZI          x99", 251, 81, 1, Color.black);
		*/
		
	}

	//Draws the box that holds descriptions
	private static void drawDescriptionBox(Graphics g)
	{
		g.setColor(Color.black);
		CWFont.draw(g, "DESCRIPTION", 121, 301, 1, Color.black);
		g.setColor(Color.black);
		g.drawRect(50,300,200,50);
		g.drawRect(50,300,200,10);
	}

	//Draw item description based on item "i". TODO: Enumize Items.
	private static void drawDescription(int i,Graphics g)
	{
		switch(i)
		{
		case 1:
			CWFont.draw(g, "A pretty average knife,".toUpperCase(), 100, 312, 1, Color.black);
			CWFont.draw(g, "used by backstabbers.".toUpperCase(), 100, 322, 1, Color.black);
			CWFont.draw(g, "Power: 33%  Range: 10px".toUpperCase(), 100, 332, 1, Color.black);
			CWFont.draw(g, "Rate: 2 Stabs per Second".toUpperCase(), 100, 342, 1, Color.black);	
		break;
		case 2:
			CWFont.draw(g, "A 1916 classic. Choice".toUpperCase(), 100, 312, 1, Color.black);
			CWFont.draw(g, "of hipsters everywhere. ".toUpperCase(), 100, 322, 1, Color.black);
			CWFont.draw(g, "POWER: "+(Server.PISTOLPOWER+Server.PISTOLVAR/2)+"%    ACC: "+Server.PISTOLACC+" DEG", 100, 332, 1, Color.black);
			CWFont.draw(g, "SPD: "+(int)(100*Server.PISTOLSPEED)+"  SEMI-AUTO "+Server.PISTOLCLIP+" RDS", 100, 342, 1, Color.black);	
		break;
		case 3:
			CWFont.draw(g, "An American SMG. A nice".toUpperCase(), 100, 312, 1, Color.black);
			CWFont.draw(g, "even flow of bullets.".toUpperCase(), 100, 322, 1, Color.black);
			CWFont.draw(g, "POWER: "+(Server.SMGPOWER+Server.SMGVAR/2)+"%    ACC: "+Server.SMGACC+" DEG", 100, 332, 1, Color.black);
			CWFont.draw(g, "SPD: "+(int)(100*Server.SMGSPEED)+"  FULL-AUTO "+Server.SMGCLIP+" RDS", 100, 342, 1, Color.black);	
		break;
		case 4:
			CWFont.draw(g, "Just a Shotgun. I swear, ".toUpperCase(), 100, 312, 1, Color.black);
			CWFont.draw(g, "you should buy this gun.".toUpperCase(), 100, 322, 1, Color.black);
			CWFont.draw(g, "POWER: "+Server.SHOTGUNPELLETS+"@"+(Server.SHOTGUNPOWER+Server.SHOTGUNVAR/2)+"% ACC: "+Server.SHOTGUNACC+" DEG", 100, 332, 1, Color.black);
			CWFont.draw(g, "SPD: "+(int)(100*Server.SHOTGUNSPEED)+"  PUMP       "+Server.SHOTGUNCLIP+" RDS", 100, 342, 1, Color.black);	
		break;
		case 5:
			CWFont.draw(g, "This semi-auto rifle will ".toUpperCase(), 100, 312, 1, Color.black);
			CWFont.draw(g, "get you elected president.".toUpperCase(), 100, 322, 1, Color.black);
			CWFont.draw(g, "POWER: "+(Server.GARANDPOWER+Server.GARANDVAR/2)+"%     ACC: "+Server.GARANDACC+" DEG".toUpperCase(), 100, 332, 1, Color.black);
			CWFont.draw(g, "VEL: "+(int)(100.0*Server.GARANDSPEED)+"  SEMIAUTO   "+Server.GARANDCLIP+" RDS", 100, 342, 1, Color.black);		
			break;
		case 6:
			CWFont.draw(g, "Shoots rockets. You will".toUpperCase(), 100, 312, 1, Color.black);
			CWFont.draw(g, "score kills in your name.".toUpperCase(), 100, 322, 1, Color.black);
			CWFont.draw(g, "Power: ~200%   Acc: 5 deg".toUpperCase(), 100, 332, 1, Color.black);
			CWFont.draw(g, "Spd: 30  SingleShot 3 rds".toUpperCase(), 100, 342, 1, Color.black);		
			break;
		case 7:
			CWFont.draw(g, "A band aid. Use to heal".toUpperCase(), 100, 311, 1, Color.black);
			CWFont.draw(g, "you or your dumb friends".toUpperCase(), 100, 321, 1, Color.black);
			CWFont.draw(g, "Heals: 40%      ".toUpperCase(), 100, 331, 1, Color.black);		
		break;
		}
	}

	//Draw hard-coded buy buttons
	//TODO 
	private static void drawHardCodedBuyButtons(Graphics g)
	{
		g.drawRect(60, 60, 180, 25);
		g.drawRect(60, 90, 180, 25);
		g.drawRect(60, 120, 180, 25);
		g.drawRect(60, 150, 180, 25);
		g.drawRect(60, 180, 180, 25);
		g.drawRect(60, 210, 180, 25);
		g.drawRect(60, 240, 180, 25);
		
		CWFont.draw(g, "1 KNIFE   $0", 70, 65 + (0*30), 2, Color.black);
		
		CWFont.draw(g, "2 MANGUM  $"+Server.PISTOLCOST, 70, 65 + (1*30), 2, Color.black);
		CWFont.draw(g, "6 SHOTS $10", 70, 65 + (1*30) + 13, 1, Color.black);
		
		CWFont.draw(g, "3 VECDER  $"+Server.SMGCOST, 70, 65 + (2*30), 2, Color.black);
		CWFont.draw(g, "1 MAGAZINE )30 SHOTS( $30", 70, 65 + (2*30) + 13, 1, Color.black);
		
		CWFont.draw(g, "4 CORBAIN $"+Server.SHOTGUNCOST, 70, 65 + (3*30), 2, Color.black);
		CWFont.draw(g, "5 SHELLS $20", 70, 65 + (3*30) + 13, 1, Color.black);
		
		CWFont.draw(g, "5 CHENEY  $"+Server.GARANDCOST, 70, 65 + (4*30), 2, Color.black);
		CWFont.draw(g, "10 SHOTS $40", 70, 65 + (4*30) + 13, 1, Color.black);
		
		CWFont.draw(g, "6 MURELLO $"+Server.ROCKETCOST, 70, 65 + (5*30), 2, Color.black);
		CWFont.draw(g, "1 ROCKET $70", 70, 65 + (5*30) + 13, 1, Color.black);
		
		CWFont.draw(g, "7 NULL", 70, 65 + (6*30), 2, Color.black);
	}
}
