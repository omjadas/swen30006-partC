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
	
	// distance using euclidean method
	public static float getDistanceEucl(Coordinate from, Coordinate to) {
		return (float) Math.pow(Math.abs(Math.pow(from.x - to.x, 2)) + Math.pow(from.y - to.y, 2), 0.5);		
	}
	
	// distance using manhattan method
	public static float getDistanceManh(Coordinate from, Coordinate to) {
		return (float) (Math.abs(to.x - from.x) + Math.abs(to.y - from.y));
	}
	
	// obtain the reversal direction of the given direction
	public static Direction reverseOrientation(Direction orientation) {
		if (orientation.equals(Direction.EAST))
			return Direction.WEST;
		else if (orientation.equals(Direction.WEST))
			return Direction.EAST;
		else if (orientation.equals(Direction.NORTH))
			return Direction.SOUTH;
		else
			return Direction.NORTH;
	}
	
	// universal tile type retriever
	public static String getTrapType(HashMap<Coordinate, MapTile> map, Coordinate coordinate) {
		MapTile mapTile = map.get(coordinate);
		if (mapTile != null) {
			if (!mapTile.isType(Type.TRAP)) {
				return mapTile.getType().toString(); // return String WALL/ROAD
	        }else {
	        	TrapTile trapTile = (TrapTile) mapTile;
	        	return trapTile.getTrap(); // return String mud/grass/lava/health
	        }
		}else {
			return "WALL"; // anything outside of the map are treated as walls so never go there
		}
	}
	
	// check surrounding to find out the surrounding map tile types given a radius
	public static HashMap<Coordinate, String> getNearby(HashMap<Coordinate, MapTile> map, Coordinate position, int radius){
		if (radius == 1) {
			return getAllNeighbours(map, position);
		}
		HashMap<Coordinate, String> nearby = new HashMap<Coordinate, String>();
		for (Entry<Coordinate, String> coord : getAllNeighbours(map, position).entrySet()) {
	        Coordinate coordinate = (Coordinate) coord.getKey();
	        nearby.putAll(getNearby(map, coordinate, radius - 1));
		}
		return nearby;
	}
	
	// a special case of getNearby when radius is 1, so neighbours
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
	
	// get the coordinates of neighbous (radius = 1)
	public static Coordinate getNeighbourCoordinate(Coordinate from, Direction direction) {
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
		return to;
	}
	
	// get a direction from current position to the next position
	public static Direction getMyDirection(Coordinate from, Coordinate to) {
		if (to.x>from.x && to.y==from.y) {
			return Direction.EAST;
		}else if(to.x<from.x && to.y==from.y) {
			return Direction.WEST;
		}else if(to.x==from.x && to.y>from.y) {
			return Direction.NORTH;
		}else if(to.x==from.x && to.y<from.y) {
			return Direction.SOUTH;
		}
		return null;
	}
	
	// get the coordinate for FINAL
	public static Coordinate getFinal(HashMap<Coordinate, MapTile> map) {
		for (Entry<Coordinate, MapTile> entry:map.entrySet()) {
			if (entry.getValue().getType().equals(Type.FINISH)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	// get the coordinate for START
	public static Coordinate getStart(HashMap<Coordinate, MapTile> map) {
		for (Entry<Coordinate, MapTile> entry:map.entrySet()) {
			if (entry.getValue().getType().equals(Type.START)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	// get a list of coordinates for keys been discovered
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
	
	// get a list of coordinates for healths been discovered
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
