//Draw a basic main menu
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.gui.TextField;

public class CMainMenu {
	
	private static final int MAXNAMELEN = 8;
	private static final int MAXIPLEN = 15;
	private static final int CURSORBLINKTIME = 500;
	
	private enum State{
		//Nothing has been selected on the menu.
		PRELOBBY,
		//Defining player name, which (TODO) does nothing.
		NAMESET,
		//Defining primary color
		COLORSET1,
		//Defining secondary color
		COLORSET2,
		//Defining IP
		IPSET,
		//Searching for a lobby to connect to.
		LOBBYSEARCH,
	}
	
	//Menu State
	private State mState = State.PRELOBBY;

	//when true, ready to join server.
	private boolean finished;
	
	private TextField nameField;
	private TextField IPField;
	
	private Color primaryColor;
	private Color secondaryColor;
	
	//goes from CURSORBLINKTIMER to -CURSORBLINKTIMER
	private int cursorBlinkTimer;
	
	
	public CMainMenu(GameContainer container){
		//Unfortunately, If I want to use a TextField, I am forced to use a font, even if I don't render it.
   		TrueTypeFont f = new TrueTypeFont(new java.awt.Font(java.awt.Font.SERIF,java.awt.Font.BOLD,8),false);
		nameField = new TextField(container,f,-1,-1,0,0);
		nameField.setMaxLength(MAXNAMELEN);
		IPField = new TextField(container,f,-1,-1,0,0);
		IPField.setMaxLength(MAXIPLEN);
		//Load last saved stuff
		loadSettings();
	}
	
	
	public void draw(Image cursor,Graphics g, GUIContext container) {
        	
    		CWFont.draw(g,"COLD WAR", 70, 50,5,Color.cyan);
    		CWFont.draw(g,"-ULTIMATE PENGUIN HEROES-", 110, 90,1,Color.cyan);
    		
    		CWFont.draw(g,"NAME: " + nameField.getText() + (mState == State.NAMESET && cursorBlinkTimer > 0 ? "|" : "")
    				, 30, 110,3,new Color(127,0,255));
    		CWFont.draw(g,"FAVOURITE COLOUR", 30, 150,3, primaryColor);
    		CWFont.draw(g,"SECONDARY COLOUR", 30, 190,3, secondaryColor);
    		CWFont.draw(g,"SERVER IP:", 30, 230,3, Color.orange);
    		CWFont.draw(g,IPField.getText() + (mState == State.IPSET && cursorBlinkTimer > 0 ? "-" : "")
    				, 210, 235,2,Color.orange);
    		CWFont.draw(g,"CONNECT!", 30, 270,3, ((IPField.getText().length() == 0|| nameField.getText().length() == 0) ? Color.darkGray : Color.white));
    		
    		CWFont.draw(g,")C( NEUTRAL SPACE STUDIOS 1985", 10, 310,2, Color.cyan);
        	
  
	    			
        	if(CState.scaledMouseY > 150 && CState.scaledMouseY < 170)
        	{
        		drawColorSet(g,30,150,290,20);
        		if(Mouse.isButtonDown(0))
            		CWFont.draw(g,"FAVOURITE COLOUR", 30, 150,3, primaryColor);
        	}
        	if(CState.scaledMouseY > 190 && CState.scaledMouseY < 210)
        	{
        		drawColorSet(g,30,190,290,20);
        		if(Mouse.isButtonDown(0))
            		CWFont.draw(g,"SECONDARY COLOUR", 30, 190,3, secondaryColor);
        	}
        	
    		g.drawImage(cursor,CState.scaledMouseX-5 , CState.scaledMouseY-5 );
	}

