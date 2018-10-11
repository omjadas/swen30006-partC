package mycontroller.movestrategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import mycontroller.util.util;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class ExploreStrategy implements Pathable{

	public static List<Coordinate> getPath(HashMap<Coordinate, MapTile> map, 
            Coordinate from) {
		
		ArrayList<Direction> directions = new ArrayList<Direction>();
		directions.add(Direction.EAST);
		directions.add(Direction.SOUTH);
		directions.add(Direction.WEST);
		directions.add(Direction.NORTH);
		
		ArrayList<Coordinate> paths = checkRoads(map, from);
//		System.out.println(areRoads);
		

		System.out.println(paths); 
		// random choose one path and return
		
		// TODO Auto-generated method stub
		return paths;
	}

	@Override
	public List<Coordinate> getPath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static ArrayList<Coordinate> checkRoads(HashMap<Coordinate, MapTile> map, Coordinate currentPosition){
		// Check tiles if they are safe to move to
		HashMap<Coordinate, String> neighbours = util.getAllNeighbours(map, currentPosition);
//		System.out.println(neighbours);
		ArrayList<Coordinate> areRoads = new ArrayList<Coordinate>();
		
		for (Entry<Coordinate, String> entry : neighbours.entrySet()) {
			if (entry.getValue().equals("ROAD")) {
				areRoads.add(entry.getKey());
			}
		}
		return areRoads;
	}

}
