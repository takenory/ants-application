import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Controller of the Ants class.
 */
public class AntsControlPanel {

	//private Ants ants; //miyamoto
	static Ants ants;
	
	private JPanel panel = new JPanel();
	
	//private JButton timerButton = new JButton("\u25BA"); //miyamoto
	static JButton timerButton = new JButton("\u25BA");
	
	private JSlider speedSlider;
	private JComboBox patternComboBox;
	private JComboBox townComboBox; //miyamoto
	private JComboBox sizeComboBox; 
	
	//private final AdvancedControlPanel advancedPanel; //miyamoto
	static AdvancedControlPanel advancedPanel;
	
	private JRadioButton foodRequiredAll = new JRadioButton("All");
	private JRadioButton foodRequiredOne = new JRadioButton("One");
	private JRadioButton RestrictBridge = new JRadioButton("Restrict");
	private JRadioButton donotRestrictBridge = new JRadioButton("don't Restrict");

	//0711fukata追加：総ステップ数を表示するためのラベル //0713miyamoto
	static JLabel stepLabel = new JLabel();
	
	//private Timer stepTimer = new Timer(0, new ActionListener(){
	static Timer stepTimer = new Timer(0, new ActionListener(){
		public void actionPerformed(ActionEvent e){
			step();
		}
	});
	
	/**
	 * 右コントローラ宣言
	 */
	public AntsControlPanel(final Ants ants, final AdvancedControlPanel advancedPanel){
		this.ants = ants;
		this.advancedPanel = advancedPanel;
		
		Dimension controlDimension = new Dimension(75, 25);
				
		timerButton.setMinimumSize(controlDimension);
		timerButton.setMaximumSize(controlDimension);
		timerButton.setPreferredSize(controlDimension);
		timerButton.setFocusable(false);
		timerButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		timerButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(stepTimer.isRunning()){
					pause();
//0713miyamoto changed_s
					//0711fukata追加：pauseボタンを押したとき、総ステップ数を表示
					//stepLabel.setText("\n"+"all steps:"+ants.all_steps+"\n");
					//stepLabel.setText("\n" + "number of ants\n" + ants.AntisGoal);
					//stepLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
//0713miyamoto changed_e
				}
				else{
					start();
				}
			}
		});
		
		final JButton stepButton = new JButton("Step");
		stepButton.setMinimumSize(controlDimension);
		stepButton.setMaximumSize(controlDimension);
		stepButton.setPreferredSize(controlDimension);
		stepButton.setFocusable(false);
		stepButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		stepButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				pause();
				step();
//0713miyamoto changed_s
				//0711fukata追加：stepボタンを押したとき、総ステップ数を表示
				//stepLabel.setText("\n"+"all steps:"+ants.all_steps+"\n");
				//stepLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
//0713miyamoto changed_e
			}
		});
		
		JLabel antsLabel = new JLabel("Ant Count:");
		antsLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		final JSlider antsSlider = new JSlider();
		antsSlider.setMinimumSize(controlDimension);
		antsSlider.setMaximumSize(controlDimension);
		antsSlider.setPreferredSize(controlDimension);
		antsSlider.setMinimum(1);
		antsSlider.setMaximum(500);//200から500に変更(0709miyamoto)
		antsSlider.setMajorTickSpacing(500);
		antsSlider.addChangeListener(new ChangeListener(){
			
			@Override
			public void stateChanged(ChangeEvent e) {
				ants.setMaxAnts(antsSlider.getValue());
				ants.repaint();
			}
			
		});
		antsSlider.setValue(500);
		
// 0712 takaki added_s
    JLabel detourCountLabel = new JLabel("Detour Count:");
    detourCountLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    final JSlider detourCountSlider = new JSlider();
    detourCountSlider.setMinimumSize(controlDimension);
    detourCountSlider.setMaximumSize(controlDimension);
    detourCountSlider.setPreferredSize(controlDimension);
    detourCountSlider.setMinimum(1);
    detourCountSlider.setMaximum(10);
    detourCountSlider.setMajorTickSpacing(1);
    detourCountSlider.addChangeListener(new ChangeListener(){
      @Override
      public void stateChanged(ChangeEvent e) {
        ants.setDetourCount(detourCountSlider.getValue());
        ants.repaint();
      }
    });
    detourCountSlider.setValue(10);

    JLabel stopCountOnDetourMinLabel = new JLabel("Stop Count on Detour:");
    stopCountOnDetourMinLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    final JSlider stopCountOnDetourMinSlider = new JSlider();
    stopCountOnDetourMinSlider.setMinimumSize(controlDimension);
    stopCountOnDetourMinSlider.setMaximumSize(controlDimension);
    stopCountOnDetourMinSlider.setPreferredSize(controlDimension);
    stopCountOnDetourMinSlider.setMinimum(5);
    stopCountOnDetourMinSlider.setMaximum(100);
    stopCountOnDetourMinSlider.setMajorTickSpacing(5);
    stopCountOnDetourMinSlider.addChangeListener(new ChangeListener(){

    @Override
    public void stateChanged(ChangeEvent e) {
      ants.setStopCountOnDetourMin(stopCountOnDetourMinSlider.getValue());
      ants.repaint();
    }

    });
    stopCountOnDetourMinSlider.setValue(5);
