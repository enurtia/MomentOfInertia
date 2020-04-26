package momentofinertia;

import javafx.geometry.Point3D;
import javafx.scene.shape.Cylinder;

public class Line extends Cylinder
{
    private Point3D pt1 = new Point3D(0, 0, 0);
    private Point3D pt2 = new Point3D(0, 0, 0);
    
    public Line()
    {
        setLine(new Point3D(0,0,0), new Point3D(0,0,0));
    }
    
    public Line(Line line)
    {
        setLine(line.getStart(), line.getEnd());
        this.setRadius(line.getRadius());
    }
    
    public Line(Point3D start, Point3D end)
    {
        setLine(start, end);
    }
    
    public Point3D getStart()
    {
        return pt1;
    }
    
    public Point3D getEnd()
    {
        return pt2;
    }
    
    public double getLength()
    {
        return pt1.distance(pt2);
    }
    
    public void setLine(Point3D start, Point3D end)
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
        
        pt1 = start;
        pt2 = end;
    }
}
