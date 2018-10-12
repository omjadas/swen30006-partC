package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;
import mycontroller.movestrategies.ExploreStrategy;
import mycontroller.util.util;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class MyAIController extends CarController{
	
	HashMap<Coordinate, MapTile> map = super.getMap();
	ArrayList<Coordinate> keys = new ArrayList<Coordinate>();
	private Coordinate currentPosition;
	

	public MyAIController(Car car) {
		super(car);
		
	}

	@Override
	public void update() {
			
		HashMap<Coordinate, MapTile> currentView = getView();
		if (getSpeed()<1) {
			applyForwardAcceleration();
		}
		currentPosition  = new Coordinate(getPosition());
		ArrayList<Coordinate> path = (ArrayList<Coordinate>) ExploreStrategy.getPath(map, currentPosition);
		
		if (path.size()>1) {
			Coordinate nextStep = path.get(path.size()-2);
			move(currentPosition,nextStep);
			
		}
		
		// if need health...
		ArrayList<Coordinate> healthLocations = util.getHealthLocations(map);
		if (getHealth()<100) {

		}

		ArrayList<Coordinate> keyLocations = util.getKeyLocations(map);
		if (keyLocations.size()>0) {
//			System.out.println("Found a key!");
		}
		
		// if explore to the destination, the car is still missing keys
		// need second exploring method to randomly walk the maze and try to find the key
		if (currentPosition.equals(util.getFinal(map))) {
			
		}

        updateWorldMap(currentView);
	}
	
	
	private void updateWorldMap(HashMap<Coordinate, MapTile> view) {
//		System.out.println("updating the map");
		map.putAll(view);
//		MapTile mapTile;
//		for (Coordinate coor: view.keySet()) {
//			mapTile = view.get(coor);
//			
//			if (mapTile.isType(Type.TRAP)) {
//				map.put(coor, mapTile);
//				if (util.getTrapType(map,coor)=="health") {
//					map.put(coor,mapTile);
//				}
//				
//				if (util.getTrapType(map,coor)=="grass") {
//					map.put(coor,mapTile);
//				}
//				
//				if (util.getTrapType(map,coor)=="mud") {
//					map.put(coor,mapTile);
//				}
//				
//				if (util.getTrapType(map,coor)=="lava") {
//					map.put(coor,mapTile);
//					int key = ((LavaTrap) mapTile).getKey();
//					if (key>0) {
//						keys.add(coor);
//					}
//				}
//			}
//		}
	}
	

	private void move(Coordinate current, Coordinate next) {
		if (current.equals(next)) {
			applyBrake();
			System.out.println("stopping the car");
		}else {
			Direction nextOrientation = getNextOrientation(current,next);
			go(nextOrientation,getOrientation());
		}
	}

	private Direction getNextOrientation(Coordinate current, Coordinate next) {
		// TODO Auto-generated method stub
		if (current.x - next.x > 0) {
			return Direction.WEST;
		}else if(current.x - next.x < 0) {
			return Direction.EAST;
		}else if(current.y - next.y > 0) {
			return Direction.SOUTH;
		}else {
			return Direction.NORTH;
		}		
	}
	

	public void go(Direction input, Direction orientation) {

		if (input.equals(WorldSpatial.Direction.NORTH)) {
			switch(orientation){
			case EAST:
				turnLeft();
				break;
			case NORTH:
				applyForwardAcceleration();
				break;
			case SOUTH:
				applyReverseAcceleration();
				applyBrake();
				break;
			case WEST:
				turnRight();
				break;
			}
		}else if (input.equals(WorldSpatial.Direction.EAST)) {
			switch(orientation){
			case EAST:
				applyForwardAcceleration(); 
				break;
			case NORTH:
				turnRight();
				break;
			case SOUTH:
				turnLeft();
				break;
			case WEST:
				applyReverseAcceleration();
				applyBrake();
				break;
			}
		}else if (input.equals(WorldSpatial.Direction.SOUTH)) {
			switch(orientation){
			case EAST:
				turnRight();
				break;
			case NORTH:
				applyReverseAcceleration();
				applyBrake();
				break;
			case SOUTH:
				applyForwardAcceleration();
				break;
			case WEST:
				turnLeft();
				break;
			}
		}else {
			switch(orientation){
			case EAST:
				applyReverseAcceleration();
				applyBrake();
				break;
			case NORTH:
				turnLeft();
				break;
			case SOUTH:
				turnRight();
				break;
			case WEST:
				applyForwardAcceleration();
				break;
			}
		}
	} 
}
