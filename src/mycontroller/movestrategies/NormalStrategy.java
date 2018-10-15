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

	@Override
	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, Coordinate from) {
//		First going to the destination
		Coordinate destination = util.getFinal(map);
		
		ArrayList<Coordinate> path = (ArrayList<Coordinate>) AStar.getPath(map,from,destination);
		
		Collections.reverse(path);
		
		return path;
	}

}
