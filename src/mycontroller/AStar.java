package mycontroller;

// Group 40

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import mycontroller.util.util;
import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial.Direction;

public class AStar {
	
	// Initialize all cost for different traps
	private static float COST_LAVA = 100.0f;
	private static float DEFAULT = 1f;
	private static float COST_MUD = Float.MAX_VALUE;
	private static float COST_GRASS = 3f;
	
			
	
	public static ArrayList<Coordinate> getPath(HashMap<Coordinate, MapTile> view, 
            Coordinate from, Coordinate to){
		
		// define Astar parameters, open and close 
		ArrayList<Coordinate> exploredNodes;
		ArrayList<Coordinate> unexploredNodes;
		// record a sum g score for Astar
		float tentative_gScore;
		// record the last path
		HashMap<Coordinate, Coordinate> cameFrom;
		
		// initial data structure
        exploredNodes = new ArrayList<>();
        unexploredNodes = new ArrayList<>();
        cameFrom = new HashMap<>();
        HashMap<Coordinate, Float> gScore = new HashMap<>();
		HashMap<Coordinate, Float> fScore = new HashMap<>();
        
        // give initial values to the parameters
		gScore.put(from, 0.0f);
		fScore.put(from, getCost(view, from) * util.getDistanceManh(from, to));
        tentative_gScore = (float) 0.0;
        unexploredNodes.add(from);
        
		while (unexploredNodes.size() > 0) {
			Coordinate currentPosition = getMinFScore(fScore,unexploredNodes);
			
			// if all the roads have been explored or dead ends
			if (currentPosition == null) {
				return null; // no possible path found
			}
			
			// if reached the destination, evaluate this path
        	if (currentPosition.equals(to)) {
                return reconstructPath(currentPosition, cameFrom);
            }
        	
        	// update explored and unexplored node sets
        	unexploredNodes.remove(currentPosition);
        	exploredNodes.add(currentPosition);
        	       	
        	// check the neighbours of the current node
        	for (Entry<Coordinate, String> entry : util.getAllNeighbours(view,currentPosition).entrySet()) {
        		
        		// define parameters
        		Coordinate neighbour = entry.getKey();
        		String tileTypeString = entry.getValue();
        		
        		// Ignore the neighbor which is already evaluated.
                if (exploredNodes.contains(neighbour)) {
                    continue;	
                }
                            
                
	             // Keep the record of the distance from start to a neighbor
	             tentative_gScore = gScore.get(currentPosition) + util.getDistanceManh(currentPosition, neighbour);
	
	             // if this neighbour is new
	             if (!unexploredNodes.contains(neighbour)) {
	            	 unexploredNodes.add(neighbour);// Discover a new node
	             }	
		         else if (tentative_gScore >= gScore.get(neighbour)) {
		        	 continue;		// This is not a better path.
		         }

	         	
				// This path is the best until now. Record it!
				cameFrom.put(neighbour, currentPosition);
				if (tileTypeString.equals("MUD") || tileTypeString.equals("WALL")) {
					// Ignore the neighbor which is mud or wall.
					gScore.put(neighbour, COST_MUD);
				} else {
					// record the gScore of the neighbour so far
					gScore.put(neighbour, tentative_gScore);
				}

				// update the f-score for the neighbours
				float neighbourCost = getCost(view,neighbour); // get the cost to choose this path
				if(util.getTrapType(view, neighbour)=="grass") { // check if the grass is safe to pass
					if(!isSafeGrass(view, currentPosition, neighbour)) {
						neighbourCost = COST_MUD; // if the end of grass is wall or mud, punish this path
					}
				}
				fScore.put(neighbour, gScore.get(neighbour) + neighbourCost*util.getDistanceManh(neighbour, to)); // update my fscore
        	}
        }
		
		// no path discovered
        return new ArrayList<>();
	}
	
	// find the node in unexplored with the lowest fscore so far
	private static Coordinate getMinFScore(HashMap<Coordinate, Float> fScore, ArrayList<Coordinate> unexploredNodes) {

		float minValue = Float.MAX_VALUE;
		Coordinate minKey = null;
		for (Coordinate unexploredNode : unexploredNodes) {
			Float value = fScore.get(unexploredNode);
		    if (minValue > value) {
		    	minValue = value;
		    	minKey = unexploredNode;
		    }
		}
		return minKey; // return the node Coordinate
	}

	// build the path by construct all the previous steps
	private static ArrayList<Coordinate> reconstructPath(Coordinate current, HashMap<Coordinate, Coordinate> cameFrom) {
        ArrayList<Coordinate> total_path = new ArrayList<>();
        total_path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            total_path.add(current);
        }
        return total_path;
    }
	
	// retrieve the cost set for each maptile. 
	private static float getCost(HashMap<Coordinate, MapTile> view, Coordinate position) {
		MapTile mapTile = view.get(position);
		if (mapTile == null) {
			return COST_MUD; // treat anything outside of the map as unreachable
		}
		if (view.get(position).isType(Type.TRAP)) {
			TrapTile trap = (TrapTile) view.get(position);
			if (trap.getTrap() == "lava") {
				return COST_LAVA;
			}else if (trap.getTrap() == "mud") {
				return COST_MUD;
			}else if (trap.getTrap() == "grass") {
				return COST_GRASS;
			}else if (trap.getTrap() == "health"){
				return DEFAULT;
			}else {
				return DEFAULT; 
			}	
		}else {
			return DEFAULT;
		}
			
	}

	// for future updates. Not used in this project.
	public void update(float lava, float grass) {
		COST_LAVA = lava;
		COST_GRASS = grass;
	}
	
	// to check if it is safe to step onto the grass
	private static boolean isSafeGrass(HashMap<Coordinate,MapTile> view, Coordinate current, Coordinate grass) {
	Direction direction = util.getMyDirection(current,grass); // check the orientation when the car step on the grass
	Coordinate grass_one_next = util.getNeighbourCoordinate(grass, direction); // check the neighbour of the grass
	Coordinate grass_two_next = util.getNeighbourCoordinate(grass_one_next, direction);// check the neighbour of the neighbour of the grass
	
	if(view.containsKey(grass_two_next)) { // if the car can see the the neighbour of the neighbour of the grass
		if (view.get(grass_one_next).isType(Type.WALL)){ // if the end of the grass is a wall
			return false; // it is not safe
		}else if(view.get(grass_one_next).isType(Type.TRAP)) { 
			if(util.getTrapType(view, grass_one_next).equals("mud")) { // if the end of the grass is a mud
				return false; // this is not safe either
			}
    		if (util.getTrapType(view, grass_one_next).equals("grass")){ // if the end of the grass is a grass
    			return isSafeGrass( view, grass_one_next, grass_two_next); // look further
    		}
    	}
	}
	return true; // if nothing dangerous has been found, then it is safe. Or, the car can't see the end of the grass
}


}
