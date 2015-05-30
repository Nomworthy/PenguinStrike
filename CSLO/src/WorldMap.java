import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;


public class WorldMap extends TiledMap {
	private final int TILESIZE = 8;
	
	public WorldMap(String name) throws SlickException{
		super(name);
	}
	
	public void drawWorldMap(int topX, int topY){
		//ok so TopX
		int xTopOffset = topX % TILESIZE;
		int yTopOffset = topY % TILESIZE;
		render(-xTopOffset,-yTopOffset,topX / TILESIZE, topY / TILESIZE, 1 + CSLO.GAMEDIM / TILESIZE, 1 + CSLO.GAMEDIM / TILESIZE);
	}
	
	//public void setTileId(int x,
   // int y,
   // int layerIndex,
   // int tileid)
	
}
