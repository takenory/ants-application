import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ant agent that interacts with an ant colony simulation evironment.
 */
public class Ant {
	
	/**アリ個人が見つけた餌情報*/
	Set<Cell> foodFound = new HashSet<Cell>();
	
	/**フェロモンの減衰率*/
	public static double dropoffRate = .9;
	
	/**既存ルートを進む確率**/
	public static double bestCellNext = .5;
	
	/**かみ砕いて持ち帰るることが可能かどうか*/
	public static boolean allFoodRequired = false;
	
	/**現在地*/
	private int x;
	
	/**現在地*/
	private int y;
	
	/**帰巣中判定フラグ*/
	private boolean returnToNest;
	
	/**盤面全体*/
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
			
			//帰巣ロジック
			if(world[x][y].hasNest()){
				//帰巣すると死ぬ
				die();
			}
			
			else{
				
				//アリの帰巣行動
				double maxNestSoFar = 0;
				Map<Cell, Double> maxFoodSoFarMap = new HashMap<Cell, Double>();
				
				/**暫定進路一覧*/
				List<Cell> maxNestCells = new ArrayList<Cell>();
				
				/**移動可能なセル一覧*/
				List<Cell> allNeighborCells = new ArrayList<Cell>();
				
				/**帰巣フェロモンの総量*/
				double totalNeighborPheromones = 0;
				
				//周囲のセルを探索
				for(int c = -1; c <=1; c++){
					
					if(x+c < 0 || x+c >= world.length){
						//検索先が盤面からはみ出したら無視
						continue;
					}
					
					for(int r = -1; r <= 1; r++){
						if(c == 0 && r == 0){
							//現在位置は無視
							continue;
						}
						else if(y+r < 0 || y+r >= world[0].length){
							//検索先が盤面からはみ出したら無視
							continue;
						}
						
						if(!world[x+c][y+r].isBlocked()){
							
							//障害物以外の時
							allNeighborCells.add(world[x+c][y+r]);
							totalNeighborPheromones += world[x+c][y+r].nestPheromoneLevel;
							
							if(world[x+c][y+r].getNestPheromoneLevel() > maxNestSoFar){
								//より大きい帰巣フェロモンが見つかったとき
								maxNestSoFar = world[x+c][y+r].getNestPheromoneLevel();
								
								maxNestCells.clear();
								maxNestCells.add(world[x+c][y+r]);
							}
							
							else if(world[x+c][y+r].getNestPheromoneLevel() == maxNestSoFar){
								maxNestCells.add(world[x+c][y+r]);
							}
							
							
							for(Cell food : foodFound){
								//餌場の発見時、より強いフェロモンの存在するルートに設定
								if(!maxFoodSoFarMap.containsKey(food) || world[x+c][y+r].getFoodPheromoneLevel(food) > maxFoodSoFarMap.get(food)){
									//餌場フェロモンを更新
									maxFoodSoFarMap.put(food, world[x+c][y+r].getFoodPheromoneLevel(food));
								}
							}
						}
					}
				}
				
				if(world[x][y].isGoal()){
					//現在地が餌場ならフェロモンを設定
					maxFoodSoFarMap.put(world[x][y], Cell.maxFoodPheromoneLevel);
				}
				
				for(Cell food : foodFound){
					//アリのフェロモン値を設定
					world[x][y].setFoodPheromone(food, maxFoodSoFarMap.get(food) * Ant.dropoffRate);
				}
				
				if(Ant.bestCellNext > chanceToTakeBest){
					//既知のルートを辿る
					if(!maxNestCells.isEmpty()){
						int cellIndex = (int) (maxNestCells.size()*Math.random());
						Cell bestNestCellSoFar = maxNestCells.get(cellIndex);
						
						x = bestNestCellSoFar.c;
						y = bestNestCellSoFar.r;
					}
				}
				
				else{ //give cells chance based on pheremone
					//新規ルートを探す
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
			
			//餌場探索ロジック
			if(world[x][y].isGoal()){
				//餌場の発見時
				foodFound.add(world[x][y]);
				if(Ant.allFoodRequired){
					if(foodFound.size() >= ants.getFood().size()){
						//Allの場合
						steps = 0;
						returnToNest = true;
						return;
					}
				}
				else{
					//Oneの場合
					steps = 0;
					returnToNest = true;
					return;
				}
			}
			
			else if(world[x][y].hasNest()){
				if(steps > 1){
					//帰巣した場合
					die();
					return;
				}
			}
			
			//空白地帯の挙動
			double maxFoodSoFar = 0;
			double maxNestSoFar = 0;
			List<Cell> maxFoodCells = new ArrayList<Cell>();
			List<Cell> allNeighborCells = new ArrayList<Cell>();
			double totalNeighborPheromones = 0;
			Map<Cell, Double> maxFoodSoFarMap = new HashMap<Cell, Double>();
			
			for(int c = -1; c <=1; c++){
				//餌場を探索
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
							//
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