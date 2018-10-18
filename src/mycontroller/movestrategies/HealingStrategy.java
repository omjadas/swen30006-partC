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
	private ArrayList<Coordinate> current;
	
	public HealingStrategy(float health, ArrayList<Coordinate> current) {
		this.health = health;
		this.current = current;
	}
	
	@Override
	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, Coordinate from) {
		ArrayList<Coordinate> locations = util.getHealthLocations(map);
		ArrayList<Coordinate> path = null;
		
		if (locations.size() == 0) {
			return current;
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
//		System.out.println(path);but 
		return path;
	}
}
