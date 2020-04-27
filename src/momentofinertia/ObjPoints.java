package momentofinertia;

import java.util.ArrayList;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class ObjPoints 
{
    private double density = 30;
    
    private MeshView meshview = new MeshView();
    
    private TriangleMesh triMesh;

    //Get normals based on counter-clockwise winding
    private ObservableIntegerArray faces;
    private ObservableFloatArray points;

    private ArrayList<Double[]> normals = new ArrayList<>();
    private ArrayList<Double[]> centroids = new ArrayList<>();
    private ArrayList<Double[]> pts = new ArrayList<>();
    
    private boolean counterClockW = true;
    
    public ObjPoints(MeshView meshview) throws Exception
    {
        this.meshview = meshview;
        triMesh = (TriangleMesh)meshview.getMesh();
        faces = triMesh.getFaces();
        points = triMesh.getPoints();
        
        init();
    }
    
    private void init()
    {
        normals.clear();
        centroids.clear();
        pts.clear();
        
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
            
            double nx = (uy*vz - uz*vy) * (counterClockW ? 1 : -1);
            double ny = (uz*vx - ux*vz) * (counterClockW ? 1 : -1);    
            double nz = (ux*vy - uy*vx) * (counterClockW ? 1 : -1);
            
            double dist = Math.sqrt(nx*nx + ny*ny + nz*nz);
            
            normals.add(new Double[]{nx/dist, ny/dist, nz/dist});
            
            //Face Centroid
            double x = ((p1x + p2x + p3x) / 3);
            double y = ((p1y + p2y + p3y) / 3);
            double z = ((p1z + p2z + p3z) / 3);
            centroids.add(new Double[]{x,y,z});          
        }
        
        calculatePts();
    }
    
    private void calculatePts()
    {
        try
        {
            pts = discretizeSolid(meshview.getBoundsInParent());
            for(int i = pts.size()-1; i >= 0; i--)
            {
                Double[] pt = pts.get(i);

                boolean isIn = isInside(pt);
                if(!isIn)
                {
                    pts.remove(pt);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    //Checks whether point is inside mesh or not
    private boolean isInside(Double[] pt)
    {
        double x = pt[0];
        double y = pt[1];
        double z = pt[2];
        
        //Find closest centroid to point
        int minInd = -1;
        double min = Integer.MAX_VALUE;
        double[] minVector = new double[3];
        
        PhongMaterial mat = new PhongMaterial();
        PhongMaterial mat2 = new PhongMaterial();
        
        mat.setDiffuseColor(Color.RED);
        mat2.setDiffuseColor(Color.BLUE);
        
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
        Double[] normal = normals.get(minInd);
        double dp = normal[0]*minVector[0] + normal[1]*minVector[1] + normal[2]*minVector[2];
        
        return dp >= 0;

    }
    
   //Gets ArrayList of {x,y,z} values that fill bounds with a given density.
   private ArrayList<Double[]> discretizeSolid(Bounds bounds) throws Exception
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
   
   public void setDensity(double density)
   {
       this.density = density;
       init();
   }
   
   public double getDensity()
   {
       return density;
   }
   
    public ArrayList<Double[]> getPts()
    {
        return pts;
    }
   
   public void setCCW(boolean ccw)
   {
       counterClockW = ccw;
       init();
   }
   
   public ArrayList<Double[]> bBox(Bounds bounds)
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
}
