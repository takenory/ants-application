import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Advanced controller of the Ants class.
 */
public class AdvancedControlPanel {

	private Ants ants;
	private JPanel panel = new JPanel();

	
	//minimum values for parameters
	private final double minMaxpheromone = 10;
	private final double minEvaporation = 2;
	private final double minDropoff = 5;
	private final double minTrail = 0;
	private final double minStepsToMax = 100;
	
	//maximum values for parameters
	private final double maxMaxpheromone = 100;
	private final double maxEvaporation = 10;
	private final double maxDropoff = 25;
	private final double maxTrail = 100;
	private final double maxStepsToMax = 2000;
	
	//start values for parameters
	private double maxpheromone = maxMaxpheromone;
	private double evaporation = minEvaporation;
	private double dropoff = minDropoff;
	private double chanceOfBestNext = 50;
	private double stepsToMax = 1000;
	
	//when environment changes, timeout to adjust
	private double changedTimeout = stepsToMax;

	//delta values adjust parameters on the fly
	private double deltapheromone = 1000;
	private double deltaEvaporation = 250;
	private double deltaDropoff = 250;
	private double deltaTrail = 1000;

	//whether adjustments should be made automatically
	private final JCheckBox adjustCheckBox = new JCheckBox("Auto-Adjust");

	//sliders to adjust environment parameters
	private final JSlider maxpheromoneSlider = new JSlider();
	private final JSlider evaporationRateSlider = new JSlider();
	private final JSlider dropoffRateSlider = new JSlider();
	private final JSlider chanceOfBestNextSlider = new JSlider();

	public AdvancedControlPanel(final Ants ants){
		this.ants = ants;

		ants.advancedControlPanel = this;

		Dimension controlDimension = new Dimension(75, 25);

		JLabel maxpheromoneLabel = new JLabel("Max pheromone");
		maxpheromoneLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);


