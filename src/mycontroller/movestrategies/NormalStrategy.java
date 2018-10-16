package mycontroller.movestrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
		System.out.println("\n");
		Coordinate destination;
		int healthMultiplier = 2;
		if ((collectedKeys + unreachable) < foundKeys.size()) {
			destination = foundKeys.get(collectedKeys + unreachable);
		} else {
			destination = util.getFinal(map);
			healthMultiplier = 1;
		}
		
		System.out.println(from);
		System.out.println(destination);
		ArrayList<Coordinate> path = (ArrayList<Coordinate>) AStar.getPath(map, from, destination);
		System.out.println(path);

		if (path == null) {
			unreachable += 1;
			return null;
		}
		
		Collections.reverse(path);
		
		int healthNeeded = 0;
		for (Coordinate tile : path) {
			if (util.getTrapType(map, tile).equals("lava")) {
				healthNeeded += 5;
			}
		}
		
		if ((healthNeeded * healthMultiplier) >= health) {
			path = getHealth(map, from);
		}
		
		return path;
	}
	
	private ArrayList<Coordinate> getHealth(HashMap<Coordinate, MapTile> map, Coordinate from) {
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
			break;
		}
		return path;
	}
}
