package pl.polsl.srppproj;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;


public class MainProgram implements Runnable {
	/* GUI */
	private JButton buttonOpen;
	private JFrame frame;
	private JLabel openFileL;
	
	/* Program stuff */
	private ArrayList<City> cities = new ArrayList<City>();
	private int numberOfCities = 0;
	private City magazine;
	private int k;
	
	private class openFile implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			JFileChooser fChooser = new JFileChooser();
			int flag = fChooser.showOpenDialog(frame);
			if(flag == JFileChooser.APPROVE_OPTION) {
				String directory = fChooser.getCurrentDirectory().toString() + "\\" + fChooser.getSelectedFile().getName();
				openFileL.setText(directory);
				
				/* Workflow! */
				MainProgram.this.readValues(directory);
				MainProgram.this.alghoritm();
				MainProgram.this.saveScore(directory);
			}
		}
	}
	
	public void readValues(String directory) {
		try {
			FileReader fReader = new FileReader(directory);
			@SuppressWarnings("resource")
			BufferedReader bReader = new BufferedReader(fReader);
			String line;
			String[] values;
			/* Read k param */
			k = Integer.parseInt(bReader.readLine());
			
			/* Read magazine coords */
			values = bReader.readLine().split(" ");
			magazine = new City(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
			numberOfCities++;
			
			/* Read rest of cities */
			while((line = bReader.readLine()) != null) {
				values = line.split(" ");
				cities.add(new City(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
				numberOfCities++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void alghoritm() {
		// A place for alghoritm
	}
	
	public void saveScore(String directory) {
	}
	
	public BufferedImage rescale(BufferedImage originalImage) {
        BufferedImage resizedImage = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, 150, 150, null);
        g.dispose();
        return resizedImage;
	}
	
	public static void main(String[] args) {
		MainProgram program = new MainProgram();
		SwingUtilities.invokeLater(program);
	}

	@Override
	public void run() {
		buttonOpen = new JButton("Open file");
		buttonOpen.addActionListener(new openFile());
		openFileL = new JLabel("Open file...");
	
		frame = new JFrame("SRPP PROJECT");
		frame.setPreferredSize(new Dimension(450, 250));
		frame.setResizable(false);
		
		JToolBar bar1 = new JToolBar();
		bar1.setFloatable(false);
		bar1.add(buttonOpen);
		bar1.add(openFileL);
		bar1.setMargin(new Insets(5, 5, 5, 5));
		frame.add(bar1, BorderLayout.NORTH);
		
		/*JToolBar bar2 = new JToolBar(JToolBar.VERTICAL);
		bar2.setPreferredSize(new Dimension(150, 350));
		bar2.setFloatable(false);
		bar2.add(img1);
		bar2.add(recFileL);
		bar2.setMargin(new Insets(5, 5, 5, 5));
		bar2.setBorder(BorderFactory.createEtchedBorder());
		frame.add(bar2, BorderLayout.WEST);
		
		JToolBar bar4 = new JToolBar(JToolBar.VERTICAL);
		bar4.setPreferredSize(new Dimension(150, 350));
		bar4.setFloatable(false);
		bar4.add(scoreL);
		bar4.add(img3);
		bar4.add(buttonPlay);
		bar4.setMargin(new Insets(5, 5, 5, 5));
		bar4.setBorder(BorderFactory.createEtchedBorder());
		frame.add(bar4, BorderLayout.EAST);
		
		JToolBar bar3 = new JToolBar(JToolBar.VERTICAL);
		bar3.setPreferredSize(new Dimension(150, 350));
		bar3.setFloatable(false);
		bar3.add(img2);
		bar3.add(notePostL);
		bar3.setMargin(new Insets(5, 5, 5, 5));
		bar3.setBorder(BorderFactory.createEtchedBorder());
		frame.add(bar3, BorderLayout.CENTER);*/
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
