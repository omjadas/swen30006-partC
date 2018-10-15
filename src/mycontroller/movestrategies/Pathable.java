package mycontroller.movestrategies;

import java.util.HashMap;
import java.util.List;
	
import tiles.MapTile;
import utilities.Coordinate;


public interface Pathable {
	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, Coordinate from);
}
