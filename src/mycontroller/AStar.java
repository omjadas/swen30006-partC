package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import mycontroller.util.util;
import tiles.MapTile;
import utilities.Coordinate;

public class AStar {
	
	private static ArrayList<Coordinate> exploredNodes;
	private static ArrayList<Coordinate> unexploredNodes;
	private static float tentative_gScore;
	private static HashMap<Coordinate, MapTile> map;
	private static HashMap<Coordinate, Coordinate> cameFrom;
	private static HashMap<Coordinate, Float> gScore;
	private static HashMap<Coordinate, Float> fScore;
	
	
	
	private static final float COST_LAVA = 100.0f;
	private static final float COST_HEALTH = 0.1f;
	private static final float COST_MUD = Float.MAX_VALUE;
	private static final float COST_GRASS = 0.5f;
	
			
	
	protected static ArrayList<Coordinate> getPath(HashMap<Coordinate, MapTile> map, 
            Coordinate from, Coordinate to){
		// initial data
        exploredNodes = new ArrayList<>();
        unexploredNodes = new ArrayList<>();
        exploredNodes.add(from);
        cameFrom = new HashMap<>();
        gScore = new HashMap<>();
        gScore.put(from, 0.0f);
        fScore = new HashMap<>();
        fScore.put(from, util.getDistanceManh(from, to));
        updateMap(map);
        
        
        tentative_gScore = (float) 0.0;
        Coordinate currentPosition;
        
        unexploredNodes.add(from);
        while(unexploredNodes.size()>0) {
        	
        	currentPosition = getMinFScore();
        	if (currentPosition.equals(to)) {
//        		System.out.println(reconstructPath(currentPosition));
                return reconstructPath(currentPosition);
            }
        	
        	unexploredNodes.remove(currentPosition);
        	exploredNodes.add(currentPosition);
        	
//        	System.out.println(fScore);
        	
        	for (Entry<Coordinate, String> entry : util.getAllNeighbours(map,currentPosition).entrySet()) {
        		
        		Coordinate neighbour = entry.getKey();
        		
                if (exploredNodes.contains(neighbour)) {
                    continue;	// Ignore the neighbor which is already evaluated.
                }
                
	             // The distance from start to a neighbor
	             tentative_gScore = gScore.get(currentPosition) + util.getDistanceManh(currentPosition, neighbour);
	
	             if (!unexploredNodes.contains(neighbour)) {
	            	 unexploredNodes.add(neighbour);
	             }	// Discover a new node
		         else if (tentative_gScore >= gScore.get(neighbour)) {
		        	 continue;		// This is not a better path.
		         }

	         	
	         	// This path is the best until now. Record it!
	             cameFrom.put(neighbour, currentPosition);
	             gScore.put(neighbour, tentative_gScore); 
	             fScore.put(neighbour, gScore.get(neighbour)*0 + util.getDistanceManh(neighbour, to)); //getCost(neighbour)*
        	}
        }
        return null;
	}
	
	private static Coordinate getMinFScore() {
		//
		
		Entry<Coordinate, Float> minValue = null;
		for (Entry<Coordinate, Float> entry : fScore.entrySet()) {
		    if (minValue == null || minValue.getValue() > entry.getValue()) {
		    	minValue = entry;
		    }
		}

		return minValue.getKey();
	}

	private static ArrayList<Coordinate> reconstructPath(Coordinate current) {
        ArrayList<Coordinate> total_path = new ArrayList<>();
        total_path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            total_path.add(current);
        }
        return total_path;
    }
	
	
	private static float getCost(Coordinate position) {
		if (util.getTrapType(map,position) == "lava") {
			return COST_LAVA;
		}else if (util.getTrapType(map,position) == "mud") {
			return COST_MUD;
		}else if (util.getTrapType(map,position) == "grass") {
			return COST_GRASS;
		}else {
			return COST_HEALTH;
		}		
	}
	
	private static void updateMap(HashMap<Coordinate, MapTile> map) {
		AStar.map = map;
	}
	
	
	


}