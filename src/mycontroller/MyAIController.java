package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import controller.CarController;
import mycontroller.movestrategies.ExploreStrategy;
import mycontroller.movestrategies.HealingStrategy;
import mycontroller.movestrategies.NormalStrategy;
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
	
	// initialise the parameters
	HashMap<Coordinate, MapTile> map = super.getMap();// get the raw map
	private Coordinate currentPosition;
	private boolean initiate = true;
	private ArrayList<Coordinate> keysOrdered = new ArrayList<Coordinate>();
	private NormalStrategy normalStrategy;
	private ExploreStrategy exploreStrategy;
	private int badEngineCount = 0;
	
	public MyAIController(Car car) {
		super(car);
		// define different strategies
		normalStrategy = new NormalStrategy(numKeys());
		exploreStrategy = new ExploreStrategy();
	}

	@Override
	public void update() {
		boolean exploring = false; // if the current strategy is exploring
		boolean normal = false; // if the current strategy is normal
		ArrayList<Coordinate> path = new ArrayList<>(); // the path to be executed
		currentPosition  = new Coordinate(getPosition());
		updateWorldMap(getView()); // update the map to have the latest view
		
		// determine whether to explore
		if (util.getKeyLocations(map).size() == 0 && numKeys() != 0) { // check if keys have been located
			exploring = true;
		} else {
			for (Coordinate key : util.getKeyLocations(map)) { // if found some keys already
				if (!keysOrdered.contains(key)) {
					keysOrdered.add(key); // add all the keys locations
				}
			}
			normal = true;
		}
					
		
		if (exploring) {
			// get path from explore strategy
			path = (ArrayList<Coordinate>) exploreStrategy.getPath(map, currentPosition);
		} else if (normal) {
			// if lava is blocking the way, and the side of the map is completed, then move to the other side of the lava no matter how fast lava is
			// update the internal parameters in normalStrategy
			normalStrategy.update(getHealth(), keysOrdered, getKeys().size());
			path = (ArrayList<Coordinate>) normalStrategy.getPath(map, currentPosition);
			if (path == null) {
				// look for more keys
				path = (ArrayList<Coordinate>) exploreStrategy.getPath(map, currentPosition);
			}
		}
		
		System.out.println(path);
		System.out.println(util.getTrapType(map, currentPosition));

		if (path != null) {
			int healthNeeded = 0;
			for (Coordinate tile : path) {
				if (util.getTrapType(map, tile).equals("lava")) {
					healthNeeded += 5;
				}
			}
			if (!path.get(path.size()-1).equals(util.getFinal(map))) {
				healthNeeded *= 2;
			}
			if (healthNeeded > getHealth()) {
				System.out.println("getting health");
				path = (ArrayList<Coordinate>) new HealingStrategy(getHealth(), path).getPath(map, currentPosition);
			}
		}
		
		
		// wait for full health
		for (Coordinate location : util.getHealthLocations(map)) {
			if (currentPosition.equals(location) && getHealth() < 100.0) {
				path.clear();
				path.add(currentPosition);
				path.add(currentPosition);
				break;
			}
		}
		
		
		// we only need the nextStep from the path for the car to move to
		Coordinate nextStep = null;
		if (path != null && path.size()>1) {
			currentPosition = new Coordinate(getPosition());	
			nextStep = path.get(1);// 0 is current location, 1 is the next steo
			startMyCar();// start car engine after a brake
			if (getSpeed()<1 && !nextStep.equals(currentPosition)) {
				badEngineCount++;
				System.out.println(badEngineCount);
			}
			if (badEngineCount>40) { // if the car brakes 50 times in this game
				System.exit(0); // quit the game
			}
			move(currentPosition, nextStep); // move the car from current location to the next location
			updateWorldMap(getView()); // update the map again after the move
		}else {
			System.out.println("No possible way to go. Map may be wrong!"); // this should only happen when win
		}
	}
	
	
	private void updateWorldMap(HashMap<Coordinate, MapTile> view) {
		map.putAll(view);
	}
	
	private void startMyCar() {
		// to determine if the car is stuck when it first starts
		if(initiate) { 
			MapTile front_mapTile = map.get(util.getNeighbourCoordinate(currentPosition,getOrientation()));
			MapTile back_mapTile = map.get(util.getNeighbourCoordinate(currentPosition,util.reverseOrientation(getOrientation())));
			String front_trapType = "";
			String back_trapType = "";
			
			// before speed up, check maptile ahead
			if (front_mapTile.isType(Type.TRAP)) {
				front_trapType = util.getTrapType(map,util.getNeighbourCoordinate(currentPosition,getOrientation()));
			}
			if (back_mapTile.isType(Type.TRAP)) {
				back_trapType = util.getTrapType(map,util.getNeighbourCoordinate(currentPosition,util.reverseOrientation(getOrientation())));
			}
			// if the front of the car is not wall or mud
			if (front_mapTile.isType(Type.WALL) || front_trapType.equals("mud")) {
				applyReverseAcceleration();// apply backward speed
			}else if(back_mapTile.isType(Type.WALL) || back_trapType.equals("mud")){
				applyForwardAcceleration();// apply forward speed 
			}else {
				if(Math.random() < 0.5) {
					applyForwardAcceleration();// apply forward speed 
				}else {
					applyReverseAcceleration();// apply backward speed
				}
			}
			initiate = false;// now the car is initialised
		}else {
			// the car is initialised, but stops for key or health
			if (getHealth() == 100 && map.get(currentPosition).isType(Type.TRAP)) {
				// start engine again if completed the job at that location
				initiate = true;	
			}
		}
	}
	
	// the move function, move he car from current to next
	private void move(Coordinate current, Coordinate next) {
		// if the car wants to stop
		if (current.equals(next)) {
			applyBrake(); // apply brakes
		} else {
			// check the orientation the car wants to go based on current and next location
			Direction nextOrientation = getNextOrientation(current,next); 
			go(nextOrientation,getOrientation()); // apply actions to the car to follow the correct orientation
		}
	}

	// check the orientation to the next from current location
	private Direction getNextOrientation(Coordinate current, Coordinate next) {
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
	
	// apply the moving action to the car
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
				break;
			}
		}else if (input.equals(WorldSpatial.Direction.SOUTH)) {
			switch(orientation){
			case EAST:
				turnRight();
				break;
			case NORTH:
				applyReverseAcceleration();
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