		maxpheromoneSlider.setMinimumSize(controlDimension);
		maxpheromoneSlider.setMaximumSize(controlDimension);
		maxpheromoneSlider.setPreferredSize(controlDimension);
		maxpheromoneSlider.setMinimum((int)minMaxpheromone);
		maxpheromoneSlider.setMaximum((int) maxMaxpheromone);
		maxpheromoneSlider.setMajorTickSpacing(100);
		maxpheromoneSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {

				Cell.maxFoodPheromoneLevel = maxpheromoneSlider.getValue();
				Cell.maxNestPheromoneLevel = maxpheromoneSlider.getValue();
				ants.repaint();

			}

		});
		maxpheromoneSlider.setValue((int) maxpheromone);


		final JLabel deltapheromoneLabel = new JLabel("\u0394 pheromone");
		deltapheromoneLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		final JSlider deltapheromoneSlider = new JSlider();
		deltapheromoneSlider.setMinimumSize(controlDimension);
		deltapheromoneSlider.setMaximumSize(controlDimension);
		deltapheromoneSlider.setPreferredSize(controlDimension);
		deltapheromoneSlider.setMinimum(10);
		deltapheromoneSlider.setMaximum(100);
		deltapheromoneSlider.setMajorTickSpacing(100);
		deltapheromoneSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {

				deltapheromone = ((double)deltapheromoneSlider.getValue());

			}

		});
		deltapheromoneSlider.setValue((int) deltapheromone);

		JPanel pheromonePanel = new JPanel();
		pheromonePanel.setLayout(new BoxLayout(pheromonePanel, BoxLayout.Y_AXIS));
		pheromonePanel.add(maxpheromoneLabel);
		pheromonePanel.add(maxpheromoneSlider);
		pheromonePanel.add(deltapheromoneLabel);
		pheromonePanel.add(deltapheromoneSlider);


		JLabel evaporationRateLabel = new JLabel("Evaporation");
		evaporationRateLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);


		evaporationRateSlider.setMinimumSize(controlDimension);
		evaporationRateSlider.setMaximumSize(controlDimension);
		evaporationRateSlider.setPreferredSize(controlDimension);
		evaporationRateSlider.setMinimum((int)minEvaporation);
		evaporationRateSlider.setMaximum((int) maxEvaporation);
		evaporationRateSlider.setMajorTickSpacing(10);
		evaporationRateSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {

				Cell.evaporationRate = (1000-evaporationRateSlider.getValue())/1000.0;

				ants.repaint();

			}

		});
		evaporationRateSlider.setValue((int) evaporation);


		final JLabel deltaEvaporationLabel = new JLabel("\u0394 Evaporation");
		deltaEvaporationLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		final JSlider deltaEvaporationSlider = new JSlider();
		deltaEvaporationSlider.setMinimumSize(controlDimension);
		deltaEvaporationSlider.setMaximumSize(controlDimension);
		deltaEvaporationSlider.setPreferredSize(controlDimension);
		deltaEvaporationSlider.setMinimum(10);
		deltaEvaporationSlider.setMaximum(100);
		deltaEvaporationSlider.setMajorTickSpacing(100);
		deltaEvaporationSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
					deltaEvaporation = ((double)deltaEvaporationSlider.getValue());
			}

		});
		deltaEvaporationSlider.setValue((int) deltaEvaporation);

		JPanel evaporationPanel = new JPanel();
		evaporationPanel.setLayout(new BoxLayout(evaporationPanel, BoxLayout.Y_AXIS));
		evaporationPanel.add(evaporationRateLabel);
		evaporationPanel.add(evaporationRateSlider);
		evaporationPanel.add(deltaEvaporationLabel);
		evaporationPanel.add(deltaEvaporationSlider);

		JLabel dropoffRateLabel = new JLabel("Dropoff");
		dropoffRateLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		dropoffRateSlider.setMinimumSize(controlDimension);
		dropoffRateSlider.setMaximumSize(controlDimension);
		dropoffRateSlider.setPreferredSize(controlDimension);
		dropoffRateSlider.setMinimum((int) minDropoff);
		dropoffRateSlider.setMaximum((int) maxDropoff);
		dropoffRateSlider.setMajorTickSpacing(100);
		dropoffRateSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				Ant.dropoffRate = (1000-dropoffRateSlider.getValue())/1000.0;
				ants.repaint();
			}
		});
		dropoffRateSlider.setValue((int) dropoff);

		final JLabel deltaDropoffLabel = new JLabel("\u0394 Dropoff");
		deltaDropoffLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		final JSlider deltaDropoffSlider = new JSlider();
		deltaDropoffSlider.setMinimumSize(controlDimension);
		deltaDropoffSlider.setMaximumSize(controlDimension);
		deltaDropoffSlider.setPreferredSize(controlDimension);
		deltaDropoffSlider.setMinimum(10);
		deltaDropoffSlider.setMaximum(100);
		deltaDropoffSlider.setMajorTickSpacing(100);
		deltaDropoffSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
					deltaDropoff = ((double)deltaDropoffSlider.getValue());
			}

		});
		deltaDropoffSlider.setValue((int) deltaDropoff);


		JPanel dropoffPanel = new JPanel();
		dropoffPanel.setLayout(new BoxLayout(dropoffPanel, BoxLayout.Y_AXIS));
		dropoffPanel.add(dropoffRateLabel);
		dropoffPanel.add(dropoffRateSlider);
		dropoffPanel.add(deltaDropoffLabel);
		dropoffPanel.add(deltaDropoffSlider);

		
		JPanel trailPanel = new JPanel();
		trailPanel.setLayout(new BoxLayout(trailPanel, BoxLayout.Y_AXIS));
		trailPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		final JLabel chanceOfBestNextLabel = new JLabel("Trail Strength");
		chanceOfBestNextLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		chanceOfBestNextSlider.setMinimumSize(controlDimension);
		chanceOfBestNextSlider.setMaximumSize(controlDimension);
		chanceOfBestNextSlider.setPreferredSize(controlDimension);
		chanceOfBestNextSlider.setMinimum(0);
		chanceOfBestNextSlider.setMaximum(100);
		chanceOfBestNextSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				Ant.bestCellNext = chanceOfBestNextSlider.getValue()/100.0;
			}
		});
		chanceOfBestNextSlider.setValue((int)chanceOfBestNext);
		chanceOfBestNextSlider.setPaintLabels(true);

		final JLabel deltaTrailLabel = new JLabel("\u0394 Trail");
		deltaTrailLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		final JSlider deltaTrailSlider = new JSlider();
		deltaTrailSlider.setMinimumSize(controlDimension);
		deltaTrailSlider.setMaximumSize(controlDimension);
		deltaTrailSlider.setPreferredSize(controlDimension);
		deltaTrailSlider.setMinimum(10);
		deltaTrailSlider.setMaximum(100);
		deltaTrailSlider.setMajorTickSpacing(100);
		deltaTrailSlider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				deltaTrail = ((double)deltaTrailSlider.getValue());
			}
		});
		deltaTrailSlider.setValue((int) deltaTrail);
			
		JPanel stepsToMaxPanel = new JPanel();
		stepsToMaxPanel.setLayout(new BoxLayout(stepsToMaxPanel, BoxLayout.Y_AXIS));
		stepsToMaxPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	
		final JLabel stepsToMaxLabel = new JLabel("Adapt Time");
		stepsToMaxLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		final JSlider stepsToMaxSlider = new JSlider();
		stepsToMaxSlider.setMinimumSize(controlDimension);
		stepsToMaxSlider.setMaximumSize(controlDimension);
		stepsToMaxSlider.setPreferredSize(controlDimension);
		stepsToMaxSlider.setMinimum((int)minStepsToMax);
		stepsToMaxSlider.setMaximum((int)maxStepsToMax);
		stepsToMaxSlider.setMajorTickSpacing(100);
		stepsToMaxSlider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				stepsToMax = ((double)stepsToMaxSlider.getValue());
			}
		});
		stepsToMaxSlider.setValue((int) stepsToMax);
		
		
		stepsToMaxPanel.add(stepsToMaxLabel);
		stepsToMaxPanel.add(stepsToMaxSlider);
		

		trailPanel.add(chanceOfBestNextLabel);
		trailPanel.add(chanceOfBestNextSlider);
		trailPanel.add(deltaTrailLabel);
		trailPanel.add(deltaTrailSlider);
	

		adjustCheckBox.setSelected(true);
		adjustCheckBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				maxpheromoneSlider.setEnabled(!adjustCheckBox.isSelected());
				evaporationRateSlider.setEnabled(!adjustCheckBox.isSelected());
				dropoffRateSlider.setEnabled(!adjustCheckBox.isSelected());
				chanceOfBestNextSlider.setEnabled(!adjustCheckBox.isSelected());
				
				deltapheromoneSlider.setVisible(adjustCheckBox.isSelected());
				deltapheromoneLabel.setVisible(adjustCheckBox.isSelected());
				deltaEvaporationSlider.setVisible(adjustCheckBox.isSelected());
				deltaEvaporationLabel.setVisible(adjustCheckBox.isSelected());
				deltaDropoffSlider.setVisible(adjustCheckBox.isSelected());
				deltaDropoffLabel.setVisible(adjustCheckBox.isSelected());
				deltaTrailSlider.setVisible(adjustCheckBox.isSelected());
				deltaTrailLabel.setVisible(adjustCheckBox.isSelected());
				stepsToMaxSlider.setVisible(adjustCheckBox.isSelected());
				stepsToMaxLabel.setVisible(adjustCheckBox.isSelected());
			}
		});

		maxpheromoneSlider.setEnabled(!adjustCheckBox.isSelected());
		evaporationRateSlider.setEnabled(!adjustCheckBox.isSelected());
		dropoffRateSlider.setEnabled(!adjustCheckBox.isSelected());
		chanceOfBestNextSlider.setEnabled(!adjustCheckBox.isSelected());

		deltapheromoneSlider.setVisible(adjustCheckBox.isSelected());
		deltapheromoneLabel.setVisible(adjustCheckBox.isSelected());
		deltaEvaporationSlider.setVisible(adjustCheckBox.isSelected());
		deltaEvaporationLabel.setVisible(adjustCheckBox.isSelected());
		deltaDropoffSlider.setVisible(adjustCheckBox.isSelected());
		deltaDropoffLabel.setVisible(adjustCheckBox.isSelected());
		deltaTrailSlider.setVisible(adjustCheckBox.isSelected());
		deltaTrailLabel.setVisible(adjustCheckBox.isSelected());
		stepsToMaxSlider.setVisible(adjustCheckBox.isSelected());
		stepsToMaxLabel.setVisible(adjustCheckBox.isSelected());
		
		
		JButton aboutButton = new JButton("About");
		aboutButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				ants.showAboutFrame();
			}
			
		});
		
		
		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
		checkBoxPanel.add(adjustCheckBox);
		checkBoxPanel.add(aboutButton);
		
		
		
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(Box.createGlue());
		panel.add(checkBoxPanel);
		panel.add(Box.createGlue());
		panel.add(pheromonePanel);
		panel.add(Box.createGlue());
		panel.add(evaporationPanel);
		panel.add(Box.createGlue());
		panel.add(dropoffPanel);
		panel.add(Box.createGlue());
		panel.add(trailPanel);
		panel.add(Box.createGlue());
		panel.add(stepsToMaxPanel);
		panel.add(Box.createGlue());
	}

	public void environmentChanged(){
		changedTimeout = 0;
	}

	public void step(){

		changedTimeout++;

		if(adjustCheckBox.isSelected()){

			//does ever food have a trail?
			boolean allFoodHasTrail = true;
			for(Cell food : ants.getFood()){
				if(!food.foodPheromoneLevelMap.containsKey(food) || food.foodPheromoneLevelMap.get(food) <= 1.0){
					allFoodHasTrail = false;
					break;
				}
			}
			
			//does every nest have a trail?
			boolean allNestsHaveTrail = true;
			for(Cell nest : ants.getNests()){
				if(nest.nestPheromoneLevel <= 1){
					allNestsHaveTrail = false;
					break;
				}
			}

			
			if(changedTimeout >= stepsToMax && allFoodHasTrail && allNestsHaveTrail){
				maxpheromone -= ((maxMaxpheromone-minMaxpheromone)/stepsToMax)*deltapheromone/stepsToMax;
				evaporation -= ((maxEvaporation-minEvaporation)/stepsToMax)*deltaEvaporation/stepsToMax;
				dropoff -= ((maxDropoff-minDropoff)/stepsToMax)*deltaDropoff/stepsToMax;
				
				chanceOfBestNext += ((maxTrail-minTrail)/stepsToMax)*deltaTrail/stepsToMax;	
			}
			else{
				maxpheromone += ((maxMaxpheromone-minMaxpheromone)/stepsToMax)*deltapheromone/stepsToMax;
				evaporation += ((maxEvaporation-minEvaporation)/stepsToMax)*deltaEvaporation/stepsToMax;
				dropoff += ((maxDropoff-minDropoff)/stepsToMax)*deltaDropoff/stepsToMax;
				
				chanceOfBestNext -= ((maxTrail-minTrail)/stepsToMax)*deltaTrail/stepsToMax;
			}
			
			checkDeltas();

			maxpheromoneSlider.setValue((int) maxpheromone);
			evaporationRateSlider.setValue((int) evaporation);
			dropoffRateSlider.setValue((int) dropoff);
			
			chanceOfBestNextSlider.setValue((int)chanceOfBestNext);
		}
	}
	
	private void checkDeltas(){
		
		if(maxpheromone < 0){
			maxpheromone = 0;
		}
		if(evaporation < 0){
			evaporation = 0;
		}
		if(dropoff < 0){
			dropoff = 0;
		}
		
		if(chanceOfBestNext < 0){
			chanceOfBestNext = 0;
		}
		
		if(maxpheromone > maxMaxpheromone){
			maxpheromone = maxMaxpheromone;
		}
		if(evaporation > maxEvaporation){
			evaporation = maxEvaporation;
		}
		if(dropoff > maxDropoff){
			dropoff = maxDropoff;
		}
		if(chanceOfBestNext > 100){
			chanceOfBestNext = 100;
		}
	}

	public JPanel getPanel(){
		return panel;
	}
}
