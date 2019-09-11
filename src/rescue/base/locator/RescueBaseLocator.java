/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rescue.base.locator;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Daniel Allen
 */
public class RescueBaseLocator {

    protected static GUI gui = new GUI();
    /**
     * @param args the command line arguments
     */
    static double locations[][];

    public static void main(String[] args) {
        
    }

    public static double[][] readLocations(String filePath, String fileName) throws FileNotFoundException, IOException {
        //make sure the file and directory exist to prevent a FileNotFoundException
        new File(filePath).mkdirs();
        File f = new File(filePath + fileName);
        System.out.println(filePath + fileName);
        if (!f.exists()) {
            f.createNewFile();
            JOptionPane alerter = new JOptionPane();
            alerter.setMessage("Location not found. An empty file has been created at " + filePath + fileName);
            alerter.setMessageType(JOptionPane.INFORMATION_MESSAGE);
            alerter.createDialog("Information").setVisible(true);
        }

        //create a file reader
        FileInputStream fis = new FileInputStream(f);
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedReader read = new BufferedReader(new InputStreamReader(bis));

        String line;
        ArrayList<String> positions = new ArrayList<>();

        int maxLines = 70;
        int curLines = 0;
        //add all lines from the file to an ArrayList
        while ((line = read.readLine()) != null && ((curLines < maxLines) || maxLines == -1)) {
            positions.add(line);
            curLines++;
        }

        //convert the ArrayList of Strings into a 2d array of doubles.
        double[][] coordinates = new double[positions.size()][2];
        for (int i = 0; i < positions.size(); i++) {
            String[] split = positions.get(i).split("[^0-9]");

            //convert the String into a double
            double xCoord = Double.parseDouble(split[0]);
            double yCoord = Double.parseDouble(split[1]);
            coordinates[i][0] = xCoord;
            coordinates[i][1] = yCoord;
        }
        return coordinates;
    }

    public static double[] getClosestPoint(Point p, double[][] locations) {
        //get the index of the closest point
        int closestPointIndex = 0;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < locations.length; i++) {
            double distance = p.distance(locations[i][0], locations[i][1]);
            if (distance < min) {
                min = distance;
                closestPointIndex = i;
            }
        }

        return new double[]{locations[closestPointIndex][0], locations[closestPointIndex][1]};
    }

    public static double[] findBestLocation(double[][] locations) {
        double bestX = 0;
        double bestY = 0;

        return new double[]{bestX, bestY};
    }
}
