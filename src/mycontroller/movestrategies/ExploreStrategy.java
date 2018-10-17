package mycontroller.movestrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
import world.World;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class ExploreStrategy implements Pathable{
//	static ArrayList<Coordinate> seens = new ArrayList<Coordinate>();
	private ArrayList<Coordinate> notSeen = new ArrayList<>();
	Coordinate closest_old;
	static HashMap<Coordinate,Integer> visits = new HashMap<Coordinate,Integer>();
//	static HashMap<Coordinate, MapTile> incompleteMap = new HashMap<Coordinate, MapTile>();

	public ExploreStrategy() {
		for(int x = 0 ; x < World.MAP_WIDTH; x++) {
			for(int y = 0 ; y < World.MAP_HEIGHT ; y++) {
				notSeen.add(new Coordinate(x,y));
			}
		}
	}
	
	@Override
	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, 
            Coordinate from) {
	
//		System.out.println(notSeen.size());
		
		for(int x = from.x - 4; x<=from.x+4;x++) {
			for(int y = from.y -4 ; y<=from.y+4;y++) {
				if((x>0 && y>0) && (x < World.MAP_WIDTH && y < World.MAP_HEIGHT)) {
					notSeen.remove(new Coordinate(x,y));
				}
			}
		}
		
//		updateExplored(view);
//		updateMap(view);
		ArrayList<Coordinate> allValidRoads = getRoadsInView(map, from);
//		HashMap<Coordinate,ArrayList<Coordinate>> validRoads = new HashMap<Coordinate,ArrayList<Coordinate>>();
		ArrayList<Coordinate> current_path = new ArrayList<>();
		ArrayList<Coordinate> available_path = new ArrayList<>();
		Coordinate nextStep = null;
		Random randomGenerator = new Random();
//		System.out.println(from);
		Coordinate closest;
		if (!notSeen.contains(closest_old)) {
			closest = Collections.min(notSeen, (Coordinate c1, Coordinate c2) -> {
				return (Math.abs(from.x - c1.x) + Math.abs(from.y - c1.y)) - (Math.abs(from.x - c2.x) + Math.abs(from.y - c2.y));
			});
			closest_old = closest;
		}else {
			closest = closest_old;
		}
		
		List<Coordinate> path = null;
//		System.out.println(from);
//		System.out.println(notSeen);
//		System.out.println(closest);
//		System.out.println(util.getTrapType(map, closest));
//		System.out.println(notSeen.size());
//		System.out.println("\n");
		
		
		if (map.get(closest).isType(Type.ROAD)) {
			path = AStar.getPath(map, from, closest);
			if (path==null) {
				notSeen.remove(closest);
				path = getPath(map, from);
			}else {
				Collections.reverse(path);
			}
		} else {
			notSeen.remove(closest);
			path = getPath(map, from);
		}
		
		
//		System.out.println(path);
		return path;
		
//		// if key was just found the shortest path out of the lava is chosen
//		if (allValidRoads.size() == 0) {
//			ArrayList<ArrayList<Coordinate>> paths = new ArrayList<>();
//			for (Coordinate road : getNearbyRoads(map, from)) {
//				paths.add(AStar.getPath(map, from, road));
//			}
//			paths.removeAll(Collections.singleton(null));
//			return Collections.min(paths, (ArrayList<Coordinate> p1, ArrayList<Coordinate> p2) -> {
//				int p1l = 0;
//				int p2l = 0;
//				System.out.println(p1);
//				for (Coordinate coord : p1) {
//					if (util.getTrapType(map, coord).equals("lava")) {
//						p1l += 1;
//					}
//				}
//				for (Coordinate coord : p2) {
//					if (util.getTrapType(map, coord).equals("lava")) {
//						p2l += 1;
//					}
//				}
//				return p1l - p2l;
//			});
//		}
//	
//		
//		for (Coordinate validRoad: allValidRoads) {
//			if (!visits.containsKey(validRoad)) {
//				visits.put(validRoad,0);
//				available_path.add(validRoad);
//			}
//		}
//		if (available_path.size()>0) {
////			int index = randomGenerator.nextInt(available_path.size());
//			nextStep = available_path.get(0);
//		} else {
//			Coordinate destination =  util.getFinal(map);
//			// if this car has not visited the final
//			if(!visits.containsKey(destination)) {
//				// use A star to go to the final destination to stay away from the familiar regions
//				ArrayList<Coordinate> path = (ArrayList<Coordinate>) AStar.getPath(map, from, util.getFinal(map));
//				if (path.size()>1) {
//					nextStep = path.get(path.size()-2); // move to the next step given by A star
//				}
////				System.out.println(""+from+nextStep);
//			}
//		}
//		if (nextStep==null) { // use random walk selected from previously visited mpaTiles.
//			int index = randomGenerator.nextInt(allValidRoads.size());
//			nextStep = allValidRoads.get(index);
//		}
//		
//		System.out.println(nextStep);
//		
//		current_path.add(from);
//		current_path.add(nextStep);
////		System.out.println(current_path);
//		if (!visits.containsKey(from)) {
//			visits.put(from,1);
//		}else {
//			visits.put(from,visits.get(from)+1);
//		}
//		return current_path;
	}
	
	
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
	    }
		return roads;
	}
}
