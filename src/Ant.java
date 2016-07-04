import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Ant agent that interacts with an ant colony simulation evironment.
 */
public class Ant {

	Set<Cell> foodFound = new HashSet<Cell>();

	public static double dropoffRate = .9;
	public static double bestCellNext = 0.5;
	public static boolean allFoodRequired = false;

	private int x;
	private int y;

	private boolean returnToNest;

	Cell[][] world;

	double maxPheromone = 10.0;

	int steps = 0;

	private Ants ants;

	public Ant(Cell startCell, Cell[][] world, Ants ants){
		this.x = startCell.c;
		this.y = startCell.r;
		this.world = world;
		this.ants = ants;
	}


	public void die(){
		returnToNest = false;
		steps = 0;
		foodFound.clear();
		Set<Cell> nests = ants.getNests();
		if(!nests.isEmpty()){
			int nestIndex = (int) (nests.size() * Math.random());
			Cell nest = (Cell) nests.toArray()[nestIndex];
			x = nest.c;
			y = nest.r;
		}
	}

	public void step(){

		double chanceToTakeBest = Math.random();
		
		steps++;
		
		foodFound.retainAll(ants.getFood());

		if(returnToNest){
			if(world[x][y].hasNest()){
				die();
			}
			else{

				double maxNestSoFar = 0;
				Map<Cell, Double> maxFoodSoFarMap = new HashMap<Cell, Double>();
				List<Cell> maxNestCells = new ArrayList<Cell>();
				List<Cell> allNeighborCells = new ArrayList<Cell>();
				double totalNeighborPheromones = 0;
				for(int c = -1; c <=1; c++){

					if(x+c < 0 || x+c >= world.length){
						continue;
					}

					for(int r = -1; r <= 1; r++){
						if(c == 0 && r == 0){
							continue;
						}
						else if(y+r < 0 || y+r >= world[0].length){
							continue;
						}

						if(!world[x+c][y+r].isBlocked()){

							allNeighborCells.add(world[x+c][y+r]);
							totalNeighborPheromones += world[x+c][y+r].nestPheromoneLevel;

							if(world[x+c][y+r].getNestPheromoneLevel() > maxNestSoFar){
								maxNestSoFar = world[x+c][y+r].getNestPheromoneLevel();

								maxNestCells.clear();
								maxNestCells.add(world[x+c][y+r]);
							}
							else if(world[x+c][y+r].getNestPheromoneLevel() == maxNestSoFar){
								maxNestCells.add(world[x+c][y+r]);
							}
							
							for(Cell food : foodFound){

								if(!maxFoodSoFarMap.containsKey(food) || world[x+c][y+r].getFoodPheromoneLevel(food) > maxFoodSoFarMap.get(food)){
									maxFoodSoFarMap.put(food, world[x+c][y+r].getFoodPheromoneLevel(food));
								}
							}	
						}
					}
				}

				if(world[x][y].isGoal()){
					maxFoodSoFarMap.put(world[x][y], Cell.maxFoodPheromoneLevel);
				}

				for(Cell food : foodFound){
					world[x][y].setFoodPheromone(food, maxFoodSoFarMap.get(food) * Ant.dropoffRate);
				}

				if(Ant.bestCellNext > chanceToTakeBest){
					if(!maxNestCells.isEmpty()){
						int cellIndex = (int) (maxNestCells.size()*Math.random());
						Cell bestNestCellSoFar = maxNestCells.get(cellIndex);

						x = bestNestCellSoFar.c;
						y = bestNestCellSoFar.r;
					}
				}
				else{ //give cells chance based on pheremone
					double pheremonesSoFar = 0;
					double goalPheromoneLevel = totalNeighborPheromones * Math.random();
					for(Cell neighbor : allNeighborCells){
						pheremonesSoFar+=neighbor.getNestPheromoneLevel();
						if(pheremonesSoFar > goalPheromoneLevel){
							x = neighbor.c;
							y = neighbor.r;
							break;
						}
					}
				}
			}
		}
		else{ //look for food

			if(world[x][y].isGoal()){
				foodFound.add(world[x][y]);
				if(Ant.allFoodRequired){
					if(foodFound.size() >= ants.getFood().size()){
						steps = 0;
						returnToNest = true;
						return;
					}
				}
				else{
					steps = 0;
					returnToNest = true;
					return;
				}
			}
			else if(world[x][y].hasNest()){
				if(steps > 1){
					die();
					return;
				}
			}

			double maxFoodSoFar = 0;
			double maxNestSoFar = 0;
			List<Cell> maxFoodCells = new ArrayList<Cell>();
			List<Cell> allNeighborCells = new ArrayList<Cell>();
			double totalNeighborPheromones = 0;
			Map<Cell, Double> maxFoodSoFarMap = new HashMap<Cell, Double>();
			for(int c = -1; c <=1; c++){

				if(x+c < 0 || x+c >= world.length){
					continue;
				}

				for(int r = -1; r <= 1; r++){
					//don't count yourself
					if(c == 0 && r == 0){
						continue;
					}
					else if(y+r < 0 || y+r >= world[0].length){
						continue;
					}

					if(!world[x+c][y+r].isBlocked()){

						allNeighborCells.add(world[x+c][y+r]);

						if(maxFoodSoFar == 0){
							maxFoodCells.add(world[x+c][y+r]);
						}

						for(Cell food : foodFound){


							if(!maxFoodSoFarMap.containsKey(food) || world[x+c][y+r].getFoodPheromoneLevel(food) > maxFoodSoFarMap.get(food)){
								maxFoodSoFarMap.put(food, world[x+c][y+r].getFoodPheromoneLevel(food));
							}

						}

						if(world[x][y].isGoal()){
							maxFoodSoFarMap.put(world[x][y], Cell.maxFoodPheromoneLevel);
						}

						for(Cell food : foodFound){
							world[x][y].setFoodPheromone(food, maxFoodSoFarMap.get(food) * Ant.dropoffRate);
						}

						if(world[x+c][y+r].getNestPheromoneLevel() > maxNestSoFar){
							maxNestSoFar = world[x+c][y+r].getNestPheromoneLevel();
						}

						if(ants.getFood().isEmpty()){
							totalNeighborPheromones += 1;
						}
						else{
							for(Cell food : ants.getFood()){

								if(foodFound.contains(food)){
									continue;
								}
								totalNeighborPheromones += world[x+c][y+r].getFoodPheromoneLevel(food);

								if(world[x+c][y+r].getFoodPheromoneLevel(food) > maxFoodSoFar){
									maxFoodSoFar = world[x+c][y+r].getFoodPheromoneLevel(food);
									maxFoodCells.clear();
									maxFoodCells.add(world[x+c][y+r]);
								}
								else if(world[x+c][y+r].getFoodPheromoneLevel(food) == maxFoodSoFar){
									maxFoodCells.add(world[x+c][y+r]);
								}
							}	
						}
					}
				}
			}

			if(world[x][y].hasNest()){
				maxNestSoFar = Cell.maxNestPheromoneLevel;
			}

			world[x][y].setNestPheromone(maxNestSoFar * Ant.dropoffRate);

			if(Ant.bestCellNext > chanceToTakeBest){
				if(!maxFoodCells.isEmpty()){
					int cellIndex = (int) (maxFoodCells.size()*Math.random());
					Cell bestCellSoFar = maxFoodCells.get(cellIndex);

					x = bestCellSoFar.c;
					y = bestCellSoFar.r;
				}
			}	
			else{ //give cells chance based on pheremone
				double pheremonesSoFar = 0;
				double goalPheromoneLevel = totalNeighborPheromones * Math.random();

				for(Cell neighbor : allNeighborCells){
					if(ants.getFood().isEmpty()){
						pheremonesSoFar += 1;
						if(pheremonesSoFar > goalPheromoneLevel){

							x = neighbor.c;
							y = neighbor.r;
							break;
						}
					}
					else{

						for(Cell food : ants.getFood()){
							if(foodFound.contains(food)){
								continue;
							}
							pheremonesSoFar+=neighbor.getFoodPheromoneLevel(food);
							if(pheremonesSoFar > goalPheromoneLevel){

								x = neighbor.c;
								y = neighbor.r;
								return;
							}
						}
					}
				}
			}
		}
	}

	public int getCol() {
		return x;
	}
	
	public int getRow(){
		return y;
	}

	public boolean isReturningHome() {
		return returnToNest;
	}
}