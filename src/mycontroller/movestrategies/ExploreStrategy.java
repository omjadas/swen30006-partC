package mycontroller.movestrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
//	static ArrayList<Coordinate> seens = new ArrayList<Coordinate>();
	static HashMap<Coordinate,Integer> visits = new HashMap<Coordinate,Integer>();
//	static HashMap<Coordinate, MapTile> incompleteMap = new HashMap<Coordinate, MapTile>();

	@Override
	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, 
            Coordinate from) {
		
//		updateExplored(view);
//		updateMap(view);
		ArrayList<Coordinate> allValidRoads = getRoadsInView(map, from);
//		HashMap<Coordinate,ArrayList<Coordinate>> validRoads = new HashMap<Coordinate,ArrayList<Coordinate>>();
		ArrayList<Coordinate> current_path = new ArrayList<>();
		ArrayList<Coordinate> available_path = new ArrayList<>();
		Coordinate nextStep = null;
		Random randomGenerator = new Random();
		
		// if key was just found the shortest path out of the lava is chosen
		if (allValidRoads.size() == 0) {
			ArrayList<ArrayList<Coordinate>> paths = new ArrayList<>();
			for (Coordinate road : getNearbyRoads(map, from)) {
				paths.add(AStar.getPath(map, from, road));
			}
			paths.removeAll(Collections.singleton(null));
			return Collections.min(paths, (ArrayList<Coordinate> p1, ArrayList<Coordinate> p2) -> {
				int p1l = 0;
				int p2l = 0;
				System.out.println(p1);
				for (Coordinate coord : p1) {
					if (util.getTrapType(map, coord).equals("lava")) {
						p1l += 1;
					}
				}
				for (Coordinate coord : p2) {
					if (util.getTrapType(map, coord).equals("lava")) {
						p2l += 1;
					}
				}
				return p1l - p2l;
			});
		}
		
		for (Coordinate validRoad: allValidRoads) {
			if (!visits.containsKey(validRoad)) {
				visits.put(validRoad,0);
				available_path.add(validRoad);
			}
		}
		if (available_path.size()>0) {
			int index = randomGenerator.nextInt(available_path.size());
			nextStep = available_path.get(index);
		} else {
			Coordinate destination =  util.getFinal(map);
			// if this car has not visited the final
			if(!visits.containsKey(destination)) {
				// use A star to go to the final destination to stay away from the familiar regions
				ArrayList<Coordinate> path = (ArrayList<Coordinate>) AStar.getPath(map, from, util.getFinal(map));
				if (path.size()>1) {
					nextStep = path.get(path.size()-2); // move to the next step given by A star
				}
//				System.out.println(""+from+nextStep);
			}
		}
		if (nextStep==null) { // use random walk selected from previously visited mpaTiles.
			int index = randomGenerator.nextInt(allValidRoads.size());
			nextStep = allValidRoads.get(index);
		}
		
		System.out.println(nextStep);
		
		current_path.add(from);
		current_path.add(nextStep);
//		System.out.println(current_path);
		if (!visits.containsKey(from)) {
			visits.put(from,1);
		}else {
			visits.put(from,visits.get(from)+1);
		}
//			path = AStar.getPath(incompleteMap, from, validRoad);

			
//			if (path == null) {
////				visits.add(validRoad);
//				continue;
//			}
//			validRoads.put(validRoad,path);

//		if(validRoads.isEmpty()) {
//			
//			Random randomGenerator = new Random();
//			int randomSelect = randomGenerator.nextInt(visits.size());
//			nextStep = visits.get(randomSelect); 
//			
//			path = AStar.getPath(incompleteMap, from, nextStep);
//			System.out.println("go back to " + nextStep);
//			
////			ArrayList<Coordinate> neighbours = checkRoads(incompleteMap, from);
////			System.out.println(neighbours.get(1));
////			path = AStar.getPath(incompleteMap, from, neighbours.get(1));
//		}else {
//			nextStep = getFurtherest(validRoads.keySet(),from);	
//			path = validRoads.get(nextStep);
//		}
//		
//		for (Coordinate x : path){
//			   if (!visits.contains(x))
//				   visits.add(x);
//			}
//		
		return current_path;
	}
	
//	private static void updateExplored(HashMap<Coordinate, MapTile> view) {
//		for (Entry<Coordinate, MapTile> entry : view.entrySet()) {
//			seens.add(entry.getKey());
//		}
//	}
	
//	private static void updateMap(HashMap<Coordinate, MapTile> view) {
//		incompleteMap.putAll(view);
//	}
	
