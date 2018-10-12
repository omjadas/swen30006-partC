package mycontroller.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.WorldSpatial.Direction;

public class util {
	
//	private static HashMap<Coordinate, MapTile> map;
	
	public static float getDistanceEucl(Coordinate from, Coordinate to) {
		return (float) Math.pow(Math.abs(Math.pow(from.x - to.x, 2)) + Math.pow(from.y - to.y, 2), 0.5);		
	}
	
	public static float getDistanceManh(Coordinate from, Coordinate to) {
		return (float) (Math.abs(to.x - from.x) + Math.abs(to.y - from.y));
	}
	
	public static String getTrapType (HashMap<Coordinate, MapTile> map, Coordinate coordinate) {
		MapTile mapTile = map.get(coordinate);
//		System.out.println(mapTile.getType().toString());
		if (mapTile != null) {
			if (!mapTile.isType(Type.TRAP)) {
				return mapTile.getType().toString();
	        }else {
	        	TrapTile trapTile = (TrapTile) mapTile;
	        	return trapTile.getTrap();
	        }
		}else {
			return "WALL"; // need work here
		}
//		return null;
	}
	
	public static HashMap<Coordinate, String> getAllNeighbours(HashMap<Coordinate, MapTile> map, Coordinate position){

		final ArrayList<Coordinate> allNeighbours = new ArrayList<Coordinate>();
		allNeighbours.add(getNeighbourCoordinate(position,Direction.EAST));
		allNeighbours.add(getNeighbourCoordinate(position,Direction.SOUTH));
		allNeighbours.add(getNeighbourCoordinate(position,Direction.WEST));
		allNeighbours.add(getNeighbourCoordinate(position,Direction.NORTH));
		
		final HashMap<Coordinate, String> myNeighbours = new HashMap<Coordinate, String>();
		
		for (Coordinate neighbour: allNeighbours) {
			String neighbourType = getTrapType(map,neighbour);
			myNeighbours.put(neighbour,neighbourType);
		}
		return myNeighbours;
	}
	
	public static Coordinate getNeighbourCoordinate(Coordinate from, Direction direction) {
//		System.out.println("in "+from+direction);
		Coordinate to = null;
		switch(direction){
		case EAST:
			to = new Coordinate(from.x+1,from.y);
			break;
		case NORTH:
			to = new Coordinate(from.x,from.y+1);
			break;
		case SOUTH:
			to = new Coordinate(from.x,from.y-1);
			break;
		case WEST:
			to = new Coordinate(from.x-1,from.y);
			break;
		}
//		System.out.println("out "+to);
		// TODO Auto-generated method stub
		return to;
	}
	
	public static Coordinate getFinal(HashMap<Coordinate, MapTile> map) {
		for (Entry<Coordinate, MapTile> entry:map.entrySet()) {
			if (entry.getValue().getType().equals(Type.FINISH)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public static ArrayList<Coordinate> getKeyLocations(HashMap<Coordinate, MapTile> map) {
		ArrayList<Coordinate> keys = new ArrayList<Coordinate>();
		for (Entry<Coordinate, MapTile> entry:map.entrySet()) {
			if (entry.getValue().getType().equals(Type.TRAP)) {
				TrapTile trap = (TrapTile) entry.getValue();
				if (trap.getTrap() == "lava") {
					LavaTrap lava = (LavaTrap) trap;
					if (lava.getKey()>0) {
						keys.add(entry.getKey());
					}
				}
			}
		}
		return keys;
	}
	
	public static ArrayList<Coordinate> getHealthLocations(HashMap<Coordinate, MapTile> map) {
		ArrayList<Coordinate> healths = new ArrayList<Coordinate>();
		for (Entry<Coordinate, MapTile> entry:map.entrySet()) {
			if (entry.getValue().getType().equals(Type.TRAP)) {
				TrapTile trap = (TrapTile) entry.getValue();
				if (trap.getTrap() == "health") {
					healths.add(entry.getKey());
				}
			}
		}
		return healths;
	}
}
