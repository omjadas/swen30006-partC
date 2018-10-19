package mycontroller;

//Group 40

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
	private HealingStrategy healingStrategy;
	private int badEngineCount = 0; // quite the game if staying at one place too long. Should never be used.
	
	public MyAIController(Car car) {
		super(car);
		
		// define different strategies
		normalStrategy = new NormalStrategy(numKeys());
		exploreStrategy = new ExploreStrategy();
		healingStrategy = new HealingStrategy();
	}

	@Override
	public void update() {
		
		ArrayList<Coordinate> path = new ArrayList<>(); // the path to be executed
		currentPosition  = new Coordinate(getPosition());
		
		updateWorldMap(getView()); // update the map to have the latest view
		path = strategySelector(); // select the best strategy
		execute(path); // execute the path from the strategy
	}

	private ArrayList<Coordinate> strategySelector() {
		// determine whether to explore
		boolean normal = false; // if the current strategy is normal
		ArrayList<Coordinate> path = new ArrayList<>();
		
		if (util.getKeyLocations(map).size() == 0 && numKeys() != 0) { // if no keys have been found
			normal = false; // do exploring
		} else {
			for (Coordinate key : util.getKeyLocations(map)) { // if found some keys already
				if (!keysOrdered.contains(key)) {
					keysOrdered.add(key); // add all the keys locations
				}
			}
			normal = true; // do normal strategy to grab the keys
		}
		
		if (normal) {
			// update the internal parameters in normalStrategy
			normalStrategy.update(getHealth(), keysOrdered, getKeys().size());
			// get path from normal strategy to retrieve keys
			path = (ArrayList<Coordinate>) normalStrategy.getPath(map, currentPosition);
		}

		if (!normal || path == null) {
			// get path from explore strategy
			path = (ArrayList<Coordinate>) exploreStrategy.getPath(map, currentPosition);
		}

		// implement healing strategy
		if (path != null) {
			healingStrategy.update(getHealth(), path);
			path = (ArrayList<Coordinate>) healingStrategy.getPath(map, currentPosition);
		}
		
		for (Coordinate location : util.getHealthLocations(map)) {
			if (currentPosition.equals(location) && getHealth() < 100.0) {
				path.clear();
				path.add(currentPosition);
				path.add(currentPosition); // applying a brake
				return path;
			}
		}
		
		return path;
	}

	private void execute(ArrayList<Coordinate> path) {
		// we only need the nextStep from the path for the car to move to
		Coordinate nextStep = null;
		if (path != null && path.size()>1) {
			currentPosition = new Coordinate(getPosition());	
			nextStep = path.get(1);// 0 is current location, 1 is the next step
			startMyCar();// start car engine after a brake
			
			// to count the number of stops the car has, debug purpose
			if (getSpeed()<1 && !nextStep.equals(currentPosition)) {
				badEngineCount++;
			}

			move(currentPosition, nextStep); // move the car from current location to the next location
			updateWorldMap(getView()); // update the map again after the move
		}
	}

	// update the map with view
	private void updateWorldMap(HashMap<Coordinate, MapTile> view) {
		map.putAll(view);
	}
	
	private void startMyCar() {
		// to determine if the car is stuck 
		if(initiate) { 
			MapTile front_mapTile = map.get(util.getNeighbourCoordinate(currentPosition,getOrientation()));
			MapTile back_mapTile = map.get(util.getNeighbourCoordinate(currentPosition,util.reverseOrientation(getOrientation())));
			String front_trapType = "";
			String back_trapType = "";
			
			// before speed up, check maptile ahead
			if (front_mapTile.isType(Type.TRAP)) {
				front_trapType = util.getTrapType(map,util.getNeighbourCoordinate(currentPosition,getOrientation()));
			}
			// before speed up, check maptile behind
			if (back_mapTile.isType(Type.TRAP)) {
				back_trapType = util.getTrapType(map,util.getNeighbourCoordinate(currentPosition,util.reverseOrientation(getOrientation())));
			}
			// checking surroundings
			if (front_mapTile.isType(Type.WALL) || front_trapType.equals("mud")) {
				applyReverseAcceleration();// apply backward speed
			}else if(back_mapTile.isType(Type.WALL) || back_trapType.equals("mud")){
				applyForwardAcceleration();// apply forward speed 
			}else {
				if(Math.random() < 0.5) { // give a chance to break tie if safe
					applyForwardAcceleration();// apply forward speed 
				}else {
					applyReverseAcceleration();// apply backward speed
				}
			} 
			initiate = false;// now the car is initialised
		}else {
			// the car is initialised, but stops for health
			if (getHealth() == 100 && map.get(currentPosition).isType(Type.TRAP)) {
				// start engine again if completed the job at that location
				initiate = true;	
			}
			// the car is initialised, but stops for keys
			if (getSpeed() == 0 && util.getTrapType(map, currentPosition).equals("lava")) {
				// start engine again if completed the job at that location
				initiate = true;	
			}
		}
	}
	
	// the move function, move he car from current to next
	private void move(Coordinate current, Coordinate next) {
		// if the car wants to stop
		if (current.equals(next)) {
			applyBrake(); // apply brakes, need to initial engine again later
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
