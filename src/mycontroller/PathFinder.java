package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial.Direction;

public class PathFinder {
	
	private static ArrayList<Coordinate> exploredNodes;
	private static ArrayList<Coordinate> unexploredNodes;
	private static float cost;
	
	
	private static final float COST_LAVA = 100.0f;
	private static final float COST_HEALTH = 0.5f;
	private static final float COST_MUD = 99999.9f;
	private static final float COST_GRASS = 999.9f;
	
			
	
	protected static ArrayList<Coordinate> getPath(HashMap<Coordinate, MapTile> map, 
            Coordinate from, Coordinate to){
		// initial data
        exploredNodes = new ArrayList<>();
        unexploredNodes = new ArrayList<>();
        cost = (float) 0.0;
        
        while(unexploredNodes.size()>0) {
        	Coordinate nextNode = null;
        	
        	unexploredNodes.remove(nextNode);
        	exploredNodes.add(nextNode);
        	
        }
		
		
				return null;
		
		
	}
	
	private static float getCost(Coordinate position, Direction direction) {

		return cost;
		
	}
	


}
