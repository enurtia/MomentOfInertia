package momentofinertia;

import javafx.geometry.Point3D;
import javafx.scene.shape.Cylinder;

public class Line extends Cylinder
{
    public Line(Point3D start, Point3D end)
    {
        this.setRadius(0.1);
        this.setHeight(start.distance(end));
        
        Point3D mid = start.midpoint(end);
        this.setTranslateX(mid.getX());
        this.setTranslateY(mid.getY());
        this.setTranslateZ(mid.getZ());
        
        Point3D vect = end.subtract(start).normalize();
        Point3D yAxis = new Point3D(0,1,0);
        
        this.setRotationAxis(yAxis.crossProduct(vect));
        this.setRotate(yAxis.angle(vect));
        
    }
}
