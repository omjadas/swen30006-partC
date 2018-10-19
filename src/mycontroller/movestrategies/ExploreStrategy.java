package mycontroller.movestrategies;

//Group 40

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mycontroller.AStar;
import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.Car;
import world.World;

public class ExploreStrategy implements Pathable{
	private ArrayList<Coordinate> notSeen = new ArrayList<>();
	private Coordinate furthest_old;
	private int viewSquare = Car.VIEW_SQUARE;

	// initial notSeen including every node in the map
	public ExploreStrategy() {
		for(int x = 0 ; x < World.MAP_WIDTH; x++) {
			for(int y = 0 ; y < World.MAP_HEIGHT ; y++) {
				notSeen.add(new Coordinate(x,y));
			}
		}
	}
	
	@Override
	public List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, 
            Coordinate from) {
	
		// remove all nodes in the current view from notSeen
		for(int x = from.x - viewSquare; x<=from.x + viewSquare;x++) {
			for(int y = from.y - viewSquare ; y<=from.y + viewSquare;y++) {
				if((x>0 && y>0) && (x < World.MAP_WIDTH && y < World.MAP_HEIGHT)) {
					notSeen.remove(new Coordinate(x,y));
				}
			}
		}

		// always explore the furthest coordinate from the current location
		Coordinate furthest;
		if (!notSeen.contains(furthest_old)) {	// check if there has already been a furthest location found before	
			furthest = Collections.max(notSeen, (Coordinate c1, Coordinate c2) -> { // from all notSeen, find the furthest
				return (Math.abs(from.x - c1.x) + Math.abs(from.y - c1.y)) - (Math.abs(from.x - c2.x) + Math.abs(from.y - c2.y));
			});
			furthest_old = furthest;
		} else { // if there has already been a furthest location found before		
			furthest = furthest_old; // go to this furthest location first before move on to the next
		}
		
		// get the path for exploration
		List<Coordinate> path = null;
		if (map.get(furthest).isType(Type.ROAD)) { // if the furthest node selected is a road from the raw map
			path = AStar.getPath(map, from, furthest); // use Astar to get the path to there
			if (path==null) { // if Astar cannot provide a valid path (deadend)
				notSeen.remove(furthest); // remove this node from the notSeen
				path = getPath(map, from); // get another furthest node
			}else {
				Collections.reverse(path); // reorder the path from Astar
			}
		} else {
			notSeen.remove(furthest); // if the furthest is not road (has to be wall)
			path = getPath(map, from); // get another furthest location
		}

		return path; 
	}
}
