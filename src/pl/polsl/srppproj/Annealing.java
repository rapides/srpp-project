package pl.polsl.srppproj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

public class Annealing extends Thread {
	private ArrayList<City> cities = new ArrayList<City>();
	private int numberOfCities = 0;
	private Magazine magazine = new Magazine();
	private int k;
	private String path;
	private DrawPanel drawPanel;
	private static int records = 0;
	
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
		for (int i = 0;i<numberOfCities-1;) {
			ArrayList<Integer> path = new ArrayList<Integer>();
			int rand_k = k;
			if(Math.random()<0.02){
				//rand_k = k - (int) (Math.random()*(numberOfCities*0.01));
			}
			
			//uncoment below if dont want to make different length of paths
			rand_k = k;
			
			//System.out.println("Rand k:" + rand_k);
			for (int j = 0; j < rand_k ; j++) {
				if (i==numberOfCities-1)
					break;
				path.add(arr[i]);
				i++;
			}
			paths.add(path);
		}
		return paths;
	}
	
	public double pathLength (ArrayList<Integer> path) {
		double sum=0;
		sum += lengthBeetween(magazine.city, cities.get(path.get(0)));
		sum += lengthBeetween(magazine.city, cities.get(path.get(path.size()-1)));
		
		for(int i=0; i< (path.size()-1); i++) {
			sum+= lengthBeetween(cities.get(path.get(i)), cities.get(path.get(i+1)));
		}
		return sum;
	}
	
	public double lengthBeetween (City start, City end) {
		return Math.sqrt(Math.pow(start.x-end.x,2) + Math.pow(start.y -end.y, 2));
	}
	
	public double totalLength (ArrayList<ArrayList<Integer>> paths) {
		double sum = 0;
		for(ArrayList<Integer> path : paths) {
			//first and last point
			sum += pathLength(path);
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
	
	public ArrayList<ArrayList<Integer>> exchange_beetween_paths (ArrayList<ArrayList<Integer>> paths, int path1, int path2) {
		int index_in_first = (int) (Math.random()*paths.get(path1).size()-1);
		int index_in_second = (int) (Math.random()*paths.get(path2).size()-1);
		
		Integer tempInteger = paths.get(path1).get(index_in_first);
		paths.get(path1).set(index_in_first, paths.get(path2).get(index_in_second));
		paths.get(path2).set(index_in_second, tempInteger);

		return paths;
	}
	
	public ArrayList<Integer> the_best_path(ArrayList<Integer> path) {
		ArrayList<Integer> globalMin = (ArrayList<Integer>) path.clone();
		
		for(int i =0; i< 10; i++) {
			//Collections.shuffle(path);
			int index_first = (int) (Math.random() * (path.size() -1));
			int index_second = (int) (Math.random() * (path.size() -1));
			
			Integer tempInteger = path.get(index_first);
			path.set(index_first, path.get(index_second));
			path.set(index_second, tempInteger);

			if(pathLength(path)<pathLength(globalMin))
				globalMin = (ArrayList<Integer>) path.clone();
			
		}
		return globalMin;
	}
	
	
	
	public ArrayList<ArrayList<Integer>> change_order_in_path (ArrayList<ArrayList<Integer>> paths, int path) {
		
		int index_first = (int) (Math.random()*paths.get(path).size()-1);
		int index_second = (int) (Math.random()*paths.get(path).size()-1);
		
		Integer tempInteger = paths.get(path).get(index_first);
		paths.get(path).set(index_first, paths.get(path).get(index_second));
		paths.get(path).set(index_second, tempInteger);
		
		return paths;
	}
	
	
	public ArrayList<ArrayList<Integer>> simulatedAnnealing (ArrayList<ArrayList<Integer>> paths) {
		double Tstart = 15000;
		double T=  Tstart;
		double Tmin = 5;
		double alfa = 0.999999;
		
		ArrayList<ArrayList<Integer>> globalMin = clonePaths(paths);
		int i=0;
		while(T>Tmin) {			
			i++;
			int first_path = (int) (Math.random()*paths.size()-1);
			int second_path = (int) (Math.random()*paths.size()-1);
			
			
			ArrayList<ArrayList<Integer>> exchange_beetween_paths = exchange_beetween_paths (clonePaths(paths), first_path, second_path);
			
			
			double before_length= totalLength(paths);
			double best_proposal = totalLength(exchange_beetween_paths);
			double exp = Math.exp((-(best_proposal - before_length))/T);
			double dif = totalLength(exchange_beetween_paths) - before_length;
			

			if(best_proposal<before_length) {
				paths = exchange_beetween_paths;
				
			} else if (dif!=0 && Math.random()<exp) {
				if (totalLength(globalMin)>totalLength(paths))
				{
					globalMin = clonePaths(paths);
				}
				
				paths=exchange_beetween_paths;
			}
			
			T*=alfa;
			
		}
		
		for (int j=0; j<paths.size(); j++) {
			ArrayList<Integer> path_temp = paths.get(j);
			path_temp = the_best_path(path_temp);
			paths.set(j, path_temp);
		}
		
		
		
		if(totalLength(globalMin)<totalLength(paths))
			return globalMin;
		else
			return paths;
	}
	
	public void saveScore(String directory, ArrayList<ArrayList<Integer>> paths) {
		try{
			String saveDir = toSaveDirectory(directory);
			System.out.println("Write to: "+saveDir);
			PrintStream ps = new PrintStream(saveDir);
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
	
	private String toSaveDirectory(String readDirectory) {
		String[] parts = readDirectory.split(Pattern.quote(File.separator));
		String saveDir = new String();
		for(int i = 0; i < parts.length-1; i++) {
			saveDir += parts[i] + "\\";
		}
		saveDir += "scores";
		(new File(saveDir)).mkdir();
		saveDir += "\\" + parts[parts.length-1] + "_output";
		return saveDir;
	}
	
	public static int getNumberOfRecords() {
		return records;
	}
	
	public static void resetNumberOfRecords() {
		records = 0;
	}
	
	public void run () {
		ArrayList<ArrayList<Integer>> paths = initialize();
		paths = simulatedAnnealing(paths);		
		System.out.println(totalLength(paths));
		
		String savePath = toSaveDirectory(path);
		File f = new File(savePath);
		if(f.exists())
		{
			try {
				FileReader fReader = new FileReader(f);
				@SuppressWarnings("resource")
				BufferedReader bReader = new BufferedReader(fReader);
				Double lastScore = Double.parseDouble(bReader.readLine());
				if (lastScore>totalLength(paths)) {
					drawPanel.setPaths(paths);
					drawPanel.repaint();
					saveScore(path, paths);
					records++;
				}
			}
			catch (Exception e) {
				System.out.println("Error: "+e.getMessage());
			}
		} else {
			drawPanel.setPaths(paths);
			drawPanel.repaint();
			//(new File(savePath)).mkdirs();
			saveScore(path, paths);
		}
		
		System.out.println("Koniec watku.");
	}
}
