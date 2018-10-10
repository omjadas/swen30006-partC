package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.World;

public class healing extends CarController {
	
	private HashMap<Coordinate, MapTile> myMap;
	private ArrayList<Coordinate> healths;
	private Coordinate currentPosition;
	private Coordinate destination ;
	private Coordinate nextPosition;

	public healing(Car car) {
		super(car);
		this.currentPosition  = new Coordinate(getPosition());
		myMap = World.getMap();
	}

	@Override
	public void update() {
		
	}
	
	private Coordinate getHeaths() {
		Coordinate health = healths.get(0);
		return health;
		
		
	}
	
	

}
