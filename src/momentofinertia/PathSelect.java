package momentofinertia;

import java.util.ArrayList;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;

public class PathSelect 
{
    final private double PTRADIUS = 0.1;
    final private double LNRADIUS = 0.05;
    
    private Group root;
    
    private ArrayList<Sphere> selectedPts = new ArrayList<>();
    private ArrayList<Line> selectedLns = new ArrayList<>();
    
    private Sphere selectSph = new Sphere();
    private Point3D selectPt = new Point3D(0, 0, 0);
    private Line selectLn = new Line();
    
    public PathSelect(MeshView object, Group root)
    {
        this.root = root;
        root.getChildren().add(selectLn);
    }
    
    public void update(Point3D newPoint)
    {
        if(selectPt.distance(newPoint) != 0 && !selectedPts.isEmpty())
        { 
            //Draw sphere
            Sphere s = new Sphere();
            s.setRadius(PTRADIUS);
            selectSph = s;
            selectPt = newPoint;
            
            
            //Edit line
            Sphere pt = selectedPts.get(selectedPts.size()-1);
            Point3D oldPoint = new Point3D(pt.getTranslateX(), pt.getTranslateY(), pt.getTranslateZ());
            selectLn.setLine(oldPoint, newPoint);
            selectLn.setRadius(LNRADIUS);
        }
    }
    
    public void click(Point3D point, boolean objClicked)
    {
        if(objClicked)
        {
            Sphere s = new Sphere();
            s.setRadius(PTRADIUS);
            s.setTranslateX(point.getX());
            s.setTranslateY(point.getY());
            s.setTranslateZ(point.getZ());
            selectedPts.add(s);

            Line newLine = new Line(selectLn);

            root.getChildren().addAll(s, newLine);
        }
        else if(!selectedPts.isEmpty())
        {
            Sphere s = new Sphere();
            s.setRadius(PTRADIUS);
            s.setTranslateX(selectPt.getX());
            s.setTranslateY(selectPt.getY());
            s.setTranslateZ(selectPt.getZ());
            selectedPts.add(s);

            Line newLine = new Line(selectLn);
            
            root.getChildren().addAll(s, newLine);
        }
    }
}