	public void doLogic(GameContainer container, int delta, Input i) {
		
		cursorBlinkTimer = cursorBlinkTimer - delta;
		if (cursorBlinkTimer < -CURSORBLINKTIME)
			cursorBlinkTimer = CURSORBLINKTIME;
		
    	if(CState.mouse1 && CState.scaledMouseY > 110 && CState.scaledMouseY < 150)
    	{
    		cursorBlinkTimer= CURSORBLINKTIME;
    		mState = State.NAMESET;
    	}
  		
		if(CState.scaledMouseX > 30 && CState.scaledMouseX < 320)
		{
			
	    	if(CState.mouse1 && CState.scaledMouseY > 150 && CState.scaledMouseY < 180)
	    	{
	    		primaryColor = getColor(30,150,290,20,CState.scaledMouseX,CState.scaledMouseY);
	    		//colour change
	    	}
	    	if(CState.mouse1 && CState.scaledMouseY > 190 && CState.scaledMouseY < 220)
	    	{
	    		secondaryColor = getColor(30,190,290,20,CState.scaledMouseX,CState.scaledMouseY);
	    		//colour change
	    	}
		}	
		
    	if(CState.mouse1 && CState.scaledMouseY > 230 && CState.scaledMouseY < 280)
    	{
    		cursorBlinkTimer= CURSORBLINKTIME;
    		mState = State.IPSET;
    	}
    	
    	if(CState.mouse1 && CState.scaledMouseY > 280 && CState.scaledMouseY < 320)
    		finished = true;
    		//starts the game
    	if(container.getInput().isKeyPressed(Input.KEY_ENTER))
    		mState = State.PRELOBBY;
    	
		nameField.setFocus(mState == State.NAMESET);
		IPField.setFocus(mState == State.IPSET);	
	}
	public boolean isDone() {
		return finished;
	}
	
	public String getServerIP(){
		return IPField.getText();
	}
	
	public String getName(){
		return nameField.getText();
	}
	
	public void drawColorSet(Graphics g, int x, int y, int w, int h)
	{
		for(int xd = x; xd != x+w; xd++)
		{
			for(int yd = y; yd != y+h; yd++)
			{
				//A section has size w/6
				int section = (6*(xd-x))/(w);
				float percent = ((xd - x) - (section*(w/6f))) * (1f/(w/6f));
				//System.out.println(section + " " + percent);
				switch(section)
				{ 
					case 0: g.setColor(new Color(1f,percent,0f)); break;
					case 1: g.setColor(new Color(1f-percent,1f,0f)); break;
					case 2: g.setColor(new Color(0f,1f,percent)); break;
					case 3: g.setColor(new Color(0f,1f-percent,1f)); break;
					case 4: g.setColor(new Color(0f,0f,1f-percent)); break;
					case 5: g.setColor(new Color(percent,percent,percent)); break;
				}

				g.fillRect(xd, yd, 1, 1);
				//g.setColor()
			
			}
		}
		
	}
	
	public Color getColor(int x, int y, int w, int h, int xd, int yd)
	{
		//A section has size w/6
		int section = (6*(xd-x))/(w);
		float percent = ((xd - x) - (section*(w/6f))) * (1f/(w/6f));
		//System.out.println(section + " " + percent);
		switch(section)
		{ 
			case 0: return (new Color(1f,percent,0f)); 
			case 1: return (new Color(1f-percent,1f,0f)); 
			case 2: return (new Color(0f,1f,percent)); 
			case 3: return (new Color(0f,1f-percent,1f));
			case 4: return (new Color(0f,0f,1f-percent)); 
			case 5: return (new Color(percent,percent,percent));
		}
			
		return null;
	}
	
	public Color getPrimaryCol()
	{
		return primaryColor;
	}
	
	public Color getSecondaryCol()
	{
		return secondaryColor;
	}
	
	//Load last name, color,
    private void loadSettings() {
    	BufferedReader br;
    	
		try {
			
			br = new BufferedReader(new FileReader("settings.neutral"));
			
			String name = br.readLine();
    	    nameField.setText(name.length() <= MAXNAMELEN ? name : name.substring(0, MAXNAMELEN-1));
			nameField.setCursorPos(nameField.getText().length());
    	    primaryColor = new Color(Float.parseFloat( br.readLine() ),Float.parseFloat(br.readLine()),Float.parseFloat(br.readLine()));
			secondaryColor = new Color(Float.parseFloat( br.readLine() ),Float.parseFloat(br.readLine()),Float.parseFloat(br.readLine()));
			
			String IP = br.readLine();
			IPField.setText(IP);
			nameField.setCursorPos(IP.length());
	    	
    	    
    	} catch (Exception e){
    		e.printStackTrace();
    	}
	}
}
