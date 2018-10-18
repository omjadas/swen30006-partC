package mycontroller.movestrategies;

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
	
	public void update(float health, ArrayList<Coordinate> current) {
		this.health = health;
		this.current_path = current;
	}
	
	@Override
	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, Coordinate from) {
		
		ArrayList<Coordinate> path = new ArrayList<>();
		ArrayList<ArrayList<Coordinate>> healthPaths = new ArrayList<>();
		for (Coordinate location : util.getHealthLocations(map)) {
			healthPaths.add(AStar.getPath(map, from, location));
		}
		if (healthPaths.size() > 0) {
			path = Collections.min(healthPaths, (ArrayList<Coordinate> p1, ArrayList<Coordinate> p2) -> {
				return healthNeeded(map, p1) - healthNeeded(map, p2);
			});
			if ((100 - healthNeeded(map, path)) > (int) health && (int) health > healthNeeded(map, path)) {
				Collections.reverse(path);
				return path;
			}
		}
		return current_path;
	}
	
	private int healthNeeded(HashMap<Coordinate, MapTile> map, ArrayList<Coordinate> path) {
		int healthNeeded = 0;
		for (Coordinate tile : path) {
			if (util.getTrapType(map, tile).equals("lava")) {
				healthNeeded += 5;
			}
		}
		return healthNeeded;
	}
	
	private List<Coordinate> getHealth(HashMap<Coordinate, MapTile> map, Coordinate from) {
		ArrayList<Coordinate> locations = util.getHealthLocations(map);
		ArrayList<Coordinate> path = null;
		
		if (locations.size() == 0) {
			return current_path;
		}
		
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
			break;
		}
		return path;
	}
}
