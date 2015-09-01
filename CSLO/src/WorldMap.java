import java.util.ArrayList;
import java.util.LinkedList;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Rectangle;

public class WorldMap extends TiledMap {
	final int TILESIZE = 8;
	//0, Destroyed. 0-499, weak. 500-999, med. 1000-1499, strong.
	public final static double STONEPHASESTR = 500;
	public final static int STONESTART = 4000;
	public final static int STONEPHASE = 4;
	//Unbreakable ID
	private final int UNBREAKABLE = 0;
	private double[][] tileIntegrity;
	private LinkedList<Tile> dirtyTiles;
	
	private int baseLayerIndex;
	private int wallLayerIndex;
	
	private ArrayList<Tile> spawnZoneTeam1;
	private ArrayList<Tile> spawnZoneTeam2;
	
	private boolean[][] buildZoneTeam1;
	private boolean[][] buildZoneTeam2;
	
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
		
		spawnZoneTeam1= new ArrayList<Tile>();
		spawnZoneTeam2= new ArrayList<Tile>();
	
		buildZoneTeam1= new boolean[this.getWidth()][this.getWidth()];
		buildZoneTeam2= new boolean[this.getWidth()][this.getWidth()];
		
		dirtyTiles = new LinkedList<Tile>();
		//TODO: client does not parse map!
		parseMap();
	}
	
	public void drawWorldMap(int topX, int topY){
		//ok so TopX
		int xTopOffset = topX % TILESIZE;
		int yTopOffset = topY % TILESIZE;
		render(-xTopOffset,-yTopOffset,topX / TILESIZE, topY / TILESIZE, 1 + CSLO.GAMEDIM / TILESIZE, 1 + CSLO.GAMEDIM / TILESIZE, baseLayerIndex ,false);
		render(-xTopOffset,-yTopOffset,topX / TILESIZE, topY / TILESIZE, 1 + CSLO.GAMEDIM / TILESIZE, 1 + CSLO.GAMEDIM / TILESIZE, wallLayerIndex ,false);
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
		
		//now to get spawn areas where you can't build
		baseLayerIndex = this.getLayerIndex("base");
		for(int x = 0; x != this.getWidth(); x++){
			for(int y = 0; y != this.getHeight(); y++){
				String spawnzone = getTileProperty(getTileId(x,y,baseLayerIndex), "Spawn", "0");
				
				if(!spawnzone.equals("0"))
				{
					
					if(spawnzone.equals("1"))
					{
						spawnZoneTeam1.add(new Tile(x,y));	
					}
					
					if(spawnzone.equals("2"))
					{
						spawnZoneTeam2.add(new Tile(x,y));
					}
				}
			}
		}
		
		int build = this.getLayerIndex("build");
		for(int x = 0; x != this.getWidth(); x++){
			for(int y = 0; y != this.getHeight(); y++){
				String buildzone = getTileProperty(getTileId(x,y,build), "Build", "0");
				
				if(buildzone != "0")
				{
					
					if(buildzone.equals("1"))
					{
						buildZoneTeam1[x][y] = true;
					}
					
					if(buildzone.equals("2"))
					{
						buildZoneTeam2[x][y] = true;
					}
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
		tileIntegrity[xTile][yTile] = ((STONEPHASE-1) * STONEPHASESTR) - 1.0;
		Tile t = new Tile(xTile,yTile);
		t.id = 4002;
		setTileId(t.x,t.y,wallLayerIndex,t.id); 
		dirtyTiles.add(t);
	}
	
	public boolean buildPermsission(int xTile, int yTile, boolean team)
	{
		return ((team ? buildZoneTeam1[xTile][yTile] : buildZoneTeam2[xTile][yTile]));
	}
	
	public double getTileIntegrity(int x, int y)
	{
		return tileIntegrity[x][y];
	}
	
	public Tile getSpawnLocation(boolean team1)
	{
		ArrayList<Tile> spawnZone;
		if(team1)
		{
			spawnZone = spawnZoneTeam1;
		} 
		else
		{
			spawnZone = spawnZoneTeam2;
		}
		
		//bad placement algorithm
		Tile placeTile = null; 
		boolean placement = false;
		while (!placement)
		{
			Tile spawnTile = (Tile)(spawnZone.toArray()[(int)(spawnZone.size() * Math.random())]);
			placeTile = new Tile(spawnTile.x * 8 - (CPlayer.RADIUS/2) , spawnTile.y * 8 - (CPlayer.RADIUS/2));
			if(!checkCollide(new Rectangle(placeTile.x,placeTile.y,CPlayer.RADIUS,CPlayer.RADIUS)))
				placement = true;
		}
		return placeTile;
	}

}
