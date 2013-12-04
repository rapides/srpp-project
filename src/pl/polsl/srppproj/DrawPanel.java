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
    private ArrayList<Path> pathes;
    private Magazine magazine;

    public DrawPanel(ArrayList<City> cities, ArrayList<Path> pathes, Magazine magazine) {
    	this.cities = cities;
    	this.pathes = pathes;
        dims = new Dimension(500, 500);
        this.magazine = magazine;
        setPreferredSize(dims);
        setBackground(Color.WHITE);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if(!cities.isEmpty() && magazine.city != null) {
	        g2d.setColor(Color.RED);
	        g2d.drawOval(magazine.city.x-2, magazine.city.y-2, 5, 5);
	        g2d.setColor(Color.BLACK);
	        for(City c : cities) {
	        	g2d.drawOval(c.x-1, c.y-1, 3, 3);
	        }
	        g2d.setColor(Color.GRAY);
	        for(Path p : pathes) {
	        	g2d.drawLine(p.start.x, p.start.y, p.finish.x, p.finish.y);
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
