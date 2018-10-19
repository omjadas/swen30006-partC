package mycontroller.movestrategies;

//Group 40

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mycontroller.AStar;
import mycontroller.util.util;
import tiles.MapTile;
import utilities.Coordinate;

public class HealingStrategy implements Pathable {

	private float health;
	private ArrayList<Coordinate> current_path;
	
	// update healing strategy parameters
	public void update(float health, ArrayList<Coordinate> current) {
		this.health = health;
		this.current_path = current; // we want to keep the current path option so we can compare later
	}
	
	@Override
	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, Coordinate from) {
		
		ArrayList<Coordinate> path = new ArrayList<>();
		ArrayList<ArrayList<Coordinate>> healthPaths = new ArrayList<>();
		
		// find all paths to the known healths location
		for (Coordinate location : util.getHealthLocations(map)) {
			healthPaths.add(AStar.getPath(map, from, location));
		}
		if (healthPaths.size() > 0) {
			// find the shortest distance to the health
			path = Collections.min(healthPaths, (ArrayList<Coordinate> p1, ArrayList<Coordinate> p2) -> {
				return healthNeeded(map, p1) - healthNeeded(map, p2);
			});
			// only go to health if
			// 1, the car would have more healths if it went back to get health and came back to the same location
			// 2, the car had enough health to go back
			if ((100 - healthNeeded(map, path)) > (int) health && (int) health > healthNeeded(map, path)) {
				Collections.reverse(path);
				return path;
			}
		}
		return current_path;
	}
	
	// calculate the health required to follow the given path
	private int healthNeeded(HashMap<Coordinate, MapTile> map, ArrayList<Coordinate> path) {
		int healthNeeded = 0;
		for (Coordinate tile : path) {
			if (util.getTrapType(map, tile).equals("lava")) {
				healthNeeded += 5; // the cost to step on lava 
			}
		}
		return healthNeeded;
	}
}
