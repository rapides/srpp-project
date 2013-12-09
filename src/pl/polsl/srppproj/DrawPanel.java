package pl.polsl.srppproj;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;


/**
 * 	Display description:
 * 
 *	City - black circle 
 * 	Magazine - red circle
 *	Path - grey line
 */
public class DrawPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Dimension dims;
    private ArrayList<City> cities;
    ArrayList<ArrayList<Integer>> paths;
    private Magazine magazine;

    public DrawPanel(ArrayList<City> cities, ArrayList<ArrayList<Integer>> paths ,Magazine magazine) {
    	this.cities = cities;
    	this.paths = paths;
        dims = new Dimension(500, 500);
        this.magazine = magazine;
        setPreferredSize(dims);
        setBackground(Color.WHITE);
    }
    
    public void setPaths(ArrayList<ArrayList<Integer>> paths) {
    	this.paths = paths;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if(!cities.isEmpty() && magazine.city != null) {
	        g2d.setColor(Color.RED);
	        g2d.fillOval(magazine.city.x-3, magazine.city.y-3, 7, 7);
	        g2d.setColor(Color.BLACK);
	        for(City c : cities) {
	        	g2d.fillOval(c.x-2, c.y-2, 5, 5);
	        }
	        
	        if(!paths.isEmpty())
	        for(ArrayList<Integer> path : paths) {
	        	g2d.setColor(Color.GRAY);
	        	
	        	//first line from magazine
	        	g2d.drawLine(magazine.city.x, magazine.city.y, cities.get(path.get(0)).x, cities.get(path.get(0)).y );
	        	//last line from magazine
	        	g2d.drawLine(magazine.city.x, magazine.city.y, cities.get( path.get(path.size()-1) ).x, cities.get( path.get(path.size()-1) ).y );
	        	
	        	//draw lines beetween
	        	g2d.setColor(Color.BLUE);
	        	
	        	for (int i = 0; i < (path.size()-1); i++) {
	        		g2d.drawLine(cities.get(path.get(i)).x, cities.get(path.get(i)).y, cities.get(path.get(i+1)).x, cities.get(path.get(i+1)).y );
				}
	        }
	        
	        g2d.setColor(Color.BLACK);
        } else {
        	g2d.drawString("Open the file to run alghoritm.", 180, 250);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return dims;
    }
}
