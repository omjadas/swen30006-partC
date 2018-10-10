package mycontroller.util;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import tiles.TrapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;

public class util {
	
//	private static HashMap<Coordinate, MapTile> map;
	
	protected static float getDistanceEucl(Coordinate from, Coordinate to) {
		return (float) Math.pow(Math.abs(Math.pow(from.x - to.x, 2)) + Math.pow(from.y - to.y, 2), 0.5);		
	}
	
	public static float getDistanceManh(Coordinate from, Coordinate to) {
		return (float) (Math.abs(to.x - from.x) + Math.abs(to.y - from.y));
	}
	
	public static String getTrapType (HashMap<Coordinate, MapTile> map, Coordinate coordinate) {
		MapTile mapTile = map.get(coordinate);
		if (mapTile != null) {
			if (!mapTile.isType(Type.TRAP)) {
				return mapTile.getType().toString();
	        }else {
	        	TrapTile trapTile = (TrapTile) mapTile;
	        	return trapTile.getTrap();
	        }
		}
		return null;
	}
	
	public static HashMap<Coordinate, String> getAllNeighbours(HashMap<Coordinate, MapTile> map, Coordinate position){
		final Coordinate[] allNeighbours = {
				new Coordinate(position.x+1,position.y),
				new Coordinate(position.x,position.y+1),
				new Coordinate(position.x-1,position.y),
				new Coordinate(position.x,position.y-1)
		};
		
		final HashMap<Coordinate, String> validNeighbours = new HashMap<Coordinate, String>();
		
		for (Coordinate neighbour: allNeighbours) {
			String neighbourType = getTrapType(map,neighbour);
			validNeighbours.put(neighbour,neighbourType);
		}
		return validNeighbours;
	}
}
