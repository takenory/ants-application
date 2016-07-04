
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Entry point for displaying a Ants as an Applet.
 */
@SuppressWarnings("serial")
public class AntsApplet extends JApplet{
	
	public AntsApplet(){
		try {
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					JPanel appletPane = new JPanel(new BorderLayout());
					Ants ants = new Ants();
					AdvancedControlPanel advancedPanel = new AdvancedControlPanel(ants);
					AntsControlPanel antsPanel = new AntsControlPanel(ants, advancedPanel);
					appletPane.add(ants, BorderLayout.CENTER);
					appletPane.add(antsPanel.getPanel(), BorderLayout.EAST);
					appletPane.add(advancedPanel.getPanel(), BorderLayout.SOUTH);
					appletPane.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.BLACK));
					setContentPane(appletPane);
				}
			});
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void init(){}
}
