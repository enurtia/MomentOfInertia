package momentofinertia;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

public class MomentOfInertia extends Application 
{
    private final Dimension SIZE = new Dimension(1200,800);
    private final String TITLE = "Moment of Inertia Program";
    private final double KEY_SENS = 2;
    
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Group root = new Group();
        
        Scene scene = new Scene(root, SIZE.width, SIZE.height);
        
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        scene.setFill(Color.GRAY);
        
        
        ObjModelImporter importer = new ObjModelImporter();
        importer.read(new File("C:\\Users\\Enurtia\\Desktop\\Sphere.obj"));
        
        MeshView sphere = importer.getImport()[0];
        importer.close();
        
        root.getChildren().add(sphere);
        
        
        PerspectiveCamera camera = new PerspectiveCamera(true);
        scene.setCamera(camera);
        camera.setTranslateZ(-30);
        
        ArrayList<Double[]> boundBox = bBox(sphere.getBoundsInParent());
        
        //Create 4 corners
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
        
        TriangleMesh triMesh = (TriangleMesh)sphere.getMesh();

        //Get normals
        ObservableIntegerArray faces = triMesh.getFaces();
        ObservableFloatArray points = triMesh.getPoints();
        
        ArrayList<Double[]> normals = new ArrayList<>();
        ArrayList<Double[]> faceCentroids = new ArrayList<>();
        
        for(int i = 0; i < faces.size(); i += 6)
        {
            double p1x = points.get(3*faces.get(i));
            double p1y = points.get(3*faces.get(i) + 1);
            double p1z = points.get(3*faces.get(i) + 2);
            
            double p2x = points.get(3*faces.get(i+2));
            double p2y = points.get(3*faces.get(i+2) + 1);
            double p2z = points.get(3*faces.get(i+2) + 2);
            
            double p3x = points.get(3*faces.get(i+4));
            double p3y = points.get(3*faces.get(i+4) + 1);
            double p3z = points.get(3*faces.get(i+4) + 2);
            
            
            //Normal is the cross product of u and v, where
            //u is between points 1 and 2,
            //v is between points 2 and 3.
            double ux = (p2x - p1x);
            double uy = (p2y - p1y);
            double uz = (p2z - p1z);
            
            double vx = (p3x - p1x);
            double vy = (p3y - p1y);
            double vz = (p3z - p1z);
            
            double nx = (uy*vz - uz*vy);
            double ny = (uz*vx - ux*vz);    
            double nz = (ux*vy - uy*vx);
            
            double dist = Math.sqrt(nx*nx + ny*ny + nz*nz);
            
            normals.add(new Double[]{nx/dist, ny/dist, nz/dist});
            
            //Face Centroid
            double x = ((p1x + p2x + p3x) / 3);
            double y = ((p1y + p2y + p3y) / 3);
            double z = ((p1z + p2z + p3z) / 3);
            faceCentroids.add(new Double[]{x,y,z});
        }
        
        
        //Bounding Box Discretize
        //Points not inside object are removed.
        ArrayList<Double[]> pts = discretizeSolid(sphere.getBoundsInParent(), 20);
        for(int i = pts.size()-1; i >= 0; i--)
        {
            Double[] pt = pts.get(i);
            
            boolean isIn = isInside(pt, faceCentroids, normals);
            if(!isIn)
            {
                pts.remove(pt);
            }
            else
            {
                Sphere s = new Sphere();
                s.setRadius(0.1);
                s.setTranslateX(pt[0]);
                s.setTranslateY(pt[1]);
                s.setTranslateZ(pt[2]);
            
                root.getChildren().add(s);
            }
        }
        