//	public static ArrayList<Coordinate> checkRoads(HashMap<Coordinate, MapTile> map, Coordinate currentPosition){
//		// Check tiles if they are safe to move to
//		HashMap<Coordinate, String> neighbours = util.getAllNeighbours(map, currentPosition);
////		System.out.println(neighbours);
//		ArrayList<Coordinate> areRoads = new ArrayList<Coordinate>();
//		
//		for (Entry<Coordinate, String> entry : neighbours.entrySet()) {
//			if (entry.getValue().equals("ROAD")) {
//				areRoads.add(entry.getKey());
//			}
//		}
//		return areRoads;
//	}
	private static boolean isSafeGrass(HashMap<Coordinate,MapTile> view, Coordinate current, Coordinate grass) {
		Direction direction = util.getMyDirection(current,grass);
		Coordinate grass_one_next = util.getNeighbourCoordinate(grass, direction);
		Coordinate grass_two_next = util.getNeighbourCoordinate(grass_one_next, direction);
		if(view.containsKey(grass_two_next)) {
			if (view.get(grass_one_next).isType(Type.ROAD)){
				return true;
			}else if(view.get(grass_one_next).isType(Type.TRAP)) {
	    		if (util.getTrapType(view, grass_one_next).equals("grass")){
	    			return isSafeGrass( view, grass_one_next, grass_two_next);
	    		}
	    	}
		}
		return false;
	}
	
	
	private static ArrayList<Coordinate> getNearbyRoads(HashMap<Coordinate, MapTile> view, Coordinate currentPosition) {
		ArrayList<Coordinate> roads = new ArrayList<Coordinate>();
	    for (Entry<Coordinate,MapTile> pair : view.entrySet()) {
	        MapTile mapTile = (MapTile) pair.getValue();
	        Coordinate viewLocation = (Coordinate) pair.getKey();
	        if (util.getNearby(view, currentPosition, 4).containsKey(viewLocation)) {
	        	if (mapTile.isType(Type.ROAD)) {
	        		roads.add((Coordinate) pair.getKey());
	        	}else if(mapTile.isType(Type.TRAP)) {
	        		if (util.getTrapType(view, viewLocation).equals("grass")){
	        			if (isSafeGrass( view, currentPosition, viewLocation)) {
		        			roads.add(viewLocation);
	        			}
	        		}
	        	}
	        }
	    }
		return roads;
	}
	
	private static ArrayList<Coordinate> getRoadsInView(HashMap<Coordinate, MapTile> view, Coordinate currentPosition) {
		
		ArrayList<Coordinate> roads = new ArrayList<Coordinate>();

	    for (Entry<Coordinate,MapTile> pair : view.entrySet()) {
	        MapTile mapTile = (MapTile) pair.getValue();
	        Coordinate viewLocation = (Coordinate) pair.getKey();
	        if (util.getAllNeighbours(view, currentPosition).containsKey(viewLocation)) {
	        	if (mapTile.isType(Type.ROAD)) {
	        		roads.add((Coordinate) pair.getKey());
	        	}else if(mapTile.isType(Type.TRAP)) {
	        		if (util.getTrapType(view, viewLocation).equals("grass")){
	        			if (isSafeGrass( view, currentPosition, viewLocation)) {
		        			roads.add(viewLocation);
	        			}
	        		}else if (util.getTrapType(view, viewLocation).equals("health")){
		        		roads.add(viewLocation);
	        		}
	        	}
	        }
	        
//	        if (mapTile.isType(Type.TRAP)) {
//	        	if (util.getTrapType(view, (Coordinate) pair.getKey()).equals("")) {
//	        		
//	        	}
//	        }
	    }
		return roads;
	}
	
//	private static Coordinate getFurtherest(Set<Coordinate> set, Coordinate from){
//
//		Coordinate furtherestPoint = null;
//		float maxDistance = 0.0f;
//		
//		for (Coordinate coordinate: set) {
//			float distance = util.getDistanceEucl(coordinate,getExploredCenter());
//			if (distance > maxDistance) {
//				furtherestPoint = coordinate;
//			}
//		}
//		if (furtherestPoint == null)
////			System.out.println(coordinates);
//			System.out.println("No max distance is found!"); 
//		return furtherestPoint;
//	}

//	private static Coordinate getRandomLocation(ArrayList<Coordinate> coordinates){
//		Random randomGenerator = new Random();
//		int randomSelect = randomGenerator.nextInt(coordinates.size());
//		return coordinates.get(randomSelect);
//	}

//	private void updateAstar() {
//		
//	}
//	
//	private static Coordinate getExploredCenter() {
//		double sumX = 0.0;
//		double sumY = 0.0;
//		for (Coordinate see:seens) {
//			sumX += see.x;
//			sumY += see.y;
//		}
//		int meanX = (int) sumX/seens.size();
//		int meanY = (int) sumY/seens.size();
//		return new Coordinate(meanX, meanY);
//	}
}
