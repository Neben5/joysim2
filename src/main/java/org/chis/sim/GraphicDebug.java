package org.chis.sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.chis.sim.GraphicDebug.Serie.Point;
import org.chis.sim.Util.Vector2D;

public class GraphicDebug extends JPanel{
    private static final long serialVersionUID = -3303992246381800667L; //idk why it gives a warning if this isn't here

    // static functions and variables, affects all window graphs
    public static ArrayList<GraphicDebug> graphicDebugs = new ArrayList<GraphicDebug>();

    public static void paintAll(){
        for(GraphicDebug graphicDebug : graphicDebugs){
            graphicDebug.repaint();
        }
    }

    public static void turnOnAll(){
        for(GraphicDebug graphicDebug : graphicDebugs){
            for(Serie serie : graphicDebug.series){
                serie.on = true;
            }
        }
    }

    public static void resetAll(){
        for(GraphicDebug graphicDebug : graphicDebugs){
            graphicDebug.reset();
        }
    }


    // Instance functions and variables, for each window graph separately
    JFrame frame;
    Dimension frameSize = new Dimension(300, 300);
    boolean isGraph;

    ArrayList<Serie> series = new ArrayList<Serie>();

    ArrayList<String> prints = new ArrayList<String>();

    public GraphicDebug(String name){ 
        isGraph = false;
        frame = new JFrame(name);
		frame.add(this);
        frame.setSize(frameSize);
        frame.setLocation((int) (GraphicSim.screenWidth - Util.posModulo((frameSize.getWidth() * graphicDebugs.size()), GraphicSim.screenWidth)), 0);
		frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        graphicDebugs.add(this);
        System.out.println("New GraphicDebug: " + name);
    }

    public GraphicDebug(String name, Serie[] series_input, int maxPoints_input){ // same but allows creating series outside and no need to call addSerie()
        isGraph = true;
        for(Serie serie_input : series_input){
            series.add(serie_input);
        }
        frame = new JFrame(name);
		frame.add(this);
        frame.setSize(frameSize);
        frame.setLocation((int) (GraphicSim.screenWidth - Util.posModulo((frameSize.getWidth() * (graphicDebugs.size() + 1)), GraphicSim.screenWidth)), 0);
		frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        calcScales();

        for(Serie serie : series){
            serie.maxLength = maxPoints_input;
        }
        
        graphicDebugs.add(this);

        System.out.println("New GraphicDebug: " + name);
    }

    public void addSerie(){
        series.add(new Serie());
    }

    public void addSerie(Color color){
        series.add(new Serie(color));
    }

    public void addSerie(int lineWidth){
        series.add(new Serie(lineWidth));
    }

    public void addSerie(Color color, int lineWidth){
        series.add(new Serie(color, lineWidth));
    }

    
    
    int leftMargin = 20;
    int rightMargin = 20;
    int bottomMargin = 20;
    int topMargin = 20;

    double xMin, xMax, yMin, yMax;

    double plotWidth, plotHeight;
    double xAxis, yAxis;
    double xScale, yScale;

    int xMinPixel, xMaxPixel, yMinPixel, yMaxPixel, xAxisPixel, yAxisPixel; //actual pixel locations, after applying margins

