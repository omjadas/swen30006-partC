package mycontroller.movestrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mycontroller.AStar;
import mycontroller.util.util;
import tiles.MapTile;
import utilities.Coordinate;

public class NormalStrategy implements Pathable {
	
	private float health;
	private ArrayList<Coordinate> foundKeys;
	private int collectedKeys;
	private int unreachable = 0;
	
	public NormalStrategy() {}
	
	public NormalStrategy(float health, ArrayList<Coordinate> foundKeys, int collectedKeys) {
		this.health = health;
		this.foundKeys = foundKeys;
		this.collectedKeys = collectedKeys;
	}

	public void update(float health, ArrayList<Coordinate> foundKeys, int collectedKeys) {
		this.health = health;
		this.foundKeys = foundKeys;
		this.collectedKeys = collectedKeys;
	}
	
	@Override
	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, Coordinate from) {
		Coordinate destination;
		if ((collectedKeys + unreachable) < foundKeys.size()) {
			destination = foundKeys.get(collectedKeys + unreachable);
		} else {
			destination = util.getFinal(map);
		}
		
		System.out.println(from);
		System.out.println(destination);
		ArrayList<Coordinate> path = (ArrayList<Coordinate>) AStar.getPath(map, from, destination);
		System.out.println(path);
		System.out.println("\n");
		if (path == null) {
			unreachable += 1;
			return null;
		}
		
		Collections.reverse(path);
		
		int healthNeeded = 0;
		
		System.out.println(path);
		System.out.println("\n");
		
		for (Coordinate tile : path) {
			if (util.getTrapType(map, tile).equals("lava")) {
				healthNeeded += 5;
			}
		}
		
		if ((healthNeeded * 2) >= health) {
			path = getHealth(map, from);
		}
		
		return path;
	}
	
	private ArrayList<Coordinate> getHealth(HashMap<Coordinate, MapTile> map, Coordinate from) {
		ArrayList<Coordinate> locations = util.getHealthLocations(map);
		ArrayList<Coordinate> path = null;
		
		for (Coordinate location : locations) {
			path = (ArrayList<Coordinate>) AStar.getPath(map, from, location);
			Collections.reverse(path);
			int healthNeeded = 0;
			for (Coordinate tile : path) {
				if (util.getTrapType(map, tile).equals("lava")) {
					healthNeeded += 5;
				}
			}
			if ((healthNeeded * 2) >= health) {
				continue;
			}
			break;
		}
		return path;
	}
}
