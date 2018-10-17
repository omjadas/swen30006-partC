package mycontroller.movestrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import mycontroller.AStar;
import mycontroller.util.util;
import tiles.MapTile;
import utilities.Coordinate;

public class NormalStrategy implements Pathable {
	
	private float health;
	private ArrayList<Coordinate> foundKeys;
	private int collectedKeys;
	private int unreachable = 0;
	private int totalKeys;
	private boolean isStop = false;
	
	public NormalStrategy(int totalKeys) {
		this.totalKeys = totalKeys;
	}

	public void update(float health, ArrayList<Coordinate> foundKeys, int collectedKeys) {
		this.health = health;
		this.foundKeys = foundKeys;
		this.collectedKeys = collectedKeys;
	}
	
	@Override
	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, Coordinate from) {
		ArrayList<Coordinate> path = new ArrayList<Coordinate>();
		Coordinate destination;
		int healthMultiplier = 2;
		if ((collectedKeys + unreachable) < foundKeys.size()) {
			destination = foundKeys.get(collectedKeys + unreachable);
		} else if (collectedKeys < totalKeys) {
			return null;
		} else {
			destination = util.getFinal(map);
			healthMultiplier = 1;
		}
		
		// wait for full health
		for (Coordinate location : util.getHealthLocations(map)) {
			if (from.equals(location) && health < 100.0) {
				path.add(from);
				// check if neighbours are healths too
				HashMap<Coordinate, String> allNeighbours = util.getAllNeighbours(map,from);
				for (Entry<Coordinate, String> neighbour:  allNeighbours.entrySet()) {
					// if the neighbour is also a health
					if(neighbour.getValue()=="health") {
						path.add(neighbour.getKey()); // add neighbour as the next step
						break;
					}
				} // this is to avoid brakes and initialisation
				
				if(path.size()==1) {// if this is the only health
					path.add(from); // brake the engine and initial engine later
				}
				return path;
			}
		}
		
		path = (ArrayList<Coordinate>) AStar.getPath(map, from, destination);

		if (path == null) {
			unreachable += 1;
			return getPath(map, from);
		}
		
		Collections.reverse(path);
		
		int healthNeeded = 0;
		for (Coordinate tile : path) {
			if (util.getTrapType(map, tile).equals("lava")) {
				healthNeeded += 5;
			}
		}
		
		healthNeeded *= healthMultiplier;
		
		if (healthNeeded >= health) {
			path = getHealth(map, from, healthNeeded);
		}
		
		
		return path;
	}
	
	private ArrayList<Coordinate> getHealth(HashMap<Coordinate, MapTile> map, Coordinate from, int need) {
		ArrayList<Coordinate> locations = util.getHealthLocations(map);
		ArrayList<Coordinate> path = null;
		
		System.out.println("need health");
		
		for (Coordinate location : locations) {
			path = (ArrayList<Coordinate>) AStar.getPath(map, from, location);
			Collections.reverse(path);
			int healthNeeded = 0;
			for (Coordinate tile : path) {
				if (util.getTrapType(map, tile).equals("lava")) {
					healthNeeded += 5;
				}
			}
			if ((healthNeeded) >= health) {
				continue;
			}
			for (int i = need ; i >=0 ; i-=1) {
				path.add(location);
			}
			break;
		}
		return path;
	}
}
