import java.util.LinkedList;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.geom.Rectangle;

public class WorldMap extends TiledMap {
	private final int TILESIZE = 8;
	private final double STONEPHASESTR = 500;
	private final int STONESTART = 4000;
	private final int STONEPHASE = 4;
	private double[][] tileIntegrity;
	private LinkedList<Vector2f> dirtyTiles;
	
	public class Tile{
		public int x;
		public int y;
		Tile(int x, int y){
			this.x=x;this.y=y;
		}
	}
	
	public WorldMap(String name) throws SlickException{
		super(name);
		tileIntegrity= new double[this.getWidth()][this.getWidth()];
		dirtyTiles = new LinkedList<Vector2f>();
		parseMap();
	}
	
	public void drawWorldMap(int topX, int topY){
		//ok so TopX
		int xTopOffset = topX % TILESIZE;
		int yTopOffset = topY % TILESIZE;
		render(-xTopOffset,-yTopOffset,topX / TILESIZE, topY / TILESIZE, 1 + CSLO.GAMEDIM / TILESIZE, 1 + CSLO.GAMEDIM / TILESIZE);
	}
	
	public void parseMap(){
		int wallLayerIndex = this.getLayerIndex("wall");
		for(int x = 0; x != this.getWidth(); x++){
			for(int y = 0; y != this.getWidth(); y++){
				int tileID = getTileId(x,y,wallLayerIndex); 
				if(tileID >= STONESTART){
					int phase = tileID % STONEPHASE;
					tileIntegrity[x][y] = phase * STONEPHASESTR;
				}
			}
		}
	}
	
	public boolean checkCollide(Shape s){
		int minXTile = (int)(s.getMinX())/TILESIZE;
		int maxXTile = (int)(s.getMaxX() + 1)/TILESIZE;
		int minYTile = (int)(s.getMinY())/TILESIZE;
		int maxYTile = (int)(s.getMaxY() + 1)/TILESIZE;
		
		for(int x = minXTile; x < maxXTile; x++){
			for(int y = minYTile; y < maxYTile; y++){
				if(tileIntegrity[x][y] > 0 && s.contains(new Rectangle(x*TILESIZE,y*TILESIZE,TILESIZE,TILESIZE))){
					return true;
				}
			}
		}
		
		return false;
	}
	//public void setTileId(int x,
   // int y,
   // int layerIndex,
   // int tileid)
	
}
