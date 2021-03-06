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

public class NormalStrategy implements Pathable {
	
	// define parameters
	private ArrayList<Coordinate> foundKeys;
	private int collectedKeys;
	private int unreachable = 0;
	private int totalKeys;
	
	// initial the total keys in this game
	public NormalStrategy(int totalKeys) {
		this.totalKeys = totalKeys;
	}

	// update the strategy with latest info
	public void update(ArrayList<Coordinate> foundKeys, int collectedKeys) {
		this.foundKeys = foundKeys;
		this.collectedKeys = collectedKeys;
	}
	
	@Override
	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, Coordinate from) {
		ArrayList<Coordinate> path = new ArrayList<Coordinate>();
		Coordinate destination;
		
		// determine the destination 
		if ((collectedKeys + unreachable) < foundKeys.size()) {
			destination = foundKeys.get(collectedKeys + unreachable);
		} else if (collectedKeys < totalKeys) {
			// need to explore to find more keys
			return null;
		} else {
			// all keys found, go to exit
			destination = util.getFinal(map);
		}
				
		// get path to the destination using AStar
		path = (ArrayList<Coordinate>) AStar.getPath(map, from, destination);

		// if path is null key must be unreachable
		if (path == null) {
			unreachable += 1;
			return getPath(map, from);
		}
		
		Collections.reverse(path);		
		return path;
	}
}
