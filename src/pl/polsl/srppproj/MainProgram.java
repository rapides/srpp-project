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
import java.io.IOException;
import java.io.PrintStream;
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
				drawPanel.repaint();
				ArrayList<ArrayList<Integer>> paths = MainProgram.this.alghoritm();
				MainProgram.this.saveScore(directory, paths);
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
	
	public ArrayList<ArrayList<Integer>> initialize() {
		// create list of paths
		ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
		
		System.out.println(numberOfCities);
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
				if ((i+j)==numberOfCities-1)
					break;
				path.add(arr[i+j]);
			}
			paths.add(path);
		}
		return paths;
	}
	
	public double lengthBeetween (City start, City end) {
		return Math.sqrt(Math.pow(start.x-end.x,2) + Math.pow(start.y -end.y, 2));
	}
	
	public double totalLength (ArrayList<ArrayList<Integer>> paths) {
		double sum = 0;
		for(ArrayList<Integer> path : paths) {
			//first and last point
			sum += lengthBeetween(magazine.city, cities.get(path.get(0)));
			sum += lengthBeetween(magazine.city, cities.get(path.get(path.size()-1)));
			
			for(int i=0; i< (path.size()-1); i++) {
				sum+= lengthBeetween(cities.get(path.get(i)), cities.get(path.get(i+1)));
			}
		}
		return sum;
	}
	
	public ArrayList<ArrayList<Integer>> alghoritm() {
		ArrayList<ArrayList<Integer>> paths = initialize();
		System.out.println(totalLength(paths));
		paths = simulatedAnnealing(paths);		
		System.out.println(totalLength(paths));
		drawPanel.setPaths(paths);
		drawPanel.repaint();
		return paths;
	}
	
	public ArrayList<ArrayList<Integer>> clonePaths (ArrayList<ArrayList<Integer>> paths) {
		ArrayList<ArrayList<Integer>> cloned = new ArrayList<ArrayList<Integer>>();
		for(ArrayList<Integer> path : paths) {
			cloned.add((ArrayList<Integer>) path.clone());
		}
		return cloned;
	}
	
	
	public ArrayList<ArrayList<Integer>> simulatedAnnealing (ArrayList<ArrayList<Integer>> paths) {
		int i = 0;
		double Tstart = 100;
		double T=  Tstart;
		double Tmin = 1;
		double alfa = 0.999999;
		
		ArrayList<ArrayList<Integer>> temp;
		ArrayList<ArrayList<Integer>> globalMin = clonePaths(paths);
		
		while(T>Tmin) {
			temp = clonePaths(paths);
			
			int first_path = (int) (Math.random()*temp.size()-1);
			int second_path = (int) (Math.random()*temp.size()-1);
			
			int index_in_first = (int) (Math.random()*temp.get(first_path).size()-1);
			int index_in_second = (int) (Math.random()*temp.get(second_path).size()-1);
			
			Integer tempInteger = temp.get(first_path).get(index_in_first);
			temp.get(first_path).set(index_in_first, temp.get(second_path).get(index_in_second));
			temp.get(second_path).set(index_in_second, tempInteger);
			
			double length1 = totalLength(temp);
			double length2 = totalLength(paths);
				
			if(length1<length2) {
				paths = temp;
				
			} else if (Math.random()<Math.exp((-(length1 - length2))/T)) {
				globalMin = clonePaths(paths);
				paths = temp;
			}
			
			
			
			System.out.println(Math.round(((Tstart-T)/(Tstart-Tmin))*100)+"%");
			T*=alfa;	
		}
		
		return paths;
	}
	
	public void saveScore(String directory, ArrayList<ArrayList<Integer>> paths) {
		try{
			File f = new File(directory+"_output");
			if(f.exists())
			{
				System.out.println("Plik istnieje");
				FileReader fReader = new FileReader(directory+"_output");
				@SuppressWarnings("resource")
				BufferedReader bReader = new BufferedReader(fReader);
				Double lastScore = Double.parseDouble(bReader.readLine());
				if (lastScore<=totalLength(paths)) {
					System.out.println("Wynik gorszy... :(");
					return;
				}
			}
			
			System.out.println("Write to: "+directory+"_output");
			PrintStream ps = new PrintStream(directory+"_output");
			ps.println(totalLength(paths));
			ps.println(paths.size());
			
			for(ArrayList<Integer> path : paths ) {
				ps.print("0 ");
				for (int i = 0; i < path.size(); i++) {
					ps.print(path.get(i)+" ");
				}
				ps.print("0");
				ps.println();
			}
			
	
			ps.close();
		}catch (Exception e) {
			System.out.println("Error: "+e.getMessage());
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
