package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import controller.CarController;
import mycontroller.util.util;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class FollowWallController extends CarController {
	
	public FollowWallController(Car car) {
		super(car);
		// TODO Auto-generated constructor stub
	}

	// How many minimum units the wall is away from the player.
	private int wallSensitivity = 2;
	
	private boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.
	
	// Car Speed to move at
	private final int CAR_MAX_SPEED = 1;

	// Coordinate initialGuess;
	// boolean notSouth = true;
	@Override
	public void update() {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = getView();
		
		// checkStateChange();
		if(getSpeed() < CAR_MAX_SPEED){       // Need speed to turn and progress toward the exit
			applyForwardAcceleration();   // Tough luck if there's a wall in the way
		}
//		if (isFollowingWall) {
//			// If wall no longer on left, turn left
//			if(!checkFollowingWall(getOrientation(), currentView)) {
//				turnLeft();
//			} else {
//				// If wall on left and wall straight ahead, turn right
//				if(checkWallAhead(getOrientation(), currentView)) {
//					turnRight();
//				}
//			}
//		} else {
//			// Start wall-following (with wall on left) as soon as we see a wall straight ahead
//			if(checkWallAhead(getOrientation(),currentView)) {
//				turnLeft();
//				isFollowingWall = true;
//			}
//		}
		ArrayList<WorldSpatial.Direction> directions = new ArrayList<WorldSpatial.Direction>();
		directions.add(WorldSpatial.Direction.EAST);
		directions.add(WorldSpatial.Direction.NORTH);
		directions.add(WorldSpatial.Direction.WEST);
		directions.add(WorldSpatial.Direction.SOUTH);
		ArrayList<Boolean> validRoads = checkRoads(currentView); // east,north,west,south
		int i = 0;
		for (boolean validRoad: validRoads) {
			if (validRoad) {
//				go(directions.get(i),getOrientation());
				break;
			}
			i += 1;
		}
	}
	
	public ArrayList<Boolean> checkRoads(HashMap<Coordinate, MapTile> currentView){
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(getPosition());
		HashMap<Coordinate, String> neighbours = util.getAllNeighbours(currentView, currentPosition);
		ArrayList<Boolean> areRoads = new ArrayList<Boolean>();
		
		for (Entry<Coordinate, String> entry : neighbours.entrySet()) {
			if (entry.getValue().equals("ROAD")) {
				areRoads.add(true);
			}
			else {
				areRoads.add(false);
			}
		}
		return areRoads;
	}
	
}
