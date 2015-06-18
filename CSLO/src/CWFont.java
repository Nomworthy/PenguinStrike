import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;


public class CWFont {
	private static Image fontSheet;
	private final static int fontWidth = 6;
	private final static int fontHeight = 7;
	private final static int sheetWidth = 6;
	private final static int sheetHeight = 6;
	
	public static void initFontSheet(){
		try {
			fontSheet = new Image("data/fonts/basicSmall.png");
			fontSheet.setFilter(Image.FILTER_NEAREST);
		} catch (SlickException e) {
			System.out.println("Error loading font");
		}
	}
	
	public static void draw(Graphics g, String s, int x, int y, int scale, Color color)
	{
		s = new String(s).toUpperCase();
		for (int i = 0; i < s.length(); i++){
		    char c = s.charAt(i);     
		    print(g,c,x,y,scale,color);
		    x += fontWidth*scale;
		}	
	}
	
	private static void print(Graphics g, char c, int x, int y, int scale,Color color)
	{
		int pos;
		if(Character.isAlphabetic(c))
		{
			pos = c - 65;
		} else if (c >= 32 && c <= 58){
			pos = (26) + (c - 32);
		} else {
			return;
		}
		
		int xLetter = (pos%sheetWidth)*fontWidth;
		int yLetter = (pos/sheetWidth)*fontHeight;
		g.setColor(Color.red);
		fontSheet.draw((float)x, (float)y, (float)x + scale*fontWidth,  (float)y + scale*fontHeight,(float)xLetter, (float)yLetter,(float)xLetter + fontWidth,(float)yLetter + fontHeight,color);
	}

}
