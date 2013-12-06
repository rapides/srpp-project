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
import java.util.Arrays;
import java.util.Collections;

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
	private DrawPanel drawPanel;
	
	/* Program stuff */
	private ArrayList<City> cities = new ArrayList<City>();
	private int numberOfCities = 0;
	private Magazine magazine = new Magazine();
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
				/* Test: draw one path */
				//pathes.add(new Path(cities.get(0), cities.get(1)));
				drawPanel.repaint();
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
			magazine.city = new City(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
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
	
	public ArrayList<ArrayList<Integer>> initialize() {
		// create list of paths
		ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
		
		//create numbers 1....number of Cities
		Integer[] arr = new Integer[numberOfCities-1];
		paths.clear();
		for (int i = 0; i < numberOfCities-1; i++) {
			arr[i]=i;
		}
		
		//shuffle numbers
		Collections.shuffle(Arrays.asList(arr));
		
		//create paths
		for (int i = 0;i<numberOfCities-1; i+=k) {
			ArrayList<Integer> path = new ArrayList<Integer>();
			for (int j = 0; j < k ; j++) {
				if ((i+j)>numberOfCities)
					break;
				path.add(arr[i+j]);
			}
			paths.add(path);
		}
		return paths;
	}
	
	public void alghoritm() {
		ArrayList<ArrayList<Integer>> paths = initialize();
		drawPanel.setPaths(paths);
		drawPanel.repaint();
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
		ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
		
		buttonOpen = new JButton("Open file");
		buttonOpen.addActionListener(new openFile());
		openFileL = new JLabel("Open file...");
	
		frame = new JFrame("SRPP PROJECT");
		frame.setPreferredSize(new Dimension(510, 590));
		frame.setResizable(false);
		
		JToolBar bar1 = new JToolBar();
		bar1.setFloatable(false);
		bar1.add(buttonOpen);
		bar1.add(openFileL);
		bar1.setMargin(new Insets(5, 5, 5, 5));
		frame.add(bar1, BorderLayout.NORTH);
		
		drawPanel = new DrawPanel(cities, paths ,magazine);
		frame.add(drawPanel, BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
