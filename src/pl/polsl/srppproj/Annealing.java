package pl.polsl.srppproj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Annealing extends Thread {
	private ArrayList<City> cities = new ArrayList<City>();
	private int numberOfCities = 0;
	private Magazine magazine = new Magazine();
	private int k;
	private String path;
	private DrawPanel drawPanel;
	
	public Annealing (ArrayList<City> cities, int numberOfCities, Magazine magazine, int k, String path, DrawPanel drawPanel) {
		this.cities = cities;
		this.magazine = magazine;
		this.numberOfCities = numberOfCities;
		this.k = k;
		this.path = path;
		this.drawPanel = drawPanel;
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
	

	
	public ArrayList<ArrayList<Integer>> clonePaths (ArrayList<ArrayList<Integer>> paths) {
		ArrayList<ArrayList<Integer>> cloned = new ArrayList<ArrayList<Integer>>();
		for(ArrayList<Integer> path : paths) {
			cloned.add((ArrayList<Integer>) path.clone());
		}
		return cloned;
	}
	
	
	public ArrayList<ArrayList<Integer>> simulatedAnnealing (ArrayList<ArrayList<Integer>> paths) {
		double Tstart = 10000;
		double T=  Tstart;
		double Tmin = 1;
		double alfa = 0.99999;
		
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
			
			
			//draw precentage progress
			//System.out.println(Math.round(((Tstart-T)/(Tstart-Tmin))*100)+"%");
			T*=alfa;	
		}
		
		if(totalLength(globalMin)<totalLength(paths))
			return globalMin;
		else
			return paths;
	}
	
	public void saveScore(String directory, ArrayList<ArrayList<Integer>> paths) {
		try{
			
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
	
	public void run () {
		ArrayList<ArrayList<Integer>> paths = initialize();
		paths = simulatedAnnealing(paths);		
		System.out.println(totalLength(paths));
		
		File f = new File(path+"_output");
		if(f.exists())
		{
			try {
				FileReader fReader = new FileReader(path+"_output");
				@SuppressWarnings("resource")
				BufferedReader bReader = new BufferedReader(fReader);
				Double lastScore = Double.parseDouble(bReader.readLine());
				if (lastScore>totalLength(paths)) {
					drawPanel.setPaths(paths);
					drawPanel.repaint();
					saveScore(path, paths);
				}
			}
			catch (Exception e) {
				System.out.println("Error: "+e.getMessage());
			}
		} else {
			drawPanel.setPaths(paths);
			drawPanel.repaint();
			saveScore(path, paths);
		}
		
		System.out.println("Koniec w�tku.");
		
	}
	
}
