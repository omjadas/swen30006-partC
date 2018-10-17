package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import mycontroller.util.util;
import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;

public class AStar {
	
	// Initialize all cost for different traps
	private static float COST_LAVA = 100.0f;
	private static float DEFAULT = 1f;
	private static float COST_MUD = Float.MAX_VALUE;
	private static float COST_GRASS = 10f;
	
			
	
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
        // exploredNodes.add(from); this should not be here <--------------delete
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
					gScore.put(neighbour, tentative_gScore);
				}

				// update the f-score for the neighbours
	            fScore.put(neighbour, gScore.get(neighbour) + getCost(view,neighbour)*util.getDistanceManh(neighbour, to)); // getCost(view,neighbour)+
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
	
//	private static void updateMap(HashMap<Coordinate, MapTile> map) {
//		AStar.map = map;
//	}
	
	public void update(float lava, float health, float grass) {
		COST_LAVA = lava;
		COST_GRASS = grass;
	}
	


}
