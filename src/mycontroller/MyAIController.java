package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;
import mycontroller.util.util;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;

public class MyAIController extends CarController{
	
	HashMap<Coordinate, MapTile> map = super.getMap();
	ArrayList<Coordinate> keys = new ArrayList<Coordinate>();
	
	private CarController pathing;
	private CarController exploring;
	

	public MyAIController(Car car) {
		super(car);
		this.pathing = new AStarController(car);
		this.exploring = new FollowWallController(car);
	}

	@Override
	public void update() {
		pathing.update();
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
}
