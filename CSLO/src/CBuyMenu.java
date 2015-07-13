import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;




public class CBuyMenu {

	
	public static void draw(Graphics g)
	{

		g.setColor(new Color(.5f,.5f,1f,.9f));
		g.fillRect(50, 50, 300, 300);
		
		g.setColor(Color.black);
		

		CWFont.draw(g, "STORAGE", 271, 51, 1, Color.black);

		CWFont.draw(g, "CORBAIN      x99", 251, 61, 1, Color.black);
		CWFont.draw(g, "R. LAUNCHER  x99", 251, 71, 1, Color.black);
		CWFont.draw(g, "UZI          x99", 251, 81, 1, Color.black);
		CWFont.draw(g, "VEDDER       x99", 251, 91, 1, Color.black);
		CWFont.draw(g, "9MM MAGAZINE x99", 251, 101, 1, Color.black);
		CWFont.draw(g, "SHOTGUNSHELL x99", 251, 111, 1, Color.black);
		CWFont.draw(g, "USSR GRENADE x99", 251, 121, 1, Color.black);
		CWFont.draw(g, "TM47         x99", 251, 131, 1, Color.black);
		
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
		CWFont.draw(g, "It's a gun", 121, 311, 1, Color.black);
		

		CWFont.draw(g, "1. MELEE", 70, 67 + (0*30), 2, Color.black);
		CWFont.draw(g, "2. PISTOLS", 70, 67 + (1*30), 2, Color.black);
		CWFont.draw(g, "3. SMGS", 70, 67 + (2*30), 2, Color.black);
		CWFont.draw(g, "4. SHOTGUNS", 70, 67 + (3*30), 2, Color.black);
		CWFont.draw(g, "5. RIFLES", 70, 67 + (4*30), 2, Color.black);
		CWFont.draw(g, "6. EXPLOSIVES", 70, 67 + (5*30), 2, Color.black);
		CWFont.draw(g, "7. SUPPORT", 70, 67 + (6*30), 2, Color.black);
		
		
		/**
		CWFont.draw(g, "MELEE", 51 + 0*30, 51 + 0*35, 1, Color.black);
		
		CWFont.draw(g, "PISTOLS", 51 + 3*30, 51 + 0*35, 1, Color.black);
		
		CWFont.draw(g, "REVOLVERS", 51 + 0*30, 51 + 1*35, 1, Color.black);
		
		CWFont.draw(g, "SUBMACHINE GUNS", 51 + 3*30, 51 + 1*35, 1, Color.black);
	
		CWFont.draw(g, "SHOTGUNS", 51 + 0*30, 51 + 2*35, 1, Color.black);
		
		CWFont.draw(g, "RIFLES", 51 + 3*30, 51 + 2*35, 1, Color.black);
		
		CWFont.draw(g, "EXPLOSIVES", 51 + 0*30, 51 + 3*35, 1, Color.black);
		
		CWFont.draw(g, "SUPPORT", 51 + 3*30, 51 + 3*35, 1, Color.black);
		*/
		
		for(int x = 0; x != 7; x++)
		{
			for(int y = 0; y != 7; y++)
			{
			//	CWFont.draw(g, "TOPIC", 51 + y*30, 51 + x*35, 1, Color.black);
					
				//g.fillRect(x, y, width, height);
			}
		}
		
		
	}
}