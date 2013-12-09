package pl.polsl.srppproj;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

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
			fChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int flag = fChooser.showOpenDialog(frame);
			if(flag == JFileChooser.APPROVE_OPTION) {
				final String directory = fChooser.getCurrentDirectory().toString() + "\\" + fChooser.getSelectedFile().getName();
				buttonOpen.setEnabled(false);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						File dir = new File(directory);
						if(dir.isDirectory()) {
							String[] fileNames = dir.list(new FilenameFilter() {
								@Override
								public boolean accept(File dir, String name) {
									return new File(dir, name).isFile();
								}
							});
							openFileL.setText(directory + " (" + fileNames.length + " files)");
							
							for(int x = 0; x < 50; x++) {
								for(int i = 0; i < fileNames.length; i++) {
									try {
										String path = directory + "\\" + fileNames[i];
										System.out.println("Started for file: " + fileNames[i]);
										openFileL.setText(path + "  (file: " + (i+1) + "/" + fileNames.length + " | records: " + Annealing.getNumberOfRecords() + ")");
										MainProgram.this.readValues(path);
										
										ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
										drawPanel.setPaths(paths);
										drawPanel.repaint();
										
										
										Annealing[] threads = new Annealing[Runtime.getRuntime().availableProcessors()-1];
										
										for(int j=0; j < threads.length; j++) {
										  threads[j] = new Annealing(cities,numberOfCities,magazine,k,path, drawPanel);
										  threads[j].start();
										}
										for(Thread t : threads) t.join();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
							System.out.println("Done!");
						} else if(dir.isFile()){
							try {
								openFileL.setText(directory);
			                    MainProgram.this.readValues(directory);
			                    ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
			                    drawPanel.setPaths(paths);
			                    drawPanel.repaint();
			                    
			                    Annealing[] threads = new Annealing[Runtime.getRuntime().availableProcessors()-1];
								
								for(int i=0; i < threads.length; i++) {
								  threads[i] = new Annealing(cities,numberOfCities,magazine,k,directory, drawPanel);
								  threads[i].start();
								}
								for(Thread t : threads) t.join();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						buttonOpen.setEnabled(true);
						Annealing.resetNumberOfRecords();
					}
				}).start();
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
			numberOfCities=0;
			magazine.city = new City(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
			numberOfCities++;
			
			cities.clear();
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
