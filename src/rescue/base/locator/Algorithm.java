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

    //whether to use Math.hypot instead of just adding the squares. I believe the Math.hypot formula is correct, but it gives me a different answer from Mr. Payne
    private static boolean useProperAlgorithm = false;

    public static void Main(String[] args) {

    }
    private static DecimalFormat df = new DecimalFormat("0000.00000");

    public static BufferedImage graphicalAnalysis(BufferedImage canvas) {
        int falloff = 1;
        double maxDist;
        maxDist = useProperAlgorithm ? Math.hypot(canvas.getWidth(), canvas.getHeight()) : Math.pow(canvas.getWidth(), 2) + Math.pow(canvas.getHeight(), 2);
        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getHeight(); y++) {
                double distanceAtPoint;
                distanceAtPoint = Algorithm.totalDistance(new double[]{x, y}, RescueBaseLocator.locations);
                float col = (float) Math.sqrt((distanceAtPoint / locations.length) / maxDist);
                Color c = new Color(col, col, col, 0.9f);
                canvas.setRGB(x, y, c.getRGB());
            }
        }
        RescaleOp rescale = new RescaleOp(1f, 2f, null);
        rescale.filter(canvas, canvas);
        return canvas;
    }

    public static double totalDistance(double[] point, double[][] points) {
        double sum = 0;
        double x = point[0];
        double y = point[1];
        for (int i = 0; i < points.length; i++) {
            double pointX = points[i][0] - x;
            double pointY = points[i][1] - y;
            double distance;
            distance = useProperAlgorithm ? Math.hypot(pointX, pointY) : Math.pow(pointX, 2) + Math.pow(pointY, 2);
            sum += distance;
        }
        return sum;
    }

    public static int countMatches(String str, char ch){
        if(str == null || str.isEmpty())
            return 0;
        int count = 0;
        for(char c : str.toCharArray()){
            if(c == ch)
                count++;
        }
        return count;
    }
}
