package momentofinertia;

import javafx.scene.PerspectiveCamera;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Transform;

public class LookCamera extends PerspectiveCamera
{
    private double screenX;
    private double screenY;
    
    
    public LookCamera(boolean fixedEyeAtCameraZero)
    {
        super(fixedEyeAtCameraZero);
    }

    public void screenUpdate(double x, double y)
    {
        screenX = x;
        screenY = y;
    }
    
    public void mouseMove(double x, double y, double sens)
    {
        double dx = screenX != 0 ? x - screenX : 0;
        double dy = screenY != 0 ? y - screenY : 0;
        screenUpdate(x, y);
    }
    
    public void keyMove(KeyEvent ke, double sens)
    {
        Transform t = this.getLocalToSceneTransform();
        switch(ke.getCode())
        {
            case W:
                this.setTranslateX(this.getTranslateX() + (sens * t.getMxz()));
                this.setTranslateY(this.getTranslateY() + (sens * t.getMyz()));
                this.setTranslateZ(this.getTranslateZ() + (sens * t.getMzz()));
                break;
            case S:
                this.setTranslateX(this.getTranslateX() - (sens * t.getMxz()));
                this.setTranslateY(this.getTranslateY() - (sens * t.getMyz()));
                this.setTranslateZ(this.getTranslateZ() - (sens * t.getMzz()));
                break;
            case A:
                this.setTranslateX(this.getTranslateX() - (sens * t.getMxx()));
                this.setTranslateY(this.getTranslateY() - (sens * t.getMyx()));
                this.setTranslateZ(this.getTranslateZ() - (sens * t.getMzx()));
                break;
            case D:
                this.setTranslateX(this.getTranslateX() + (sens * t.getMxx()));
                this.setTranslateY(this.getTranslateY() + (sens * t.getMyx()));
                this.setTranslateZ(this.getTranslateZ() + (sens * t.getMzx()));
                break;
            case SPACE:
                this.setTranslateX(this.getTranslateX() - (sens * t.getMxy()));
                this.setTranslateY(this.getTranslateY() - (sens * t.getMyy()));
                this.setTranslateZ(this.getTranslateZ() - (sens * t.getMzy()));
                break;
            case CONTROL:
                this.setTranslateX(this.getTranslateX() + (sens * t.getMxy()));
                this.setTranslateY(this.getTranslateY() + (sens * t.getMyy()));
                this.setTranslateZ(this.getTranslateZ() + (sens * t.getMzy()));
                break;
        }
    }
}

