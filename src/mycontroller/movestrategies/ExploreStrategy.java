package mycontroller.movestrategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import mycontroller.util.util;
import tiles.MapTile;
import utilities.Coordinate;

public class ExploreStrategy implements Pathable{

	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, 
            Coordinate from, Coordinate to) {
		
//		ArrayList<Boolean> areRoads = checkRoads(currentView, Coordinate currentPosition)
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Coordinate> getPath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ArrayList<Boolean> checkRoads(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition){
		// Check tiles if they are safe to move to
		HashMap<Coordinate, String> neighbours = util.getAllNeighbours(currentView, currentPosition);
		ArrayList<Boolean> areRoads = new ArrayList<Boolean>();
		
		for (Entry<Coordinate, String> entry : neighbours.entrySet()) {
			if (entry.getValue().equals("ROAD")) {
				areRoads.add(true);
			}
			else {
				areRoads.add(false);
			}
		}
		return areRoads;
	}

}
