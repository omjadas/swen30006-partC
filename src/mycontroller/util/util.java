package mycontroller.util;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;

public class util {
	
	private static HashMap<Coordinate, MapTile> map;
	
	protected static float getDistanceEucl(Coordinate from, Coordinate to) {
		return (float) Math.pow(Math.abs(Math.pow(from.x - to.x, 2)) + Math.pow(from.y - to.y, 2), 0.5);		
	}
	
	public static float getDistanceManh(Coordinate from, Coordinate to) {
		return (float) (Math.abs(to.x - from.x) + Math.abs(to.y - from.y));
	}
	
	public static String getTrapType (HashMap<Coordinate, MapTile> map, Coordinate coordinate) {
		MapTile mapTile = map.get(coordinate);
		if (mapTile != null) {
            TrapTile trapTile = (TrapTile) mapTile;

            return trapTile.getTrap();
        }
        return null;
	}
	
	public static ArrayList<Coordinate> getAllNeighbours(Coordinate position){
		final Coordinate[] allNeighbours = {
				new Coordinate(position.x+1,position.y),
				new Coordinate(position.x,position.y+1),
				new Coordinate(position.x-1,position.y),
				new Coordinate(position.x,position.y-1)
		};
		
		final ArrayList<Coordinate> validNeighbours = new ArrayList<>();
		
		for (Coordinate neighbour: allNeighbours) {
			String neighbourType = getTrapType(map,neighbour);
			if (map.containsKey(neighbour) && neighbourType != "Wall") {
				validNeighbours.add(neighbour);
			}
			
			return validNeighbours;
		}
		
		
		return null;
		
		
	}
	
	

}
