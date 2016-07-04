import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Entry point for displaying Ants as an Application.
 * Use this for both Java web start and executable jar.
 */

//アリの行動シュミレーション
public class AntsApplication {
	
	public static void main(String [] args){
		
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				
				//グリッドを作成
				JFrame frame = new JFrame ("Ants");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				//グリッドの宣言
				final Ants ants = new Ants();
				
				//グリッド表示
				frame.add(ants);
				
				AdvancedControlPanel advancedPanel = new AdvancedControlPanel(ants);
				
				final AntsControlPanel antsPanel = new AntsControlPanel(ants, advancedPanel);
				
				//コントロール部
				frame.add(antsPanel.getPanel(), BorderLayout.EAST);
				
				//下部の設定部
				frame.add(advancedPanel.getPanel(), BorderLayout.SOUTH);
				
				//ウインドウ初期サイズ
				frame.setSize(600, 600);
				
				frame.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
				
				//ウインドウの表示
				frame.setVisible(true);
				
				}
		});

	}
}