        scene.setOnKeyPressed((KeyEvent ke) ->
        {
            Transform t = camera.getLocalToSceneTransform();
            switch(ke.getCode())
            {
                case W:
                    camera.setTranslateX(camera.getTranslateX() + (KEY_SENS * t.getMxz()));
                    camera.setTranslateY(camera.getTranslateY() + (KEY_SENS * t.getMyz()));
                    camera.setTranslateZ(camera.getTranslateZ() + (KEY_SENS * t.getMzz()));
                    break;
                case S:
                    camera.setTranslateX(camera.getTranslateX() - (KEY_SENS * t.getMxz()));
                    camera.setTranslateY(camera.getTranslateY() - (KEY_SENS * t.getMyz()));
                    camera.setTranslateZ(camera.getTranslateZ() - (KEY_SENS * t.getMzz()));
                    break;
                case A:
                    camera.setTranslateX(camera.getTranslateX() - (KEY_SENS * t.getMxx()));
                    camera.setTranslateY(camera.getTranslateY() - (KEY_SENS * t.getMyx()));
                    camera.setTranslateZ(camera.getTranslateZ() - (KEY_SENS * t.getMzx()));
                    break;
                case D:
                    camera.setTranslateX(camera.getTranslateX() + (KEY_SENS * t.getMxx()));
                    camera.setTranslateY(camera.getTranslateY() + (KEY_SENS * t.getMyx()));
                    camera.setTranslateZ(camera.getTranslateZ() + (KEY_SENS * t.getMzx()));
                    break;
                case SPACE:
                    camera.setTranslateX(camera.getTranslateX() - (KEY_SENS * t.getMxy()));
                    camera.setTranslateY(camera.getTranslateY() - (KEY_SENS * t.getMyy()));
                    camera.setTranslateZ(camera.getTranslateZ() - (KEY_SENS * t.getMzy()));
                    break;
                case CONTROL:
                    camera.setTranslateX(camera.getTranslateX() + (KEY_SENS * t.getMxy()));
                    camera.setTranslateY(camera.getTranslateY() + (KEY_SENS * t.getMyy()));
                    camera.setTranslateZ(camera.getTranslateZ() + (KEY_SENS * t.getMzy()));
                    break;
            }
        });       
    }

    private boolean isInside(Double[] pt, ArrayList<Double[]> centroids, ArrayList<Double[]> norms)
    {
        double x = pt[0];
        double y = pt[1];
        double z = pt[2];
        
        //Find closest centroid to point
        int minInd = -1;
        double min = Integer.MAX_VALUE;
        double[] minVector = new double[3];
        
        for(int i = 0; i < centroids.size(); i++)
        {
            Double[] centroid = centroids.get(i);
            
            double dx = (centroid[0] - x);
            double dy = (centroid[1] - y);
            double dz = (centroid[2] - z);
            
            
            
            double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
            if(dist < min)
            {
                min = dist;
                minInd = i;
                minVector = new double[]{dx/dist, dy/dist, dz/dist};
            }
        }
        
        //Calculate dot product of normal and vector from point to centroid
        Double[] normal = norms.get(minInd);
        double dp = normal[0]*minVector[0] + normal[1]*minVector[1] + normal[2]*minVector[2];
        
        return dp >= 0;
    }
    
   private ArrayList<Double[]> bBox(Bounds bounds)
    {
        double[] x = {bounds.getMinX(), bounds.getMaxX()};
        double[] y = {bounds.getMinY(), bounds.getMaxY()};
        double[] z = {bounds.getMinZ(), bounds.getMaxZ()};
     
        ArrayList<Double[]> out = new ArrayList<>();
        out.add(new Double[]{x[0], y[0], z[0]});
        out.add(new Double[]{x[1], y[0], z[0]});
        out.add(new Double[]{x[0], y[1], z[0]});
        out.add(new Double[]{x[0], y[0], z[1]});
        out.add(new Double[]{x[1], y[1], z[0]});
        out.add(new Double[]{x[0], y[1], z[1]});
        out.add(new Double[]{x[1], y[0], z[1]});
        out.add(new Double[]{x[1], y[1], z[1]});
   
        return out;
    }
    
   //Gets ArrayList of {x,y,z} values that fill bounds with a given density.
   private ArrayList<Double[]> discretizeSolid(Bounds bounds, double density) throws Exception
   {
       double xLen = bounds.getMaxX() - bounds.getMinX();
       double yLen = bounds.getMaxY() - bounds.getMinY();
       double zLen = bounds.getMaxZ() - bounds.getMinZ();
       
       ArrayList<Double[]> pts = new ArrayList<>();
       
       for(double x = 0; x <= xLen; x += xLen / density)
       {
           for(double y = 0; y <= yLen; y += yLen / density)
           {
               for(double z = 0; z <= zLen; z += zLen / density)
               {
                   Double[] pt = new Double[]{bounds.getMinX() + x, bounds.getMinY() + y, bounds.getMinZ() + z};
                   pts.add(pt);
               }
           }
       }
       
       return pts;
   }
   
    public static void main(String[] args) 
    {
        launch(args);
    }
    
}
