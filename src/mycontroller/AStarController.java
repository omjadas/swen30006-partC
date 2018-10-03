package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class AStarController extends CarController {
	
	private HashMap<Coordinate, MapTile> myMap;
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
		
		if(getSpeed() < 1){       // Need speed to turn and progress toward the exit
			applyForwardAcceleration();   // Tough luck if there's a wall in the way
		}
		
		currentPosition  = new Coordinate(getPosition());
		HashMap<Coordinate, MapTile> currentView = getView();
		for (Coordinate dest: currentView.keySet()) {
			destination = dest;
		}
		
		nextPosition = getPath().get(0);
		
		move(currentPosition,nextPosition);
		
		
	}

	private void move(Coordinate current, Coordinate next) {
		// TODO Auto-generated method stub
		turnLeft();
		turnRight();
	}

	private ArrayList<Coordinate> getPath() {
		// TODO Auto-generated method stub
		ArrayList<Coordinate> path = AStar.getPath(this.myMap, currentPosition, destination);
		if (path == null) {
            throw new IllegalArgumentException("No path to the given destination.");
        }
		return path;
		
	}
}
