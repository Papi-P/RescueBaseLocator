/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rescue.base.locator;

import java.awt.Point;

/**
 *
 * @author Daniel Allen
 */
public class DoublePoint extends Point{
    double xDouble;
    double yDouble;
    int x;
    int y;
    DoublePoint(double x, double y){
        this.xDouble = x;
        this.yDouble = y;
        this.x = (int)x;
        this.y = (int)y;
    }

    @Override
    public String toString(){
        return "("+this.xDouble+", "+this.yDouble+")";
    }

    public Point toPoint(){
        return new Point(this.x,this.y);
    }

    public double distanceFrom(DoublePoint dp){
        double distanceX;
        double distanceY;
        distanceX = Math.sqrt(Math.pow(x, 2)+Math.pow(dp.x, 2));
        distanceY = Math.sqrt(Math.pow(y, 2)+Math.pow(dp.y, 2));
        return Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
    }
}
