package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;

public class PathFinder {
	
	private static ArrayList<Coordinate> exploredNodes;
	private static ArrayList<Coordinate> unexploredNodes;
	private static float cost;
	
	protected static ArrayList<Coordinate> getPath(HashMap<Coordinate, MapTile> map, 
            Coordinate from, Coordinate to){
		// initial data
        exploredNodes = new ArrayList<>();
        unexploredNodes = new ArrayList<>();
        cost = (float) 0.0;
        
        while(unexploredNodes.size()>0) {
        	Coordinate nextNode = null;
        	
        	unexploredNodes.remove(nextNode);
        	exploredNodes.add(nextNode);
        	
        }
		
		
				return null;
		
		
	}
	


}
