/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rescue.base.locator;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.text.DecimalFormat;
import static rescue.base.locator.RescueBaseLocator.gui;
import static rescue.base.locator.RescueBaseLocator.locations;

/**
 *
 * @author Daniel Allen
 */
public class Algorithm {

    /*
     Based on https://devpost.com/software/position-an-implementation-of-weiszfeld-s-algorithm
     */
    public static void Main(String[] args) {

    }
    private static DecimalFormat df = new DecimalFormat("0000.00000");

    public static DoublePoint getOptimalPosition(double[][] points) {
        /*
         THEORY:
         1. Start at the coordinate the user entered to start.
         2. Check the 4 cardinal directions from a point (x,y) for the least distance between all points.
         a) If the distance between all points is at a minimum from the check position, set the minimum distance to the distance calculated, and set the new (x,y) point to the tested point.
         3. Iterate this process multiple times until the move value is less than the accuracy value. This will slowly move towards the median point, getting a more accurate result with each iteration.
         */
        if (points == null) {
            return new DoublePoint(0, 0);
        }
        if (points.length == 0) {
            return new DoublePoint(0, 0);
        }

        //start at 0, 0 for demonstration purposes. This will be replaced with user input later.
        double x = 0;
        double y = 0;

        //define the algorithm settings.
        double accuracy = 1;

        //define the intial step value. This is used while checking the 4 cardinal directions
        double step = 25;

        //declare the minimum distance to the distance of the center of gravity.
        double min = totalDistance(new double[]{x, y}, points);
        double[] directionModifier = new double[]{1, 0, -1, 0, 0, 1, 0, -1};

        //loop while the algorithm is not as accurate as specified
        while (step > accuracy) {
            boolean movedThisIteration = false;
            for (int i = 0; i < points.length; i++) {

                //check each of the cardinal directions
                for (int z = 0; z < 4; z++) {

                    //store the points at the current cardinal direction
                    double nextX = x + step * directionModifier[z];
                    double nextY = y + step * directionModifier[z + 4];

                    //store the total distance of the point
                    double distance = totalDistance(new double[]{nextX, nextY}, points);

                    //if the distance is the minimum in this direction
                    if (distance <= min) {

                        //set this position to the next point to search.
                        x = nextX;
                        y = nextY;

                        //update the GUI to display the current point that the algorithm has.
                        gui.imgP.currentPointCalculation = new DoublePoint(x, y);

                        //set the minimum distance to the best point found.
                        min = distance;

                        //prevent the step size from going down since it found a better position.
                        movedThisIteration = true;

                        //force the GUI to repaint itself immediately. Just using repaint() counts this as a redundent call and will not display it on the GUI.
                        gui.imgP.paintImmediately(0, 0, gui.imgP.getWidth(), gui.imgP.getHeight());
                        try {
                            //make the algorithm wait a little bit before calculating the next point. This helps with debugging and can be deleted or disabled at any time.
                            Thread.sleep(200);
                        } catch (InterruptedException ex) {

                        }
                        System.out.println("Distance from current minimum (" + df.format(x) + ", " + df.format(y) + "): " + df.format(totalDistance(new double[]{x, y}, points)));
                    }

                }
            }
            //if the point didn't move this iteration (if the point was better than the 4 directions around it)
            if (!movedThisIteration) {
                //decrease the step size to increase accuracy.
                step /= 2;
                System.out.println("Spot did not move!\nStep size is now: " + step + "\n");
            }
        }
        gui.imgP.currentPointCalculation = null;
        DoublePoint median = new DoublePoint(x, y);
        System.out.println(median);
        return median;
    }

    /**
     * This is the efficient algorithm. This uses the mean instead of a walking
     * calculation.
     *
     * @param points
     * @return
     *
     */
    public static DoublePoint getMedianEfficiently(double[][] points) {
        double sumX = 0;
        double sumY = 0;
        for (int i = 0; i < points.length; i++) {
            sumX += points[i][0];
            sumY += points[i][1];
        }
        sumX /= points.length;
        sumY /= points.length;
        return new DoublePoint(sumX, sumY);
    }

    public static DoublePoint walkingAlgorithm(double[] startPoint, double[][] points) {
        double x = startPoint[0];
        double y = startPoint[1];
        boolean optimal = false;
        //while x and y are not optimal
        while (!optimal) {
            //loop through the cardinal directions of the point
            for (int i = 0; i < 4; i++) {
                //calculate the total distance of the current point + the step size in the current direction
                double currentDistance = Algorithm.totalDistance(new double[]{x, y}, points);
                //set the new point to the position of the lowest distances
            }
        }
        return null;
    }

    public static BufferedImage graphicalAnalysis(BufferedImage canvas) {
        int falloff = 1;
        double maxDist = Math.pow(canvas.getWidth(),2)+Math.pow(canvas.getHeight(),2);
        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getHeight(); y++) {
                double distanceAtPoint = Algorithm.totalDistance(new double[]{x, y}, RescueBaseLocator.locations);
                float col = (float) Math.sqrt((distanceAtPoint / locations.length) / maxDist);
                Color c = new Color(col, col, col, 0.96f);
                canvas.setRGB(x, y, c.getRGB());
            }
        }
        RescaleOp rescale = new RescaleOp(1f, 2f, null);
        rescale.filter(canvas, canvas);
        return canvas;
    }

    public static double[] centerOfGravity(double[][] points) {
        double sumX = 0, sumY = 0;
        int num = points.length;
        for (int i = 0; i < num; i++) {
            sumX += points[i][0];
            sumY += points[i][1];
        }
        return new double[]{sumX / num, sumY / num};
    }

    public static double totalDistance(double[] point, double[][] points) {
        double sum = 0;
        double x = point[0];
        double y = point[1];
        for (int i = 0; i < points.length; i++) {
            double pointX = points[i][0] - x;
            double pointY = points[i][1] - y;
            double distance = Math.pow(pointX, 2) + Math.pow(pointY, 2);
            sum += distance;
        }
        return sum;
    }
}
