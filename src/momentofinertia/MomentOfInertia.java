package momentofinertia;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import java.awt.Dimension;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
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
        
        sphere.setTranslateZ(30);
        
        
        PerspectiveCamera camera = new PerspectiveCamera(true);
        scene.setCamera(camera);
        
        ArrayList<Double[]> boundBox = bBox(sphere.getBoundsInParent());
        
        for(int i = 0; i < boundBox.size(); i++)
        {
            Sphere s = new Sphere();
            s.setRadius(0.2);
            
            Double [] pt = boundBox.get(i);
            s.setTranslateX(pt[0]);
            s.setTranslateY(pt[1]);
            s.setTranslateZ(pt[2]);
            
            System.out.println(pt[0] + ", " + pt[1] + ", " + pt[2]);
            
            root.getChildren().add(s);
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
    
    public static void main(String[] args) 
    {
        launch(args);
    }
    
}
