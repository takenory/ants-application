import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * Class for displaying and updating an ant colony simulation.
 * Another class should be used to control the simulation
 * by calling the methods of this class.
 */
@SuppressWarnings("serial")
public class Ants extends JPanel{

	public static enum Pattern{
		Clear, Random, Filled, Bridge1, Bridge2, Bridge4 ;
	}
	
	//ラジオボタンの選択肢(GOAL = FOODS :foodsは別のオブジェクトで宣言してるため)
	public static enum Tile{
		OBSTACLE, NEST, GOAL, CLEAR;
	}
	
	private Tile tile = Tile.GOAL;
	
	//グリッド数
	int rows = 25;
	int columns = 25;
	Cell [][] cellArray = new Cell[columns][rows];
	
	private int maxAnts = 100;
// 0712 takaki added_s
  public static int stopCountOnDetour_1 = 25;  // 迂回経路（目的地から遠ざかるセル)を選択する場合の停止回数上限 (1)
  public static int stopCountOnDetour_2 = 50;  // 迂回経路（目的地から遠ざかるセル)を選択する場合の停止回数上限 (2)
// 0712 takaki added_e
	private List<Ant> ants = new ArrayList<Ant>();
	
	private Set<Cell> nests = new HashSet<Cell>();
	private Set<Cell> food = new HashSet<Cell>();
	
	//タイルとアリの関係性について定義(TODO　継承でない理由はなぜか？)
	AdvancedControlPanel advancedControlPanel;
	
	//aboutのメッセージ小ウィンドウ
	final JInternalFrame aboutFrame = new JInternalFrame("About", false, true);
	
	//最初に開くメッセージメッセージ小ウィンドウ
	final JInternalFrame messageFrame = new JInternalFrame("Getting Started", false, true);

	//★深田追加点
	private int[] stopCount=new int[maxAnts];
	//
	private int stop_count;
	
