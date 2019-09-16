/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rescue.base.locator;

import java.util.concurrent.Callable;
import static rescue.base.locator.Algorithm.totalDistance;
import static rescue.base.locator.RescueBaseLocator.gui;

/**
 *
 * @author 340900828
 */
public class FindOptimalLocationRunnable implements Callable<DoublePoint> {

    private double[][] points;
    private double[] start;

    public FindOptimalLocationRunnable(double[][] points) {
        this(points, new double[]{0, 0},0);
    };
    public FindOptimalLocationRunnable(double[][] points, double[] start, long waitTime) {
        if (start == null) {
            throw new NullPointerException("The start location cannot be null!");
        }
        if (start.length < 2) {
            throw new IllegalArgumentException("The start location must have 2 points!");
        }
        cancelled = false;
        this.points = points;
        this.start = start;
        setWaitTime(waitTime);
    };

    //keep track of if the task was cancelled
    public static volatile boolean cancelled = false;

    //handle the delay between calculations. The timer has been modified to not implement the time that the thread was sleeping.
    private long waitTime = 0;

    public void setWaitTime(long waitTime) {
        if (waitTime < 0) {
            throw new IllegalArgumentException("The wait time cannot be negative!");
        }
        this.waitTime = waitTime;
    }

    public long getWaitTime() {
        return this.waitTime;
    }

    @Override
    public DoublePoint call() throws Exception {
        synchronized (this) {
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

            long startMillis = System.currentTimeMillis();

            //start at 0, 0 for demonstration purposes. This will be replaced with user input later.
            double x = start[0];
            double y = start[1];

            //define the algorithm settings.
            double accuracy = 1;

            //define the intial step value. This is used while checking the 4 cardinal directions
            double step = 250;

            //declare the minimum distance to the distance of the center of gravity.
            double min = totalDistance(new double[]{x, y}, points);
            double[] directionModifier = new double[]{1, 0, -1, 0, 0, 1, 0, -1};

            //keep track of how many steps are taken
            int numSteps = 1;

            //loop while the algorithm is not as accurate as specified
            while (step > accuracy && !cancelled) {
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

                            double prevX = x;
                            double prevY = y;

                            //set this position to the next point to search.
                            x = nextX;
                            y = nextY;

                            //update the GUI to display the current point that the algorithm has.
                            gui.imgP.currentPointCalculation = new DoublePoint(x, y);

                            //set the minimum distance to the best point found.
                            min = distance;

                            //prevent the step size from going down since it found a better position.
                            movedThisIteration = true;

                            //force the GUI to repaint itself immediately. Just using repaint() counts this as a redundent call and will not display it on the GUI. For speed optimization, only repaint the old and new points.
                            //gui.imgP.paintImmediately((int)prevX-10, (int)prevY-10, (int)prevX+10, (int)prevY+10);
                            //gui.imgP.paintImmediately((int)x-10, (int)y-10, (int)x+10, (int)y+10);
                            gui.imgP.paintImmediately(0, 0, gui.imgP.getWidth(), gui.imgP.getHeight());
                            try {
                                //make the algorithm wait a little bit before calculating the next point. This helps with debugging and can be deleted or disabled at any time.
                                Thread.sleep(waitTime);
                            } catch (InterruptedException ex) {

                            }
                            numSteps++;
                            //System.out.println("Distance from current minimum (" + df.format(x) + ", " + df.format(y) + "): " + df.format(totalDistance(new double[]{x, y}, points)));
                        }
                    }
                }
                //if the point didn't move this iteration (if the point was better than the 4 directions around it)
                if (!movedThisIteration) {
                    //decrease the step size to increase accuracy.
                    step /= 2;
                    //System.out.println("Spot did not move!\nStep size is now: " + step + "\n");
                }
            }
            long endMillis = System.currentTimeMillis();

            //output the calculation time and steps. This removes the time the thread was sleeping from the timer.
            System.out.println("Found in " + ((endMillis - startMillis) - (numSteps - 1) * waitTime) + " milliseconds with " + numSteps + " steps.");

            //remove the currentPoint from the GUI since it no longer needed and would just slow down the program.
            gui.imgP.currentPointCalculation = null;
            DoublePoint median = new DoublePoint(x, y);

            System.out.println(median);
            return median;
        }
    }
}
