package engine.objects;

import games.circuitshooter.CircuitShooter;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.awt.Color;

import sage.renderer.IRenderer;
import sage.scene.shape.Sphere;

public class Projectile extends Sphere {
	private int		updates;
	private float	speed;
	private Avatar	sourceAvatar;
	
	public Projectile(CircuitShooter cs, Avatar avatar) {
		super(1, 10, 10, Color.GREEN);
		super.scale(.5f, .7f, 1);

		sourceAvatar = avatar;
		speed = 45;
		updates = 0;
		
		if (sourceAvatar != cs.localPlayer)
			setColor(Color.RED);
		
		Matrix3D translate = new Matrix3D();
		Vector3D translation = avatar.getLocation();
		translate.translate(translation.getX(), translation.getY(), translation.getZ());
		setLocalTranslation(translate);
		System.out.println(avatar.getLocation());
		
		setLocalRotation((Matrix3D)avatar.getTriMesh().getLocalRotation().clone());
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
        updates++;
    }

	public Avatar getSourceAvatar() {
		return sourceAvatar;
	}

	public void setSourceAvatar(Avatar sourceAvatar) {
		this.sourceAvatar = sourceAvatar;
	}
	
	public boolean isExpired() {
		return updates > 1000 ? true : false;
	}
}