	/**網目状のウィンドウ部を制御*/
	public Ants(){
		
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		//開始時のテキスト表を宣言
		JLabel messageLabel1 = new JLabel("Place some food and obstacles in the environment and press play,");
		JLabel messageLabel2 = new JLabel("then experiment by changing the environment!");
		JLabel messageLabel3 = new JLabel("For more info, see About from the Advanced panel.");
		
		//テキスト表示位置を指定
		messageFrame.add(messageLabel1, BorderLayout.NORTH);
		messageFrame.add(messageLabel2, BorderLayout.CENTER);
		messageFrame.add(messageLabel3, BorderLayout.SOUTH);
		
		//テキストをウインドウに追加
		add(messageFrame);
		
		//動作不明
		messageFrame.pack();
		
		//ウインドウの表示位置を指定
		messageFrame.setLocation(300, 300);
		//表示指示
		messageFrame.setVisible(true);
		
		
		//Aboutボタンの動作を設定
		JPanel aboutPanel = new JPanel();
		aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
		
		//Aboudの表示文字列などを設定
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel.add(new JLabel("Auto-adjust: Automatically adjust parameters over time."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel.add(new JLabel("Deltas: How fast each parameter should adjust."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel.add(new JLabel("Max Pheromone: The maximum pheromone allowed in the environment."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel.add(new JLabel("Evaporation: How fast pheromones dissipate."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel.add(new JLabel("Dropoff: Pheromones get weaker further from nests or food."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel.add(new JLabel("Trail Strength: How strictly ants follow strongest pheromones."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel.add(new JLabel("Adapt Time: How fast all parameters should adjust."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel.add(new JLabel("Food Needed: One: Ants must find one food before returning."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel.add(new JLabel("Food Needed: All: Ants must find all food before returning. (TSP)"));
		aboutPanel.add(Box.createVerticalStrut(10));
		
		//パネルをウインドウに登録
		aboutFrame.setContentPane(aboutPanel);
		aboutFrame.pack();
		
		add(aboutFrame);
		aboutFrame.setLocation(300, 300);
		
		//マウス操作時の挙動を設定 (TODO リファクタリングcellArrayについて、クリックしたものしか動かさないならローカル変数でオブジェクト化すべき。)
		addMouseListener(new MouseAdapter(){
			@Override
			
			//クリックしたとき
			public void mouseClicked(MouseEvent e){
				
				//クリックした場所を問わず実行
				
				//初期メッセージ消去
				messageFrame.dispose();
				aboutFrame.setVisible(false);
				
				//クリックした場所をセル番号に変換して特定(少数点以下切り捨て)
				int clickedCellColumn = (int) (((double)e.getX())/getWidth() * columns);
				int clickedCellRow = (int) (((double)e.getY())/getHeight() * rows);
				
				if(clickedCellColumn < 0 || clickedCellColumn >= columns || clickedCellRow < 0 || clickedCellRow >= rows){
					//枠外でのクリックは反応しない
					return;
				}
				
				//クリックしたセルの状態を初期化　＊右クリックでリセット
				cellArray[clickedCellColumn][clickedCellRow].setIsGoal(false);
				food.remove(cellArray[clickedCellColumn][clickedCellRow]);
				cellArray[clickedCellColumn][clickedCellRow].setIsObstacle(false);
				cellArray[clickedCellColumn][clickedCellRow].setHasNest(false);
				nests.remove(cellArray[clickedCellColumn][clickedCellRow]);
				
				//左クリック
				if(e.getButton() == MouseEvent.BUTTON1){
					
					//Place Tileに応じて動作
					if(Tile.OBSTACLE.equals(tile)){
						//障害物
						cellArray[clickedCellColumn][clickedCellRow].setIsObstacle(true);
					}
					else if(Tile.GOAL.equals(tile)){
						//えさ場
						cellArray[clickedCellColumn][clickedCellRow].setIsGoal(true);
						food.add(cellArray[clickedCellColumn][clickedCellRow]);
					}
					else if(Tile.NEST.equals(tile)){
						//巣の出入り口
						cellArray[clickedCellColumn][clickedCellRow].setHasNest(true);
						nests.add(cellArray[clickedCellColumn][clickedCellRow]);
					}
				}
				
				//ステップ数の初期化（0に設定）
				advancedControlPanel.environmentChanged();
				
				repaint();
			}
		});
		
		addMouseMotionListener(new MouseAdapter(){
			
			//ドラッグ位置の初期化
			private int previousColumn = -1;
			private int previousRow = -1;
			
			@Override
			
			//ドラッグしたとき
			public void mouseDragged(MouseEvent e){
				//ドラッグ後の位置を取得
				int clickedCellColumn = (int) (((double)e.getX())/getWidth() * columns);
				int clickedCellRow = (int) (((double)e.getY())/getHeight() * rows);
				
				//グリッド外の判定
				if(clickedCellColumn < 0 || clickedCellColumn >= columns || clickedCellRow < 0 || clickedCellRow >= rows){
					return;
				}
				
				//別のセルに移動していることを判定
				if(clickedCellColumn != previousColumn || clickedCellRow != previousRow){
					
					//クリック時と同じ処理
					cellArray[clickedCellColumn][clickedCellRow].setIsGoal(false);
					food.remove(cellArray[clickedCellColumn][clickedCellRow]);
					cellArray[clickedCellColumn][clickedCellRow].setIsObstacle(false);
					cellArray[clickedCellColumn][clickedCellRow].setHasNest(false);
					nests.remove(cellArray[clickedCellColumn][clickedCellRow]);
					
					if(Tile.OBSTACLE.equals(tile)){
						cellArray[clickedCellColumn][clickedCellRow].setIsObstacle(true);
					}
					else if(Tile.GOAL.equals(tile)){
						cellArray[clickedCellColumn][clickedCellRow].setIsGoal(true);
						food.add(cellArray[clickedCellColumn][clickedCellRow]);
					}
					else if(Tile.NEST.equals(tile)){
						cellArray[clickedCellColumn][clickedCellRow].setHasNest(true);
						nests.add(cellArray[clickedCellColumn][clickedCellRow]);
					}
					
					advancedControlPanel.environmentChanged();
					repaint();
					
					//ドラッグ後の位置を取得
					previousColumn = clickedCellColumn;
					previousRow = clickedCellRow;
				}
			}
		});
		
		//背景色の初期設定
		setBackground(Color.WHITE);
		
		//初期化
		killAllCells();
	}
	
	/**
	 * グリッドサイズの変更
	 * @param columns
	 * @param rows
	 */
	
	

	
	
	public void setGridSize(int columns, int rows){
		this.columns = columns;
		this.rows = rows;
		cellArray = new Cell[columns][rows];
		killAllCells();
		ants.clear();
		
		cellArray[columns/2][rows/2].setHasNest(true);
		nests.add(cellArray[columns/2][rows/2]);
		
		repaint();
	}
	
	/**
	 * 初期化
	 */
	public void killAllCells(){
		nests.clear();//出入り口の位置記憶を初期化
		food.clear();//餌の位置記憶を初期化
		for(int column = 0; column < columns; column++){
			for(int row = 0; row < rows; row++){
				cellArray[column][row] = new Cell(column, row);
			}
		}
		if(advancedControlPanel != null){
			advancedControlPanel.environmentChanged();
		}
		repaint();
	}
	
	/**
	 * パターン変更時の処理
	 */
	public void setPattern(Pattern newPattern){
		
		killAllCells();
		
		if(newPattern.equals(Pattern.Filled)){
			//すべてを障害物に設定する
			for(int column = 0; column < columns; column++){
				for(int row = 0; row < rows; row++){
					cellArray[column][row].setIsObstacle(true);
				}
			}
		}
		
		else if(newPattern.equals(Pattern.Random)){
			//ランダムに障害物設定する（ここでは3/10の確率で障害物になる)
			for(int column = 0; column < columns; column++){
				for(int row = 0; row < rows; row++){
					if(Math.random() < .3){
						cellArray[column][row].setIsObstacle(true);
					}
				}
			}
			
			//真ん中の障害物を取り除いて、巣の出入り口を設定する
			cellArray[columns/2][rows/2].setIsObstacle(false);
			cellArray[columns/2][rows/2].setHasNest(true);
			nests.add(cellArray[columns/2][rows/2]);
		}
		
		//橋を読み込む
		//中心より±2列から±3行の範囲を橋とするための障害物を置く
		//橋1本
		else if(newPattern.equals(Pattern.Bridge1)){
			for(int column = 0; column < columns; column++){
				for(int row = 0; row < rows; row++){
					if((row >= rows/2 - 3 && row <= rows/2 + 3) && 
							(column <= columns/2 - 2 ||column >= columns/2 + 2)){				
						cellArray[column][row].setIsObstacle(true);
						Ant.numBridge = 1;
					}
				}
			}
		//橋2本(0706追加)
		}else if(newPattern.equals(Pattern.Bridge2)){
			for(int column = 0; column < columns; column++){
				for(int row = 0; row < rows; row++){
					if((row >= rows/2 - 3 && row <= rows/2 + 3) &&
							((column <= columns/3 - 2 || column >= columns/3 + 2) &&
									(column <= 2*(columns/3) - 2 || column >= 2*(columns/3) + 2))){
						cellArray[column][row].setIsObstacle(true);
						Ant.numBridge = 2;
					}
				}
			}
		//橋4本(0706追加)
		}else if(newPattern.equals(Pattern.Bridge4)){
			for(int column = 0; column < columns; column++){
				for(int row = 0; row < rows; row++){
					if((row >= rows/2 - 3 && row <= rows/2 + 3) &&
							((column <= columns/5 - 2 || column >= columns/5 + 2) &&
									(column <= 2*(columns/5) - 2 || column >= 2*(columns/5) + 2) &&
									(column <= 3*(columns/5) - 2 || column >= 3*(columns/5) + 2) &&
									(column <= 4*(columns/5) - 2 || column >= 4*(columns/5) + 2))){
						cellArray[column][row].setIsObstacle(true);
						Ant.numBridge = 4;
					}
				}
			}
		}
		
		//再描画
		repaint();
	}
	
	/**
	 * グリッドの描画
	 */
	@Override
	public void paintComponent(Graphics g){
		
		super.paintComponent(g);
		
		g.setColor(Color.BLACK);
		/**横幅*/
		double cellWidth = (double)getWidth()/columns;
		/**高さ*/
		double cellHeight = (double)getHeight()/rows;
		
		//50行50列以下の場合のみ描画
		if(columns <= 50 && rows <= 50){
			
			for(int column = 0; column < columns; column++){
				int cellX = (int) (cellWidth * column);
				g.drawLine(cellX, 0, cellX, getHeight());
			}
			
			for(int row = 0; row < rows; row++){
				int cellY = (int) (cellHeight * row);
				g.drawLine(0, cellY, getWidth(), cellY);
			}
		}
		
		//背景色の設定
		for(int column = 0; column < columns; column++){
			for(int row = 0; row < rows; row++){
				
				int cellX = (int) (cellWidth * column);
				int cellY = (int) (cellHeight * row);
				
				int thisCellWidth = (int) (cellWidth*(column+1) - cellX);
				int thisCellHeight = (int) (cellHeight*(row+1) - cellY);
				
				if(cellArray[column][row].hasNest()){
					g.setColor(Color.ORANGE);
				}
				
				else if(cellArray[column][row].isGoal()){
					//餌場の色をランダムに設定
					Random random = new Random(cellArray[column][row].hashCode());
					g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), 255));
				}
				
				else if(cellArray[column][row].isBlocked()){
					//障害物の色を設定
					g.setColor(Color.GRAY);
				}
				else{
					//フェロモンの色指定
					double nestPheromone = Math.min(1, (cellArray[column][row].nestPheromoneLevel-1)/Cell.maxNestPheromoneLevel);
					double foodPheromone = 0;
					double maxFood = 0;
					Cell maxFoodCell = null;
					
					for(Cell food : getFood()){
						if(cellArray[column][row].getFoodPheromoneLevel(food) > maxFood){
							maxFood = cellArray[column][row].getFoodPheromoneLevel(food);
							maxFoodCell = food;
						}
						foodPheromone = Math.max(foodPheromone, Math.min(1, (cellArray[column][row].getFoodPheromoneLevel(food)-1)/Cell.maxFoodPheromoneLevel));
					}
					
					if(nestPheromone > foodPheromone){
						//帰巣フェロモンの濃度を設定（緑）
						g.setColor(new Color(0, 255, 0, (int) (255*nestPheromone)));
					}
					else if(maxFoodCell != null ){
						//フェロモンの強さで濃度を設定
						Random random = new Random(maxFoodCell.hashCode());
						g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), (int) (255*foodPheromone)));
						
					}
					else{
						g.setColor(Color.white);
					}
				}
				//塗りつぶし実行
				g.fillRect(cellX+1, cellY+1, Math.max(thisCellWidth-1, 1), Math.max(thisCellHeight-1, 1));
			}
		}
		
		for(Ant ant : ants){
			
			//アリの色設定
			int column = ant.getCol();
			int row = ant.getRow();
			int cellX = (int) (cellWidth * column);
			int cellY = (int) (cellHeight * row);
			int thisCellWidth = (int) (cellWidth*(column+1) - cellX);
			int thisCellHeight = (int) (cellHeight*(row+1) - cellY);
			
			if(ant.isReturningHome()){
				//帰巣中は青
				g.setColor(Color.BLUE);
			}
			else{
//kimura_s
				//★深田追加点：停止中のアリの色
				//停止回数が多いほど濃い緑(40回以上は黒)
				if(ant.isStop()) {	
//0711takaki change_s
//          int sc = ant.stop_count(true);
          int sc = ant.stop_count();
//0711takaki change_e
					//int sc = ant.stop_count0();
					//stopCount[ant.number()]++;
					//止まった回数が多いほど濃い緑（40回以上は黒）
					//g.setColor(new Color(0,255-num_count[ant.number()],0));//変化わかりづらい
					/*
					if((0<stopCount[ant.number()])&&(stopCount[ant.number()]<10))g.setColor(new Color(0,200,0));
					else if((10<=stopCount[ant.number()])&&(stopCount[ant.number()]<20))g.setColor(new Color(0,150,0));
					else if((20<=stopCount[ant.number()])&&(stopCount[ant.number()]<30))g.setColor(new Color(0,100,0));
					else if((30<=stopCount[ant.number()])&&(stopCount[ant.number()]<40))g.setColor(new Color(0,50,0));
					else g.setColor(new Color(0,0,0));
					*/
//0709miyamoto_s
					if((0<sc)&&(sc<10))g.setColor(new Color(0,200,0));
					else if((10<=sc)&&(sc<20))g.setColor(new Color(0,150,0));
					else if((20<=sc)&&(sc<30))g.setColor(new Color(0,100,0));
					else if((30<=sc)&&(sc<40))g.setColor(new Color(0,50,0));
					else g.setColor(new Color(0,0,0));
//0709miyamoto_e
				}
				else if(!ant.isStop()){
//0711takaki change_s
//					//進んだら止まった回数をリセット
//					//stopCount[ant.number()]=0;
//					ant.stop_count(false);
//0711takaki change_e
				//橋北のアリは赤
				if(ant.isNorth()) {
					g.setColor(Color.RED);
				}
				//橋南のアリは黒
				else {
					g.setColor(Color.BLUE);
				}
				}
//kimura_e
			}
			//塗りつぶし(フェロモン系より一回り小さい。)
			g.fillRect(cellX+2, cellY+2, thisCellWidth-3, thisCellHeight-3);
		}
	}
	
	public Set<Cell> getFood(){
		return food;
	}
	
	public Set<Cell> getNests(){
		return nests;
	}
	
	//追加 : Antで中心座標を得たいので
	public int getColumns(){
		return columns;
	}
	public int getRows(){
		return rows;
	}

	//int num = 0; 
	int all_steps=0;//0711fukata追加：総ステップ数(宣言、初期化)
	public void step(){
		all_steps++;//0711fukata追加：総ステップ数をカウント
		//num = (num + 1)%maxAnts;
		//アリ生成
		if(ants.size() < maxAnts){
			if(!nests.isEmpty()){
				int num = (int) (food.size() * Math.random());
				int nestIndex = (int) (nests.size() * Math.random());
				// num(アリの生成番号(生成された順))を追加 : Antで目的地の割り振りに使用するため
				ants.add(new Ant((Cell) nests.toArray()[nestIndex], cellArray, this, num));
				//num = num + 1; 
			}
		}
		else if(ants.size() > maxAnts){
//0709miyamoto_s
			Ant ant = ants.get(0);
			int x = ant.getCol();
			int y = ant.getRow();
			isnotAnt(x,y);
//0709miyamoto_e
			ants.remove(0);
		}
		
		for(Ant ant : ants){
			//アリをワンステップ動かす
			ant.step();
		}
		
		for(int column = 0; column < columns; column++){
			for(int row = 0; row < rows; row++){
				//フェロモンが弱まる
				cellArray[column][row].step();
			}
		}
		
		repaint();
	}
	
	public void isAnt(int x, int y){
		cellArray[x][y].setHasAnt(true);
	}
	
	public void isnotAnt(int x, int y){
		cellArray[x][y].setHasAnt(false);
	}
	
	
	public void showAboutFrame(){
		messageFrame.dispose();
		aboutFrame.setVisible(true);
	}
	
	public void setTileToAdd(Tile tile) {
		this.tile = tile;
	}
	
	public void setMaxAnts(int maxAnts) {
		this.maxAnts = maxAnts;
		while(ants.size() > maxAnts){
//0709miyamoto_s
			Ant ant = ants.get(0);
			int x = ant.getCol();
			int y = ant.getRow();
			isnotAnt(x,y);
//0709miyamoto_e
			ants.remove(0);
		}
	}

// 0712 takaki added_s
  public void setStopCountDetour(int slider_stop_count) {
    this.stopCountOnDetour_1 = slider_stop_count * 5;
    this.stopCountOnDetour_2 = slider_stop_count * 10;
  }
// 0712 takaki added_e
}
