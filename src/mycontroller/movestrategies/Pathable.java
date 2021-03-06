package mycontroller.movestrategies;

import java.util.HashMap;
import java.util.List;
	
import tiles.MapTile;
import utilities.Coordinate;

// an interface for all strategies.
public interface Pathable {
	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, Coordinate from);
}
