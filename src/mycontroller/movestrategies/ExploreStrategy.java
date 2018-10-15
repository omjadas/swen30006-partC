package mycontroller.movestrategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import mycontroller.AStar;
import mycontroller.util.util;
import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class ExploreStrategy implements Pathable{
	static ArrayList<Coordinate> seens = new ArrayList<Coordinate>();
	static ArrayList<Coordinate> visits = new ArrayList<Coordinate>();
	static HashMap<Coordinate, MapTile> incompleteMap = new HashMap<Coordinate, MapTile>();

	@Override
	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> view, 
            Coordinate from) {
		
		updateExplored(view);
		updateMap(view);
		ArrayList<Coordinate> allValidRoads = getRoadsInView(view, from);
		HashMap<Coordinate,ArrayList<Coordinate>> validRoads = new HashMap<Coordinate,ArrayList<Coordinate>>();
		ArrayList<Coordinate> path = null;
		Coordinate nextStep = null;

		for (Coordinate validRoad: allValidRoads) {
			System.out.println("loop");
			if (visits.contains(validRoad)) {
				continue;
			}
			path = AStar.getPath(incompleteMap, from, validRoad);
			
			if (path == null) {
//				visits.add(validRoad);
				continue;
			}
			validRoads.put(validRoad,path);
		}
		
		if(validRoads.isEmpty()) {
			
			Random randomGenerator = new Random();
			int randomSelect = randomGenerator.nextInt(visits.size());
			nextStep = visits.get(randomSelect); 
			
			path = AStar.getPath(incompleteMap, from, nextStep);
			System.out.println("go back to " + nextStep);
			
//			ArrayList<Coordinate> neighbours = checkRoads(incompleteMap, from);
//			System.out.println(neighbours.get(1));
//			path = AStar.getPath(incompleteMap, from, neighbours.get(1));
		}else {
			nextStep = getFurtherest(validRoads.keySet(),from);	
			path = validRoads.get(nextStep);
		}
		
		for (Coordinate x : path){
			   if (!visits.contains(x))
				   visits.add(x);
			}
		
		return path;
	}
	
	private static void updateExplored(HashMap<Coordinate, MapTile> view) {
		for (Entry<Coordinate, MapTile> entry : view.entrySet()) {
			seens.add(entry.getKey());
		}
	}
	
	private static void updateMap(HashMap<Coordinate, MapTile> view) {
		incompleteMap.putAll(view);
	}
	
	public static ArrayList<Coordinate> checkRoads(HashMap<Coordinate, MapTile> map, Coordinate currentPosition){
		// Check tiles if they are safe to move to
		HashMap<Coordinate, String> neighbours = util.getAllNeighbours(map, currentPosition);
//		System.out.println(neighbours);
		ArrayList<Coordinate> areRoads = new ArrayList<Coordinate>();
		
		for (Entry<Coordinate, String> entry : neighbours.entrySet()) {
			if (entry.getValue().equals("ROAD")) {
				areRoads.add(entry.getKey());
			}
		}
		return areRoads;
	}
	
	private static ArrayList<Coordinate> getRoadsInView(HashMap<Coordinate, MapTile> view, Coordinate currentPosition) {
		
		ArrayList<Coordinate> roads = new ArrayList<Coordinate>();

	    for (Entry<Coordinate,MapTile> pair : view.entrySet()) {
	        MapTile mapTile = pair.getValue();
	        if (mapTile.isType(Type.ROAD)) {
	        	roads.add((Coordinate) pair.getKey());
	        }
	        
	        if (mapTile.isType(Type.TRAP)) {
	        	if (util.getTrapType(view, (Coordinate) pair.getKey()).equals("")) {
	        		
	        	}
	        }
	    }
		return roads;
	}
	
	private static Coordinate getFurtherest(Set<Coordinate> set, Coordinate from){

		Coordinate furtherestPoint = null;
		float maxDistance = 0.0f;
		
		for (Coordinate coordinate: set) {
			float distance = util.getDistanceEucl(coordinate,getExploredCenter());
			if (distance > maxDistance) {
				furtherestPoint = coordinate;
			}
		}
		if (furtherestPoint == null)
//			System.out.println(coordinates);
			System.out.println("No max distance is found!"); 
		return furtherestPoint;
	}

	private static Coordinate getRandomLocation(ArrayList<Coordinate> coordinates){
		Random randomGenerator = new Random();
		int randomSelect = randomGenerator.nextInt(coordinates.size());
		return coordinates.get(randomSelect);
	}

	private void updateAstar() {
		
	}
	
	private static Coordinate getExploredCenter() {
		double sumX = 0.0;
		double sumY = 0.0;
		for (Coordinate see:seens) {
			sumX += see.x;
			sumY += see.y;
		}
		int meanX = (int) sumX/seens.size();
		int meanY = (int) sumY/seens.size();
		return new Coordinate(meanX, meanY);
	}
}
