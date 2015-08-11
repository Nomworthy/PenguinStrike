import java.util.LinkedList;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Rectangle;

public class WorldMap extends TiledMap {
	final int TILESIZE = 8;
	//0, Destroyed. 0-499, weak. 500-999, med. 1000-1499, strong.
	private final double STONEPHASESTR = 500;
	private final int STONESTART = 4000;
	private final int STONEPHASE = 4;
	private final int UNBREAKABLE = 0;
	private double[][] tileIntegrity;
	private LinkedList<Tile> dirtyTiles;
	private int wallLayerIndex;
	
	public class Tile{
		public int x;
		public int y;
		public int id;
		Tile(int x, int y){
			this.x=x;this.y=y;
		}
	}
	
	public WorldMap(String name) throws SlickException{
		super(name);
		tileIntegrity= new double[this.getWidth()][this.getWidth()];
		dirtyTiles = new LinkedList<Tile>();
		//TODO: client does not parse map!
		parseMap();
	}
	
	public void drawWorldMap(int topX, int topY){
		//ok so TopX
		int xTopOffset = topX % TILESIZE;
		int yTopOffset = topY % TILESIZE;
		render(-xTopOffset,-yTopOffset,topX / TILESIZE, topY / TILESIZE, 1 + CSLO.GAMEDIM / TILESIZE, 1 + CSLO.GAMEDIM / TILESIZE);
	}
	
	public void parseMap(){
		wallLayerIndex = this.getLayerIndex("wall");
		for(int x = 0; x != this.getWidth(); x++){
			for(int y = 0; y != this.getHeight(); y++){
				
				int tileID = getTileId(x,y,wallLayerIndex) - 1; 
				
				if(tileID >= STONESTART){
					int phase = tileID % STONEPHASE;
					if(phase == 0)
						tileIntegrity[x][y] = 0;
					else
						tileIntegrity[x][y] = (phase * STONEPHASESTR) - 1.0;
				}
				
				if(tileID == UNBREAKABLE)
				{
					tileIntegrity[x][y] = Integer.MAX_VALUE;
				}
			}
		}
	}
	
	public boolean checkCollide(Shape s){
		int minXTile = (int)(s.getMinX())/TILESIZE;
		int maxXTile = (int)(s.getMaxX())/TILESIZE;
		int minYTile = (int)(s.getMinY())/TILESIZE;
		int maxYTile = (int)(s.getMaxY())/TILESIZE;
		
		for(int x = minXTile; x <= maxXTile; x++){
			for(int y = minYTile; y <= maxYTile; y++){	
				if(tileIntegrity[x][y] > 0 && s.intersects(new Rectangle(x*TILESIZE,y*TILESIZE,TILESIZE,TILESIZE))){
					return true;
				}
			}
		}
		
		return false;
	}

	//checks collsion, but also does damage to wall.
	public boolean checkCollide(SProjectile s, double wallDamage) {
		
		int minXTile = (int)(s.getShape().getMinX())/TILESIZE;
		int maxXTile = (int)(s.getShape().getMaxX())/TILESIZE;
		int minYTile = (int)(s.getShape().getMinY())/TILESIZE;
		int maxYTile = (int)(s.getShape().getMaxY())/TILESIZE;
		
		for(int x = minXTile; x <= maxXTile; x++){
			for(int y = minYTile; y <= maxYTile; y++){	
				if(tileIntegrity[x][y] > 0 && s.getShape().intersects(new Rectangle(x*TILESIZE,y*TILESIZE,TILESIZE,TILESIZE))){
					//we collided! is the tile invincible?
					return true;
				}
			}
		}
		
		return false;
	}
	
	//cleans up tiles, returns tiles with new states
	public LinkedList<Tile> cleanDirtyTiles(){
		for(Tile t : dirtyTiles){
			//for testing purposes, completely wipeout the tile.
			int oldID = getTileId(t.x,t.y,wallLayerIndex) - 1; 
			int phase = oldID % STONEPHASE;
			int baseID = oldID - phase;
			int newID;
			
			if(tileIntegrity[t.x][t.y] == 0.00)
				newID = baseID + 1;
			else
				newID = baseID + 1 + (int)(tileIntegrity[t.x][t.y] / STONEPHASESTR) + 1;
			t.id = newID;
			
			setTileId(t.x,t.y,wallLayerIndex,newID); 
		}
		return dirtyTiles;
	}
	
	public void purgeDirtyTiles(){
		dirtyTiles = new LinkedList<Tile>();
	}

	public boolean tileBecomesDirty(int x, int y, double dmg){
		return (tileIntegrity[x][y] - dmg <= 0.0 || ((int)((tileIntegrity[x][y] / STONEPHASESTR)) != ((int)((tileIntegrity[x][y] - dmg) / STONEPHASESTR))));
	}
	
	public LinkedList<Tile> getDirtyTiles(){
		return dirtyTiles;	
	}
	
	public int getWallLayerIndex(){
		return wallLayerIndex;
	}

	public void damageTile(int xTile, int yTile, double damage) {
		if(tileIntegrity[xTile][yTile] > 0 && tileIntegrity[xTile][yTile] != Integer.MAX_VALUE)
		{
			//yes! before we damage the tile check if it becomes dirty.
			if(tileBecomesDirty(xTile,yTile,damage)){
				dirtyTiles.add(new Tile(xTile,yTile));
			}
			
			tileIntegrity[xTile][yTile] = Math.max(tileIntegrity[xTile][yTile] - damage,0.0);
		}	
	}
	
	public void constructTile(int xTile,int yTile)
	{
		//cant carry over
		tileIntegrity[xTile][yTile] = ((STONEPHASE-1) * STONEPHASESTR) - 1.0;
		Tile t = new Tile(xTile,yTile);
		t.id = 4002;
		setTileId(t.x,t.y,wallLayerIndex,t.id); 
		dirtyTiles.add(t);
	}

}
