package engine.objects;

import games.circuitshooter.CircuitShooter;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.awt.Color;

import sage.renderer.IRenderer;
import sage.scene.shape.Sphere;

public class Projectile extends Sphere {
	private int				age;
	private float 			speed;
	private Avatar			sourceAvatar;
	
	public Projectile(CircuitShooter cs, Avatar avatar) {
		super(1, 10, 10, Color.RED);

		sourceAvatar = avatar;
		age = 10;
		speed = 10;
		
		if (sourceAvatar != cs.localPlayer)
			setColor(Color.GREEN);
		
		Matrix3D translate = new Matrix3D();
		Vector3D translation = avatar.getTriMesh().getLocalTranslation().getCol(3);
		translate.translate(translation.getX(), translation.getY(), translation.getZ());
		setLocalTranslation(translate);
		
		setLocalRotation((Matrix3D)avatar.getTriMesh().getLocalRotation().clone());
	}
	
	public boolean isDead() {
		return age <= 0 ? true : false;
	}
	
    /**
     * Custom draw method to rotate and flash the treasure
     */
    @Override
    public void draw(IRenderer r) {
        super.draw(r);

        Matrix3D rot = getLocalRotation();
        Vector3D dir = new Vector3D(0, 0, 1);
        dir = dir.mult(rot);
        dir.scale((double) (speed * .1));
        translate((float)dir.getX(), (float)dir.getY(), (float)dir.getZ());
    }
}
