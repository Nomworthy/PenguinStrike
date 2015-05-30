public class WorldState {
	public static WorldMap map;
	public static Player[] players = new Player[4];
	
	public static void instWorldState(){
		for(int i = 0; i != players.length; i++){
			players[i] = new Player();
		}
	}
}
