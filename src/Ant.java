import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Random;
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
	
	Set<Cell> foods;
	int foodIndex;
	Cell Targetfood;
	
	/**アリ個人が見つけた餌情報*/
	Set<Cell> foodFound = new HashSet<Cell>();
	
	/**フェロモンの減衰率*/
	public static double dropoffRate = .9;
	
	/**既存ルートを進む確率**/
	public static double bestCellNext = .5;
	
	/**かみ砕いて持ち帰るることが可能かどうか*/
	public static boolean allFoodRequired = false;
	
	///////追加
	public static boolean RestrictBridge = false; //使える橋の制限があるかどうか
	public static int numBridge = 1; //橋の数
	
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
	
	//追加
	private int num;
	private boolean is_stop;
	private int stop_count; //0708miyamoto
	
//kimura_s
    //橋北のアリか？
    private boolean is_north;
	//中心を通過するか？
	private boolean path_center;
//kimura_e
	

	//ひとつ前の座標	
	private int bX, bY; //0710miura
	
	public Ant(Cell startCell, Cell[][] world, Ants ants, int num){
		this.x = startCell.c;
		this.y = startCell.r;
		this.world = world;
		this.ants = ants;
		this.num = num;
		
//kimura_s
        this.is_north = false;
        if (this.y < this.ants.getRows() / 2) {
            this.is_north = true;
        }
//kimura_e


	}
	
//0712 takaki changed_s
////miyamoto_s
//	public int stop_count(boolean stop){
//		if(!stop) return this.stop_count = 0;
//		else return this.stop_count = this.stop_count + 1;
//	}
//
//	public int stop_count0(){
//		return this.stop_count;
//	}
////miyamoto_e

  public int stop_count(){
    return this.stop_count;
  }
//0712 takaki changed_e
 
	public boolean isStop() {
		return is_stop;
	}
	
//kimura_s
	public int number() {
		return this.num;
	}
    public boolean isNorth() {
        return this.is_north;
    }
    public boolean pathCenter() {
        return this.path_center;
    }
//kimura_e
	
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
		
//kimura_s
        this.is_north = false;
        if (this.y < this.ants.getRows() / 2) {
            this.is_north = true;
        }
