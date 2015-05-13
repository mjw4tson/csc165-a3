package event;

import sage.scene.shape.Sphere;
import graphicslib3D.Point3D;

public class NPC extends Sphere {
    Point3D location; // other state info goes here (FSM)
    private boolean isSmall = false;
    
    public NPC(){
        
    }

    public double getX() { return location.getX(); }
    public double getY() { return location.getY(); }
    public double getZ() { return location.getZ(); }

    public void updateLocation() {  
       //TODO
    }
    
    public void getSmall(){
        if(isSmall){
            
        } else {
            this.scale(.5f, .5f, .5f);
            isSmall = !isSmall;
        }
    }
    
    public void getBig(){
        if(!isSmall){
            
        } else {
            this.scale(2f, 2f, 2f);
            isSmall = !isSmall;
        }
    }
    
    public void randomizeLocaton(int x, int z){
        this.location.setX(getX());
        this.location.setZ(getZ());
    }
}