    @Override
    public void paint(Graphics g) { //run by each instance of GraphicDebug (each window graph)
        super.paint(g);

        if(isGraph){
            xMin = Integer.MAX_VALUE;
            xMax = Integer.MIN_VALUE;
            yMin = Integer.MAX_VALUE;
            yMax = Integer.MIN_VALUE;

            for(Serie serie : series){
                if(serie.on){
                    synchronized(serie.points){ //synchonized to avoid concurrent exceptions with usercode thread
                        for(Point point : serie.points){
                            if(point.x < xMin) {
                                xMin = point.x;
                            }else if(point.x > xMax){
                                xMax = point.x;
                            }

                            if(point.y < yMin) {
                                yMin = point.y;
                            }else if(point.y > yMax){
                                yMax = point.y;
                            }
                        }
                    }

                }
            }

            xMin -= 0.1;
            xMax += 0.1;
            yMin -= 0.1;
            yMax += 0.1;

            calcScales();

            //draw axes
            g.drawLine(xMinPixel, xAxisPixel, xMaxPixel, xAxisPixel);
            g.drawLine(yAxisPixel, yMinPixel, yAxisPixel, yMaxPixel);

            //label axes
            g.setColor(Color.BLACK);
            NumberFormat numFormat = new DecimalFormat("#.###E0");
            g.drawString(numFormat.format(xMin), xMinPixel - 20, frame.getContentPane().getHeight()/2);
            g.drawString(numFormat.format(xMax), xMaxPixel - 40, frame.getContentPane().getHeight()/2);
            g.drawString(numFormat.format(yMin), frame.getContentPane().getWidth()/2 - 20, yMinPixel);
            g.drawString(numFormat.format(yMax), frame.getContentPane().getWidth()/2 - 20, yMaxPixel);

            for(Serie serie : series){ //draw each scatterplot series in the graph
                if(serie.on){
                    g.setColor(serie.color);
                    synchronized(serie.points){
                        for(Point point : serie.points){ //draw all the points in the serie so far
                            int displayX = (int) (point.x * xScale + yAxis + leftMargin);
                            int displayY = (int) (frame.getContentPane().getHeight() - (point.y * yScale + xAxis + bottomMargin));

                            g.fillOval(displayX, displayY, serie.lineWidth, serie.lineWidth);
                        }
                    }
                    
                }
            }

        } else { //if not graph, do text stuff

            int lineNumber = 1;
            for(String str : prints){
                g.drawString(str, 20, 20 * lineNumber);
                lineNumber++;
            }
            

        }

    }

    public void addText(String text, double number){
        for(int i = 0; i < prints.size(); i++){
            if(prints.get(i).startsWith(text)){
                prints.set(i, text + ": " + number);
                return;
            }
        }

        prints.add(text + ": " + number);
    }

    void calcScales(){
        plotWidth = frame.getContentPane().getWidth() - leftMargin - rightMargin;
        plotHeight = frame.getContentPane().getHeight() - bottomMargin - topMargin;
        
        yAxis = plotWidth * (-xMin / (xMax - xMin));
        xScale = Math.abs(yAxis / xMin);

        xAxis = plotHeight * (-yMin / (yMax - yMin));
        yScale = Math.abs(xAxis / yMin);

        xMinPixel = leftMargin;
        xMaxPixel = frame.getContentPane().getWidth() - rightMargin;
        yMinPixel = frame.getContentPane().getHeight() - bottomMargin;
        yMaxPixel = topMargin;
        yAxisPixel = (int) (leftMargin + yAxis); //an x coordinate
        xAxisPixel = (int)(frame.getContentPane().getHeight() - (bottomMargin + xAxis)); //a y coordinate

    }

    public void reset(){
        for(Serie serie: series){
            serie.points.clear();
        }
        xMin = Integer.MAX_VALUE;
        xMax = Integer.MIN_VALUE;
        yMin = Integer.MAX_VALUE;
        yMax = Integer.MIN_VALUE;
    }


    public static class Serie{ //series but singular :/
        Color color = Color.BLACK;
        int lineWidth = 1;
        int maxLength = 300;
        volatile ArrayList<Point> points = new ArrayList<Point>();
        volatile Boolean on = false; //set to true once UserCode initializes

        public Serie(){
        }

        public Serie(Color color_input){
            color = color_input;
        }

        public Serie(int lineWidth_input){
            lineWidth = lineWidth_input;
        }

        public Serie(Color color_input, int lineWidth_input){
            color = color_input;
            lineWidth = lineWidth_input;
        }

        public void addPoint(double x, double y){
            synchronized(points){ //synchronized so usercode thread can call this while painting and avoid concurrentModificationException
                points.add(new Point(x, y));
                if(points.size() > maxLength){
                    points.remove(0);
                }
            }
            
        }

        public void addPoint(Vector2D vector2d){
            synchronized(points){ //synchronized so usercode thread can call this while painting and avoid concurrentModificationException
                points.add(new Point(vector2d.x, vector2d.y));
                if(points.size() > maxLength){
                    points.remove(0);
                }
            }
            
        }

        public class Point{ // quick alternative to java.awt.Point which can only do ints
            double x, y;
            public Point(double x, double y){
                this.x = x;
                this.y = y;
            }
        }
    }
}