// 0712 takaki added_e

		JPanel blockPanel = new JPanel();
		blockPanel.setLayout(new BoxLayout(blockPanel, BoxLayout.Y_AXIS));
		blockPanel.setBorder(BorderFactory.createTitledBorder("Place Tile"));
		
		JRadioButton obstacle = new JRadioButton("Obstacle");
		obstacle.setFocusable(false);
		obstacle.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				ants.setTileToAdd(Ants.Tile.OBSTACLE);
			}
		});
		
		JRadioButton nest = new JRadioButton("Nest");
		nest.setFocusable(false);
		nest.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				ants.setTileToAdd(Ants.Tile.NEST);
			}
		});
		JRadioButton goal = new JRadioButton("Food");
		goal.setFocusable(false);
		goal.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				ants.setTileToAdd(Ants.Tile.GOAL);
			}
		});
		JRadioButton clear = new JRadioButton("Clear");
		clear.setFocusable(false);
		clear.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				ants.setTileToAdd(Ants.Tile.CLEAR);
			}
		});
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(obstacle);
		bg.add(nest);
		bg.add(goal);
		bg.add(clear);
		goal.setSelected(true);
		
		blockPanel.add(obstacle);
		blockPanel.add(nest);
		blockPanel.add(goal);
		blockPanel.add(clear);
		
		blockPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		JLabel sizeLabel = new JLabel("Size:");
		sizeLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		sizeComboBox = new JComboBox();
		sizeComboBox.setFocusable(false);
		sizeComboBox.setMinimumSize(controlDimension);
		sizeComboBox.setMaximumSize(controlDimension);
		sizeComboBox.setPreferredSize(controlDimension);
		//sizeComboBox.addItem("10 X 10");
		sizeComboBox.addItem("25");
		sizeComboBox.addItem("50");
		sizeComboBox.addItem("100");
		
		JLabel speedLabel = new JLabel("Speed:");
		speedLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		speedSlider = new JSlider();
		speedSlider.setMinimumSize(controlDimension);
		speedSlider.setMaximumSize(controlDimension);
		speedSlider.setPreferredSize(controlDimension);
		speedSlider.setMinimum(1200);
		speedSlider.setMaximum(2100);
		speedSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				stepTimer.setDelay(2100 - speedSlider.getValue());
			}

		});
		speedSlider.setValue(2050);

//miyamoto_s
		JPanel RestrictBridgePanel = new JPanel();
		RestrictBridgePanel.setLayout(new BoxLayout(RestrictBridgePanel, BoxLayout.Y_AXIS));
		RestrictBridgePanel.setBorder(BorderFactory.createTitledBorder("Restrict Bridge"));
		RestrictBridgePanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);


		RestrictBridge.setMinimumSize(controlDimension);
		RestrictBridge.setMaximumSize(controlDimension);
		RestrictBridge.setPreferredSize(controlDimension);
		RestrictBridge.setFocusable(false);
		RestrictBridge.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Ant.RestrictBridge = true;
				advancedPanel.environmentChanged();
			}

		});

		donotRestrictBridge.setFocusable(false);
		donotRestrictBridge.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Ant.RestrictBridge = false;
				advancedPanel.environmentChanged();
			}
		});

		ButtonGroup RestrictBridgeGroup = new ButtonGroup();
		RestrictBridgeGroup.add(RestrictBridge);
		RestrictBridgeGroup.add(donotRestrictBridge);

		RestrictBridgePanel.add(RestrictBridge);
		RestrictBridgePanel.add(donotRestrictBridge);


		donotRestrictBridge.setSelected(true);
//miyamoto_e
		
		/*
		JPanel foodRequiredPanel = new JPanel();
		foodRequiredPanel.setLayout(new BoxLayout(foodRequiredPanel, BoxLayout.Y_AXIS));
		foodRequiredPanel.setBorder(BorderFactory.createTitledBorder("Food Needed"));
		foodRequiredPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);


		foodRequiredAll.setMinimumSize(controlDimension);
		foodRequiredAll.setMaximumSize(controlDimension);
		foodRequiredAll.setPreferredSize(controlDimension);
		foodRequiredAll.setFocusable(false);
		foodRequiredAll.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Ant.allFoodRequired = true;
				advancedPanel.environmentChanged();
			}

		});

		foodRequiredOne.setFocusable(false);
		foodRequiredOne.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Ant.allFoodRequired = false;
				advancedPanel.environmentChanged();
			}
		});

		ButtonGroup foodRequiredGroup = new ButtonGroup();
		foodRequiredGroup.add(foodRequiredAll);
		foodRequiredGroup.add(foodRequiredOne);

		foodRequiredPanel.add(foodRequiredAll);
		foodRequiredPanel.add(foodRequiredOne);


		foodRequiredOne.setSelected(true);
		*/
		
