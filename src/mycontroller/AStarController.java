package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class AStarController extends CarController {
	
	private HashMap<Coordinate, MapTile> myMap = super.getMap();
	private Coordinate currentPosition;
	private Coordinate destination ;
	private Coordinate nextPosition;
	
	
	public AStarController(Car car) {
		super(car);
	}
	
	// Coordinate initialGuess;
	// boolean notSouth = true;
	@Override
	public void update() {
		
		
		currentPosition  = new Coordinate(getPosition());
		HashMap<Coordinate, MapTile> currentView = getView();
		for (Coordinate dest: currentView.keySet()) {
			destination = dest;
		}
		destination = new Coordinate(10,2);
		ArrayList<Coordinate> pathToDest = getPath();
		nextPosition = pathToDest.get(pathToDest.size()-2);
		System.out.print(currentPosition);
		System.out.println(nextPosition);
		System.out.println(pathToDest);
		go(Direction.EAST,Direction.EAST);
//		move(currentPosition,nextPosition);
	}

	private void move(Coordinate current, Coordinate next) {
		// TODO Auto-generated method stub
		Direction nextMovement = getMoveAction(current,next);
		
//		System.out.println(getOrientation());
		go(Direction.EAST,Direction.EAST);
	}

	private Direction getMoveAction(Coordinate current, Coordinate next) {
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

	private ArrayList<Coordinate> getPath() {
		// TODO Auto-generated method stub
		
		ArrayList<Coordinate> path = AStar.getPath(this.myMap, currentPosition, destination);
		if (path == null) {
            throw new IllegalArgumentException("No path to the given destination.");
        }
		return path;
		
	}
	

	public void go(Direction input, Direction orientation) {
		if(getSpeed() < 1){       // Need speed to turn and progress toward the exit
		applyForwardAcceleration();   // Tough luck if there's a wall in the way
		}
//		applyBrake();
		if (input.equals(WorldSpatial.Direction.NORTH)) {
			switch(orientation){
			case EAST:
				turnLeft();
			case NORTH:
				
			case SOUTH:
				turnLeft();
				turnLeft();
			case WEST:
				turnRight();
			}
		}else if (input.equals(WorldSpatial.Direction.EAST)) {
			switch(orientation){
			case EAST:
				
			case NORTH:
				turnRight();
			case SOUTH:
				turnLeft();
			case WEST:
				turnLeft();
				turnLeft();
			}
		}else if (input.equals(WorldSpatial.Direction.SOUTH)) {
			switch(orientation){
			case EAST:
				turnRight();
			case NORTH:
				turnRight();
				turnRight();
			case SOUTH:
				
			case WEST:
				turnLeft();
			}
		}else {
			switch(orientation){
			case EAST:
				turnRight();
				turnRight();
			case NORTH:
				turnLeft();
			case SOUTH:
				turnRight();
			case WEST:
			}
		}
	}
}
