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
	private ArrayList<Coordinate> keys;
	private int collectedKeys;
	
	public NormalStrategy(float health, ArrayList<Coordinate> keys, int collectedKeys) {
		this.health = health;
		this.keys = keys;
		this.collectedKeys = collectedKeys;
	}

	@Override
	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, Coordinate from) {
		Coordinate destination;
		if (collectedKeys < keys.size()) {
			destination = keys.get(collectedKeys);
		} else {
			destination = util.getFinal(map);
		}
		
		ArrayList<Coordinate> path = (ArrayList<Coordinate>) AStar.getPath(map, from, destination);
		
		Collections.reverse(path);
		return path;
	}

}
