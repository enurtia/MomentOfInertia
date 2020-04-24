package momentofinertia;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class MomentOfInertia extends Application 
{
    private final Dimension SIZE = new Dimension(1200,800);
    private final String TITLE = "Moment of Inertia Program";
    private final double KEY_SENS = 2;
    
    private double x;
    private double y;
    
    private double mPerUnit;
    private double objMass;
    
    private Point3D avg;
    
    private ArrayList<Sphere> lengthPts = new ArrayList<>();
    
    //The points that make up the object
    ArrayList<Double[]> pts = new ArrayList<>();
    
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Group rootMain = new Group();
        Scene scene = new Scene(rootMain, SIZE.width, SIZE.height, true);   
        
        Group root = new Group();
        SubScene subscene = new SubScene(root, SIZE.width, SIZE.height, true, SceneAntialiasing.BALANCED);
        subscene.heightProperty().bind(scene.heightProperty());
        subscene.widthProperty().bind(scene.widthProperty());
        subscene.setPickOnBounds(true);
        
        rootMain.getChildren().add(subscene);
        
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        scene.setFill(Color.GRAY);
        
        
        ObjModelImporter importer = new ObjModelImporter();
        importer.read(new File("C:\\Users\\Enurtia\\Desktop\\Sphere.obj"));
        
        MeshView object = importer.getImport()[0];
        object.setDrawMode(DrawMode.LINE);
        importer.close();
        
        
        ObjPoints objP = new ObjPoints(object);
        
        root.getChildren().add(object);
        
        
        LookCamera camera = new LookCamera(true);
        subscene.setCamera(camera);
        camera.setTranslateZ(-30);
        
        ArrayList<Double[]> boundBox = objP.bBox(object.getBoundsInParent());
        
        //Create 8 corners
        for(int i = 0; i < boundBox.size(); i++)
        {
            Sphere s = new Sphere();
            s.setRadius(0.2);
            
            Double [] pt = boundBox.get(i);
            s.setTranslateX(pt[0]);
            s.setTranslateY(pt[1]);
            s.setTranslateZ(pt[2]);
            
            root.getChildren().add(s);
        }
            
        pts = objP.getPts();
        
        
        for(int i = 0; i < pts.size(); i++)
        {
            Sphere s = new Sphere();
            s.setRadius(0.2);
            s.setTranslateX(pts.get(i)[0]);
            s.setTranslateY(pts.get(i)[1]);
            s.setTranslateZ(pts.get(i)[2]);
            
            root.getChildren().add(s);
        }
        
        //Calculate where the center of mass is located
        avg = new Point3D(0,0,0);
        for(int i = 0; i < pts.size(); i++)
        {
            Double[] pt = pts.get(i);
            avg = avg.add(pt[0], pt[1], pt[2]);
        }
        avg = avg.multiply(1.0 / pts.size());
        
        updateText(rootMain);
        
        scene.setOnKeyPressed((KeyEvent ke) ->
        {
            camera.keyMove(ke, KEY_SENS);
            
            if(ke.getCode() == KeyCode.ENTER)
            {
                String massIn = JOptionPane.showInputDialog("Input the mass of the object in Kilograms: ");
                objMass = Double.parseDouble(massIn);
                updateText(rootMain);
            }
            else if(ke.getCode() == KeyCode.SHIFT)
            {
                String mPerPixIn = JOptionPane.showInputDialog("Input the meters per unit: ");
                mPerUnit = Double.parseDouble(mPerPixIn);
                updateText(rootMain);
            }
        });       
        
        subscene.setOnMousePressed((MouseEvent me) ->
        {
            camera.screenUpdate(me.getSceneX(), me.getSceneY());
        });
        
        subscene.setOnMouseDragged((MouseEvent me) ->
        {
            if(me.isPrimaryButtonDown())
            {
                double sens = 0.06;
                camera.mouseMove(me.getSceneX(), me.getSceneY(), sens);
            }
        });
        
        subscene.setOnMouseClicked((MouseEvent me) ->
        {           
            PickResult pr = me.getPickResult();
            Point3D point = pr.getIntersectedPoint();
            
            Sphere s = new Sphere();
            s.setRadius(0.2);
            s.setTranslateX(point.getX());
            s.setTranslateY(point.getY());
            s.setTranslateZ(point.getZ());
            
            PhongMaterial mat = new PhongMaterial();
            mat.setDiffuseColor(lengthPts.size() != 1 ? Color.GREEN : Color.RED);
            s.setMaterial(mat);
            root.getChildren().add(s);
            
            if(lengthPts.size() == 2)
            {
                root.getChildren().removeAll(lengthPts);
                lengthPts.clear();
            }
            else if(lengthPts.size() == 1)
            {
                double startx = lengthPts.get(0).getTranslateX();
                double starty = lengthPts.get(0).getTranslateY();
                double startz = lengthPts.get(0).getTranslateZ();
                
                double dx = startx - point.getX();
                double dy = starty - point.getY();
                double dz = startz - point.getZ();
                
                
                double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
                System.out.println(dist);
            }
            
            lengthPts.add(s);
        });
        
        
        Line cylinx = new Line(avg, avg.add(new Point3D(10,0,0)));
        Line cyliny = new Line(avg, avg.add(new Point3D(0,10,0)));
        Line cylinz = new Line(avg, avg.add(new Point3D(0,0,10)));
        root.getChildren().addAll(cylinx, cyliny, cylinz);
    }
    
    private void updateText(Group root)
    {
        root.getChildren().removeIf(n -> (n.getClass() == Text.class));
        root.getChildren().removeIf(n -> (n.getClass() == Cylinder.class));
        
        Text massText = new Text();
        massText.setText("Mass: " + objMass + " kg");
        massText.setTranslateX(25);
        massText.setTranslateY(20);
        
        Text mPerPixelText = new Text();
        mPerPixelText.setText("Meters per unit: " + mPerUnit);
        mPerPixelText.setTranslateX(25);
        mPerPixelText.setTranslateY(35);
        
        Text mx = new Text();
        mx.setText("Moment x-axis: " + calculate(avg, new Point3D(1,0,0), mPerUnit, objMass) + " kg m^2");
        mx.setTranslateX(25);
        mx.setTranslateY(50);
        
        Text my = new Text();
        my.setText("Moment y-axis: " + calculate(avg, new Point3D(0,1,0), mPerUnit, objMass) + " kg m^2");
        my.setTranslateX(25);
        my.setTranslateY(65);
        
        Text mz = new Text();
        mz.setText("Moment z-axis: " + calculate(avg, new Point3D(0,0,1), mPerUnit, objMass) + " kg m^2");
        mz.setTranslateX(25);
        mz.setTranslateY(80);
        
        
        root.getChildren().addAll(massText, mPerPixelText, mx, my, mz);
    }
    
    public double calculate(Point3D origin, Point3D axisDir, double metersPerUnit, double mass)
    {
        double particleMass = mass / pts.size();    //Kilograms
        
        double total = 0;
        for(int i = 0; i < pts.size(); i++)
        {
            Double[] pt = pts.get(i);
            double x = pt[0];
            double y = pt[1];
            double z = pt[2];
            
            Point3D p = new Point3D(x,y,z);
            
            
            double r = axisDir.dotProduct(p.subtract(origin)) * metersPerUnit; //Converted to meters
            System.out.println("R: " + r);
            
            total += particleMass*r*r;
        }
        
        return total;
    }
    
    public static void main(String[] args) 
    {
        launch(args);
    }
    
}
