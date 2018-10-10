package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;

public class healing extends CarController {
	
	private HashMap<Coordinate, MapTile> myMap;
	private ArrayList<Coordinate> healths;
	private Coordinate currentPosition;
	private Coordinate destination ;
	private Coordinate nextPosition;

	public healing(Car car) {
		super(car);
		// TODO Auto-generated constructor stub
		
		this.currentPosition  = new Coordinate(getPosition());
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	private Coordinate getHeaths() {
		
		Coordinate health = healths.get(0);
		return health;
		
		
	}
	
	

}
