import org.newdawn.slick.AppletGameContainer.Container;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.gui.TextField;


public class CMainMenu {

	private enum State{
	PRELOBBY,
	//Defining name
	NAMESET,
	//Defining color
	COLORSET1,
	COLORSET2,
	//Defining IP
	IPSET,
	//searching for a lobby to connect to.
	LOBBYSEARCH,
	}
	
	private boolean done;
	private State mState = State.PRELOBBY;
	private static final int MAXNAMELEN = 8;
	private static final int MAXIPLEN = 15;	
	private TextField nameField;
	private TextField IPField;
	private Color primaryColor = new Color((float)Math.random(),(float)Math.random(),(float)Math.random());
	private Color secondaryColor = new Color((float)Math.random(),(float)Math.random(),(float)Math.random());
	
	public CMainMenu(GameContainer container){
   		TrueTypeFont f = new TrueTypeFont(new java.awt.Font(java.awt.Font.SERIF,java.awt.Font.BOLD,8),false);
		nameField = new TextField(container,f,-1,-1,0,0);
		IPField = new TextField(container,f,-1,-1,0,0);
	}
	public void draw(Image cursor,Graphics g, GUIContext container) {

        	
    		CWFont.draw(g,"COLD WAR", 70, 50,5,Color.cyan);
    		
    		CWFont.draw(g,"NAME: " + nameField.getText() + (mState == State.NAMESET ? "-" : "")
    				, 30, 110,3,new Color(127,0,255));
    		CWFont.draw(g,"FAVOURITE COLOUR", 30, 150,3, primaryColor);
    		CWFont.draw(g,"SECONDARY COLOUR", 30, 190,3, secondaryColor);
    		CWFont.draw(g,"SERVER IP:", 30, 230,3, Color.orange);
    		CWFont.draw(g,IPField.getText() + (mState == State.IPSET? "-" : "")
    				, 210, 235,2,Color.orange);
    		CWFont.draw(g,"CONNECT!", 30, 270,3, ((IPField.getText().length() == 0|| nameField.getText().length() == 0) ? Color.darkGray : Color.white));
    		
    		CWFont.draw(g,"(C) NEUTRAL SPACE STUDIOS 1985", 10, 310,2, Color.cyan);
    		
    		g.drawImage(cursor,CState.scaledMouseX-3 , CState.scaledMouseY-3 );		
	}

	public void doLogic(GameContainer container, int delta) {
		
		if(nameField.getText().length() > MAXNAMELEN)
		{
			nameField.setText(nameField.getText().substring(0, MAXNAMELEN));
		}
		if(IPField.getText().length() > MAXIPLEN)
		{
			IPField.setText(IPField.getText().substring(0, MAXIPLEN));
		}
    	if(CState.mouse1 && CState.scaledMouseY > 110 && CState.scaledMouseY < 150)
    		mState = State.NAMESET;
    	if(CState.mouse1 && CState.scaledMouseY > 150 && CState.scaledMouseY < 190)
    		primaryColor = new Color((float)Math.random(), (float)Math.random(),(float) Math.random());
    	if(CState.mouse1 && CState.scaledMouseY > 190 && CState.scaledMouseY < 230)
    		secondaryColor = new Color((float)Math.random(), (float)Math.random(),(float) Math.random());
    	if(CState.mouse1 && CState.scaledMouseY > 230 && CState.scaledMouseY < 280)
    		mState = State.IPSET;
    	if(CState.mouse1 && CState.scaledMouseY > 280 && CState.scaledMouseY < 320)
    		done = true;
    		//start the game
    	if(container.getInput().isKeyPressed(Input.KEY_ENTER))
    		mState = State.PRELOBBY;
    	
		nameField.setFocus(mState == State.NAMESET);
		IPField.setFocus(mState == State.IPSET);	
	}
	public boolean isDone() {
		return done;
	}
	
	public String getServerIP(){
		return IPField.getText();
	}
	
	public String getName(){
		return nameField.getText();
	}
}
