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
	
	private static float COST_LAVA = 100.0f;
	private static float DEFAULT = 2f;
	private static float COST_MUD = Float.MAX_VALUE;
	private static float COST_GRASS = 10f;
	
			
	
	public static ArrayList<Coordinate> getPath(HashMap<Coordinate, MapTile> view, 
            Coordinate from, Coordinate to){
		
		ArrayList<Coordinate> exploredNodes;
		ArrayList<Coordinate> unexploredNodes;
		float tentative_gScore;
//		private static HashMap<Coordinate, MapTile> map;
		HashMap<Coordinate, Coordinate> cameFrom;
		// initial data
        exploredNodes = new ArrayList<>();
        unexploredNodes = new ArrayList<>();
        exploredNodes.add(from);
        cameFrom = new HashMap<>();
        HashMap<Coordinate, Float> gScore = new HashMap<>();
		gScore.put(from, 0.0f);
		HashMap<Coordinate, Float> fScore = new HashMap<>();
		fScore.put(from, getCost(view, from) * util.getDistanceManh(from, to));

        tentative_gScore = (float) 0.0;
        
//      updateMap(map);
        unexploredNodes.add(from);
		while (unexploredNodes.size() > 0) {
			Coordinate currentPosition = getMinFScore(fScore,unexploredNodes);
			
			if (currentPosition == null) {
				return null;
			}
			
        	if (currentPosition.equals(to)) {
//        		System.out.println(reconstructPath(currentPosition));
                return reconstructPath(currentPosition, cameFrom);
            }
        	
        	unexploredNodes.remove(currentPosition);
        	exploredNodes.add(currentPosition);
        	       	
        	for (Entry<Coordinate, String> entry : util.getAllNeighbours(view,currentPosition).entrySet()) {
        		
        		Coordinate neighbour = entry.getKey();
        		String tileTypeString = entry.getValue();
        		
                if (exploredNodes.contains(neighbour)) {
                    continue;	// Ignore the neighbor which is already evaluated.
                }
                            
                
	             // The distance from start to a neighbor
	             tentative_gScore = gScore.get(currentPosition) + util.getDistanceManh(currentPosition, neighbour);
	
	             if (!unexploredNodes.contains(neighbour)) {
	            	 unexploredNodes.add(neighbour);
//	            	 System.out.println(exploredNodes);
	             }	// Discover a new node
		         else if (tentative_gScore >= gScore.get(neighbour)) {
		        	 continue;		// This is not a better path.
		         }

	         	
				// This path is the best until now. Record it!
				cameFrom.put(neighbour, currentPosition);
				if (tileTypeString.equals("MUD") || tileTypeString.equals("WALL")) {
					gScore.put(neighbour, COST_MUD);
					// Ignore the neighbor which is mud or wall.
				} else {
					gScore.put(neighbour, tentative_gScore);
				}

	            fScore.put(neighbour, gScore.get(neighbour) + getCost(view,neighbour)*util.getDistanceManh(neighbour, to)); // getCost(view,neighbour)+
        	}
        }
        return new ArrayList<>();
	}
	
	private static Coordinate getMinFScore(HashMap<Coordinate, Float> fScore, ArrayList<Coordinate> unexploredNodes) {
		//
//		System.out.println(fScore);
		float minValue = Float.MAX_VALUE;
		Coordinate minKey = null;
		for (Coordinate unexploredNode : unexploredNodes) {
			Float value = fScore.get(unexploredNode);
		    if (minValue > value) {
		    	minValue = value;
		    	minKey = unexploredNode;
		    }
		}
		return minKey;
	}

	private static ArrayList<Coordinate> reconstructPath(Coordinate current, HashMap<Coordinate, Coordinate> cameFrom) {
        ArrayList<Coordinate> total_path = new ArrayList<>();
        total_path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            total_path.add(current);
        }
        return total_path;
    }
	
	
	private static float getCost(HashMap<Coordinate, MapTile> view, Coordinate position) {
		MapTile mapTile = view.get(position);
		if (mapTile == null) {
			return COST_MUD;
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