//miyamoto_s
		JLabel patternLabel = new JLabel("Bridge:");
		patternLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		patternComboBox = new JComboBox();
		patternComboBox.setFocusable(false);
		patternComboBox.setMinimumSize(controlDimension);
		patternComboBox.setMaximumSize(controlDimension);
		patternComboBox.setPreferredSize(controlDimension);

		patternComboBox.addItem(Ants.Pattern.Clear);
		//patternComboBox.addItem(Ants.Pattern.Filled);
		//patternComboBox.addItem(Ants.Pattern.Random);
		patternComboBox.addItem(Ants.Pattern.Bridge1);
		patternComboBox.addItem(Ants.Pattern.Bridge2);
		patternComboBox.addItem(Ants.Pattern.Bridge4);
		
		patternComboBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(patternComboBox.getSelectedIndex() != -1){
					ants.setPattern((Ants.Pattern) patternComboBox.getSelectedItem());
					patternComboBox.setSelectedIndex(-1);
				}

			}
		});

		patternComboBox.setSelectedIndex(-1);

		
		JLabel townLabel = new JLabel("town:");
		townLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		townComboBox = new JComboBox();
		townComboBox.setFocusable(false);
		townComboBox.setMinimumSize(controlDimension);
		townComboBox.setMaximumSize(controlDimension);
		townComboBox.setPreferredSize(controlDimension);

		townComboBox.addItem(Ants.Towns.Clear);
		//townComboBox.addItem(Ants.Pattern.Filled);
		//townComboBox.addItem(Ants.Pattern.Random);
		townComboBox.addItem(Ants.Towns.Town1);
		//追加
		townComboBox.addItem(Ants.Towns.Town2);
		townComboBox.addItem(Ants.Towns.Town3);
		townComboBox.addItem(Ants.Towns.Town4);
		townComboBox.addItem(Ants.Towns.Town5);
		
		townComboBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(townComboBox.getSelectedIndex() != -1){
					ants.setTown((Ants.Towns) townComboBox.getSelectedItem());
					townComboBox.setSelectedIndex(-1);
				}

			}
		});

		townComboBox.setSelectedIndex(-1);		
//miyamoto_e

		sizeComboBox.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(sizeComboBox.getSelectedItem().equals("10")){
					AntsControlPanel.this.ants.setGridSize(10, 10);
				}
				else if(sizeComboBox.getSelectedItem().equals("25")){
					AntsControlPanel.this.ants.setGridSize(25, 25);
				}
				else if(sizeComboBox.getSelectedItem().equals("50")){
					AntsControlPanel.this.ants.setGridSize(50, 50);
				}
				else if(sizeComboBox.getSelectedItem().equals("100")){
					AntsControlPanel.this.ants.setGridSize(100, 100);
				}
			}
		});

		sizeComboBox.setSelectedItem("50");

		final JCheckBox showAdvanced = new JCheckBox("Advanced");
		showAdvanced.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		showAdvanced.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				advancedPanel.getPanel().setVisible(showAdvanced.isSelected());
			}

		});
		advancedPanel.getPanel().setVisible(showAdvanced.isSelected());


		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(timerButton);
		panel.add(stepButton);
		panel.add(speedLabel);
		panel.add(speedSlider);
		panel.add(Box.createGlue());
		panel.add(antsLabel);
		panel.add(antsSlider);
		
// 0712 takaki added_s
    panel.add(detourCountLabel);
    panel.add(detourCountSlider);
    panel.add(stopCountOnDetourMinLabel);
    panel.add(stopCountOnDetourMinSlider);
// 0712 takaki added_s
    
		panel.add(Box.createGlue());
		panel.add(blockPanel);
		panel.add(Box.createGlue());
		panel.add(sizeLabel);
		panel.add(sizeComboBox);
		panel.add(Box.createGlue());
		panel.add(RestrictBridgePanel);
		panel.add(Box.createGlue());
		panel.add(patternLabel);
		panel.add(patternComboBox);
		
//miyamoto_s
		panel.add(townLabel);
		panel.add(townComboBox);	
//miyamoto_e
		
		panel.add(Box.createGlue());
		panel.add(showAdvanced);
		//0711fukata追加：stepLabelをパネルに追加
		panel.add(Box.createGlue());
		panel.add(stepLabel);
	}

	
	public void start(){
		stepTimer.restart();
		timerButton.setText("Pause");
	}

//miyamoto_s
	//public void pause(){
	public static void pause(){
		stepTimer.stop();
		timerButton.setText("\u25BA");
		stepLabel.setText("\n" + "number of ants\n" + ants.AntisGoal);
		stepLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	}
//miyamoto_e
	
	//public void step(){
	static void step(){
		ants.step();
		advancedPanel.step();
	}

	public JPanel getPanel(){
		return panel;
	}
	
}
