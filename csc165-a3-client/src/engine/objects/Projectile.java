package engine.objects;

import games.circuitshooter.CircuitShooter;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.awt.Color;

import sage.scene.shape.Sphere;

public class Projectile extends Sphere {
	private int		updates;
	private float	speed;
	private Avatar	sourceAvatar;
	
	public Projectile(CircuitShooter cs, Avatar avatar) {
		super(1, 10, 10, Color.GREEN);
		super.scale(2f, 1.7f, 3);

		sourceAvatar = avatar;
		speed = 0.5f;
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

	public Avatar getSourceAvatar() {
		return sourceAvatar;
	}

	public void setSourceAvatar(Avatar sourceAvatar) {
		this.sourceAvatar = sourceAvatar;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void incrementUpdates() {
		updates++;
	}
	
	public boolean isExpired() {
		return updates > 1000 ? true : false;
	}
}