//kimura_e
        
	}
	
	public void step(){
		double chanceToTakeBest = Math.random();
		
		steps++;
		
		foodFound.retainAll(ants.getFood());

//miyamoto_s
		//車に目的地を割り振る
		foods = ants.getFood();
		foodIndex = num;
		Targetfood = (Cell)foods.toArray()[foodIndex];
//miyamoto_e
		
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
					//die();
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
				
				else{ //give cells chance based on pheromone
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
			
//0712 takaki added_s
      bX = x; //0711
      bY = y; //0711
//0712 takaki added_e
			
			//餌場探索ロジック
			if(world[x][y].isGoal()){ //餌場の発見時

//0712 takaki changed_s
//				bX = x; //0711
//				bY = y; //0711
//0712 takaki changed_e

//0713miyamoto_s
				//自分の目的地でない場合は無視
				if((x == Targetfood.c) && (y == Targetfood.r)){
					ants.count_AntisGoal(true);
//0713miyamoto_e		

					ants.isnotAnt(x,y);
//0710miura_s
					ants.isnotAnt(bX, bY);
//0710miura_e
					
					die();
								
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
//0713miyamoto_s
				}
//0713miyamoto_e
			}
			
			/*
			//0713miyamoto 途中で巣を踏んでも死なない
			else if(world[x][y].hasNest()){
				if(steps > 1){
					//帰巣した場合
					die();
					return;
				}
			}
			*/
						
			//空白地帯の挙動		
			int col = ants.getColumns();
			int row = ants.getRows();		
			
			double maxFoodSoFar = 0;
			double maxNestSoFar = 0;
			List<Cell> maxFoodCells = new ArrayList<Cell>();
			List<Cell> allNeighborCells = new ArrayList<Cell>();
			double totalNeighborPheromones = 0;
			Map<Cell, Double> maxFoodSoFarMap = new HashMap<Cell, Double>();

//miyamoto_s
			/** 車に橋の座標を与える　ここから**/
			int[] Bridges;		
			Bridges = new int[numBridge];
			//橋のx座標
			for(int i=0; i<numBridge; i++){
				Bridges[i] = (i+1)*(col/(numBridge+1));
			}
			int TargetBridge;
			int nearestBridge = 0;
			//橋に進行方向の制限を与えない
			if(!Ant.RestrictBridge){
				//一番近い橋へ
					int tmp = 100;
					for(int i = 0; i < numBridge; i++){
						if(Math.abs(x - Bridges[i]) < tmp){
							tmp = Math.abs(x - Bridges[i]);
							nearestBridge = i;
						}
					}		
				TargetBridge = nearestBridge;
				
			//橋に進行方向の制限を与える
			}else{
				//橋一本の場合
				if(numBridge == 1){
					TargetBridge = 0;
				//橋二本
				}else if(numBridge == 2){
					if(y < (int)row/2){
						TargetBridge = 0;
					}else{
						TargetBridge = 1;
					}
				//橋三本
				}else{
					int tmp = 100;
					if(y < (int)row/2){
						if (Math.abs(x - Bridges[0]) < Math.abs(x - Bridges[2])){
							TargetBridge = 0;
						}else{
							TargetBridge = 2;
						}
					}else{
						if (Math.abs(x - Bridges[1]) < Math.abs(x - Bridges[3])){
							TargetBridge = 1;
						}else{
							TargetBridge = 3;
						}
					}
				}
			}
			/** 車に橋の座標を与える　ここまで **/
//miyamoto_e
			
//miyamoto_s
			// マップの中心の座標を得る
			// midcol : x座標の中心
			// midrow : y座標の中心
			int midcol = (int)col/2;
			int midrow = (int)row/2;
//miyamoto_e
			
			int x0, x1, y0 = 0;

//kimura_s
			//中心に行く必要の有無を確認
			int TargetTmp = 0;
			int TargetBridge_left = 0;
			if ((Targetfood.r < midrow) && (midrow + 3  < y)) {
				this.path_center = true;
				
				//左側通行のための座標を得る(0709宮本)
				TargetBridge_left = Bridges[TargetBridge]- 1; 
				TargetTmp = Bridges[TargetBridge] + 1;//+1消去
			}
			else if ((Targetfood.r > midrow) && (midrow-+ 3 > y)) {
				this.path_center = true;
				
				//左側通行のための座標を得る(0709宮本)
				TargetBridge_left = Bridges[TargetBridge] + 1;// + 1; 
				TargetTmp = Bridges[TargetBridge] - 1;
			}
			else {
				this.path_center = false;
			}
//kimura_s
			
//miyamoto_s
			//現在地がターゲットの橋より左である
			if(TargetBridge_left > x) x0 = 1;
			//現在地がターゲットの橋より右である
			else if(TargetBridge_left < x) x0 = -1;
			//現在地はターゲットの橋の真下である
			else x0 = 0;
			
			// 現在地が目的地より左である
			if(Targetfood.c > x) x1 = 1;
			// 現在地が目的地より右である
			else if(Targetfood.c < x) x1 = -1;
			// 現在地のx座標は目的地と同じである
			else x1 = 0;
//miyamoto_e
			
//kimura_s
			if (Targetfood.r > y) y0 = 1;
			else if(Targetfood.r < y) y0 = -1;
			else y0 = 0;
//kimura_e

			is_stop = false;
//kimura_s
			//if(y > midrow){
			if (this.path_center) {
			//橋を越える前
//kimura_e		
				
				if(!world[x + x0][y + y0].hasAnt()){
					
//0710miura_s
					ants.isnotAnt(bX, bY);
					bX = x;
					bY = y;
//0710_miura_e
					
//0709miyamoto_s
					//ターゲットではない橋に入らないようにする
					if(Math.abs(y - midrow) == 4){	
						if((x + x0) != TargetBridge_left){
						//if(!(Math.abs((x + x0) - TargetBridge_left) < 3 
						//		&& Math.abs((x + x0) - TargetTmp) < 3)){
							x = x + x0;
						}else{
							y = y + y0;
							x = x + x0;
						}
					}
//0709miyamoto_e

					else if(!world[x + x0][y + y0].isBlocked()){
						x = x + x0;
						y = y + y0;
					}
					else {
						if(!world[x][y + y0].isBlocked()){
							y = y + y0;
						}else{
							x = x + x0;
						}
					}
					ants.isAnt(x,  y);
				}
				else {
					is_stop = true;
					stopOrGotoOtherCell(TargetBridge_left, midrow);
				}
			}		
			
//0709miyamoto_s
			//橋の上
			else if(Math.abs(y - midrow) <= 3){	
//0710miura_s
				if(!world[x][y + y0].hasAnt()){
					ants.isnotAnt(bX, bY);
					bX = x;
					bY = y;
					y = y + y0;
					ants.isAnt(x,  y);
				
				}else{
					is_stop = true;
					stopOrGotoOtherCell(Targetfood.c, Targetfood.r);//
				}
//0710miura_e
			}
//0709miyamoto_e
			//橋を越えてから
			else {
				//進んだ先が障害物でない場合
				if(!world[x + x1][y + y0].hasAnt()){
					
//0710miura_s
					ants.isnotAnt(bX, bY);
					bX = x;
					bY = y;
//0710_miura_e
					
					if(!world[x + x1][y + y0].isBlocked()){					
						//x座標を中心に近づける
						if((x - Targetfood.c) != 0) {
							x = x + x1;
							y = y + y0;
						}
						else {
							x = x + x1;
							y = y + y0;
						}	
					}
					else {
						x = x - x1;
						y = y + y0;
					}
//0710miura_s
					ants.isAnt(x,  y);
//0710miura_e
				}
				else {
					is_stop = true;
					stopOrGotoOtherCell(Targetfood.c, Targetfood.r);//
				}
			}
//0712 takaki changed_s
      //if(is_stop==true){
        //stopOrGotoOtherCell(Targetfood);
      //}
//0712 takaki changed_e

			/**  ここからコメントアウト **/
			/*
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
				
					//移動候補セルがマップの端でも障害物でもなかったら
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
			*/
			/** ここまでコメントアウト **/
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

//0712 takaki added_s
	//private void stopOrGotoOtherCell(Cell TargetFood) { //0713miyamoto changed
	private void stopOrGotoOtherCell(int Target_c, int Target_r){
		// 停止回数が0の場合
	    if (this.stop_count == 0){
	    	this.is_stop = true;
	    	this.stop_count = 1;	
	    }
	    // 停止回数が0でない場合
	    else{
	    	int other_x = this.x;
	    	int other_y = this.y;
	    	// 移動可能なセルの一覧を作成する
	    	List<Cell> allNeighborCells = new ArrayList<Cell>();
	
	    	// x方向の移動範囲 | x-1 | x+0 | x+1 |
	    	for(int c = -1; c <=1; c++){
	    		//検索先が盤面からはみ出したら無視
	    		if(this.x+c < 0 || this.x+c >= world.length){
	    			continue;
	    		}	

	    		//                 | y+1 |
	    		// y方向の移動範囲 | y+0 |
	    		//                 | y-1 |
	    		for(int r = -1; r <= 1; r++){
	    			//現在位置は無視
	    			if(c == 0 && r == 0){
	    				continue;
	    			}
	    			//検索先が盤面からはみ出したら無視
	    			else if(this.y+r < 0 || this.y+r >= world[0].length){
	    				continue;
	    			}

	    			//移動先候補セルがマップの端でも障害物でなかった場合
	    			//if(!world[this.x+c][this.y+r].isBlocked()){
	    			//移動先候補セルがマップの端でも障害物でもなく && アリのいるセルでもなかった場合
	    			//if(!world[this.x+c][this.y+r].isBlocked() && !world[this.x+c][this.y+r].hasAnt()){	
	    			//移動先候補セルがマップの端でも障害物でもなく && アリの巣でもなかった場合
	    			if(!world[this.x+c][this.y+r].isBlocked() && !world[this.x+c][this.y+r].hasNest()){
	    				//移動先候補セルがマップの端でも障害物でもなく && アリのいるセルでもなく && アリの巣でもなかった場合
	    				//if(!world[this.x+c][this.y+r].isBlocked() && !world[this.x+c][this.y+r].hasAnt() && !world[this.x+c][this.y+r].hasNest()){
	    				//移動先候補セルがマップの端でも障害物でなかった場合
	    				allNeighborCells.add(world[this.x+c][this.y+r]);
	    			}	
	    		}
	    	}

	    	// 移動可能なセルの一覧から目的地へ近づくセルと迂回する(遠ざかる)セルを求める
	    	Map<Cell, Double> approachingCellsMap = new HashMap<Cell, Double>();
	    	Map<Cell, Double> detouringCellsMap = new HashMap<Cell, Double>();
	    	for(Cell neighbor : allNeighborCells){
	    		// 現在の場所から目的地への移動距離と移動可能なセルから目的地への移動距離を取得する
	    		double current_distance = Math.sqrt((Target_c - this.x)^2 + (Target_r - this.y)^2);	
	    		double neighbor_distance = Math.sqrt((Target_c - neighbor.c)^2 + (Target_r - neighbor.r)^2);
	    		// 目的地への距離が近くなるセルの場合
	    		if(current_distance > neighbor_distance){
	    			// 近づくセルのマップに追加する
	    			approachingCellsMap.put(neighbor, neighbor_distance);
	    		}
	    		else{
	    			// 迂回するセルのマップに追加する
	    			detouringCellsMap.put(neighbor, neighbor_distance);
	    		}
	    	}

	    	// 近づくセルがある場合
	    	if(approachingCellsMap.size() >0){
	    		// 近づいていくセルのリストから移動距離が短いセルに移動する
	    		List<Map.Entry> mapValuesList = new ArrayList<Map.Entry>(approachingCellsMap.entrySet());
	    		Collections.sort(mapValuesList, new Comparator<Map.Entry>() {
	    			@Override
	    			public int compare(Entry entry1, Entry entry2) {
	    				// 移動距離の昇順でソートする
	    				return ((Comparable) entry1.getValue()).compareTo((Comparable) entry2.getValue());
	    			}	
	    		});
	    		// マッップから移動先のセルを順に取り出す
	    		for (Entry entry : mapValuesList) {
	    			Cell next_cell = (Cell) entry.getKey();
	    			// セルにアリがいない場合
	    			if(!world[next_cell.c][next_cell.r].hasAnt()){
	    				// 移動する
	    				this.ants.isnotAnt(this.x, this.y);
	    				this.x = next_cell.c;
	    				this.y = next_cell.r;
	    				this.ants.isAnt(this.x,  this.y);
	    				// 停止状態・停止回数をクリアする
	    				this.is_stop = false;
	    				return;
	    			}
	    		}
	    	}
	    	else{
	    		// 迂回する(遠ざかる)セルがある場合
	    		if(detouringCellsMap.size() > 0){
	    			// 離れていくセルのリストから移動距離が一番短いセルを移動先として取り出す
	    			List<Map.Entry> mapValuesList = new ArrayList<Map.Entry>(detouringCellsMap.entrySet());
	    			Collections.sort(mapValuesList, new Comparator<Map.Entry>() {
	    				@Override
	    				public int compare(Entry entry1, Entry entry2) {
	    					// 移動距離の昇順でソートする
	    					return ((Comparable) entry1.getValue()).compareTo((Comparable) entry2.getValue());	
	    				}
	    			});
	    			// 迂回するか判断するための停止回数リミットごとに、迂回する(遠ざかる)セルの候補からセルを取り出して処理する
	    			for(int i = 0; i < this.ants.stopCountOnDetourList.size(); i++){
	    				if(detouringCellsMap.size() > i){
	    					// 停止回数がリミットを超える場合
	    					if(this.stop_count > this.ants.stopCountOnDetourList.get(i)){
	    						// マッップからi番目にある移動先のセルを取り出す
	    						Entry entry = mapValuesList.get(i);
	    						Cell next_cell = (Cell) entry.getKey();
	    						// セルにアリがいない場合
	    						if(!world[next_cell.c][next_cell.r].hasAnt()){
	    							// 移動する
	    							this.ants.isnotAnt(this.x, this.y);
	 	
	    							this.y = next_cell.r;
	    							this.ants.isAnt(this.x,  this.y);	
	    							// 停止状態・停止回数をクリアする
	    							this.is_stop = false;
	    							this.stop_count = 0;	
	    							return;
	    						}
	    					}
	    				}
	    			}
	    		}
	    	}
	    	// 停止状態、停止回数をカウントアップする
	    	this.is_stop = true;
	    	this.stop_count = this.stop_count + 1;
	    }
	}
	//0712 takaki added_e
}
