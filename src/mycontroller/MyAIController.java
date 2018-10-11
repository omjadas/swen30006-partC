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
	
	private CarController pathing;
	private CarController exploring;
	private Coordinate currentPosition  = new Coordinate(getPosition());
	

	public MyAIController(Car car) {
		super(car);
		
	}

	@Override
	public void update() {
//		ArrayList<Coordinate> path = AStar.getPath(this.map, getPosition(), destination);
		
		
		ArrayList<Coordinate> path = (ArrayList<Coordinate>) ExploreStrategy.getPath(map,new Coordinate(getPosition()));
		
		if (path == null) {
            throw new IllegalArgumentException("No path to the given destination.");
        }
		move(currentPosition,path.get(path.size()-1));
		// TODO Auto-generated method stub
		HashMap<Coordinate, MapTile> currentView = getView();
		// System.out.println(grassLocation);
        updateWorldMap(currentView);
	}
	
	private void updateWorldMap(HashMap<Coordinate, MapTile> view) {
		MapTile mapTile;
		for (Coordinate coor: view.keySet()) {
			mapTile = view.get(coor);
			
			if (mapTile.isType(Type.TRAP)) {
				map.put(coor, mapTile);
				if (util.getTrapType(map,coor)=="health") {
					map.put(coor,mapTile);
				}
				
				if (util.getTrapType(map,coor)=="grass") {
					System.out.println(coor);
					map.put(coor,mapTile);
				}
				
				if (util.getTrapType(map,coor)=="mud") {
					map.put(coor,mapTile);
				}
				
				if (util.getTrapType(map,coor)=="lava") {
					map.put(coor,mapTile);
					int key = ((LavaTrap) mapTile).getKey();
					if (key>0) {
						keys.add(coor);
					}
				}
			}
		}
	}
	

	private void move(Coordinate current, Coordinate next) {
		// TODO Auto-generated method stub
		Direction nextMovement = getMoveAction(current,next);
		
//		System.out.println(getOrientation());
		go(nextMovement,getOrientation());
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
	

	public void go(Direction input, Direction orientation) {
//		applyBrake();
		if (input.equals(WorldSpatial.Direction.NORTH)) {
			switch(orientation){
			case EAST:
				turnLeft();
				break;
			case NORTH:
				if(getSpeed() < 1){       // Need speed to turn and progress toward the exit
					applyForwardAcceleration();   // Tough luck if there's a wall in the way
				}
				break;
			case SOUTH:
				applyReverseAcceleration();
				applyReverseAcceleration();
				break;
			case WEST:
				turnRight();
				break;
			}
		}else if (input.equals(WorldSpatial.Direction.EAST)) {
			switch(orientation){
			case EAST:
				if(getSpeed() < 1){       // Need speed to turn and progress toward the exit
					applyForwardAcceleration();   // Tough luck if there's a wall in the way
				}
				break;
			case NORTH:
				turnRight();
				break;
			case SOUTH:
				turnLeft();
				break;
			case WEST:
				applyReverseAcceleration();
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
				applyReverseAcceleration();
				break;
			case SOUTH:
				if(getSpeed() < 1){       // Need speed to turn and progress toward the exit
					applyForwardAcceleration();   // Tough luck if there's a wall in the way
				}
				break;
			case WEST:
				turnLeft();
				break;
			}
		}else {
			switch(orientation){
			case EAST:
				applyReverseAcceleration();
				applyReverseAcceleration();
				break;
			case NORTH:
				turnLeft();
				break;
			case SOUTH:
				turnRight();
				break;
			case WEST:
				if(getSpeed() < 1){       // Need speed to turn and progress toward the exit
					applyForwardAcceleration();   // Tough luck if there's a wall in the way
				}
				break;
			}
		}
	}
}
