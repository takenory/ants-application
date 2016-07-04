import java.util.HashMap;
import java.util.Map;


public class Cell {

	public static double maxFoodPheromoneLevel = 100.0;
	public static double maxNestPheromoneLevel = 100.0;
	public static double evaporationRate = .9;
	private boolean hasObstacle;
	private boolean hasNest;
	public Map<Cell, Double> foodPheromoneLevelMap = new HashMap<Cell, Double>();
	public double nestPheromoneLevel = 1.0;
	private boolean isGoal = false;
	
	int c;
	int r;
	
	public Cell(int c, int r){
		this.c = c;
		this.r = r;
	}
	
	public void setIsGoal(boolean isGoal){
		this.isGoal = isGoal;
	}
	
	public void step(){
		
		for(Cell food : foodPheromoneLevelMap.keySet()){
			double foodPheromoneLevel = foodPheromoneLevelMap.get(food);
			foodPheromoneLevel *= Cell.evaporationRate;
			if(foodPheromoneLevel < 1){
				foodPheromoneLevel = 1;
			}
			if(foodPheromoneLevel > Cell.maxFoodPheromoneLevel){
				foodPheromoneLevel = Cell.maxFoodPheromoneLevel;
			}
			foodPheromoneLevelMap.put(food, foodPheromoneLevel);
		}
		
		nestPheromoneLevel *= Cell.evaporationRate;
		if(nestPheromoneLevel < 1){
			nestPheromoneLevel = 1;
		}
		
		if(nestPheromoneLevel > Cell.maxNestPheromoneLevel){
			nestPheromoneLevel = Cell.maxNestPheromoneLevel;
		}
	}

	public void setFoodPheromone(Cell food, double pheromone){
		if(pheromone > Cell.maxFoodPheromoneLevel){
			pheromone = Cell.maxFoodPheromoneLevel;
		}
		foodPheromoneLevelMap.put(food, pheromone);
	}
	
	public void setNestPheromone(double pheromone){
		
		nestPheromoneLevel = pheromone;
		if(nestPheromoneLevel > Cell.maxNestPheromoneLevel){
			nestPheromoneLevel = Cell.maxNestPheromoneLevel;
		}
	}
	
	public double getFoodPheromoneLevel(Cell food){
		if(!foodPheromoneLevelMap.containsKey(food)){
			return 1;
		}
		return foodPheromoneLevelMap.get(food);
	}
	
	public double getNestPheromoneLevel(){
		return nestPheromoneLevel;
	}
	
	public boolean isBlocked(){
		return hasObstacle;
	}

	public boolean isGoal() {
		return isGoal;
	}

	public void setIsObstacle(boolean hasObstacle) {
		this.hasObstacle = hasObstacle;
	}

	public boolean hasNest() {
		return hasNest;
	}

	public void setHasNest(boolean hasNest) {
		this.hasNest = hasNest;
	}	
}