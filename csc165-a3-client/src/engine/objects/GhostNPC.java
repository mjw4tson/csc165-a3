package engine.objects;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.util.UUID;

import sage.scene.shape.Sphere;

public class GhostNPC extends Sphere {
	private UUID id;
	
	public GhostNPC(UUID id, Vector3D position) {
		this.id = id;
		setPosition(position);
	}
	
	public UUID getId() {
		return id;
	}
	
	public Matrix3D getPosition() {
		return getLocalTranslation();
	}
	
	public void setPosition(Vector3D position) {
		Matrix3D trans = new Matrix3D();
		trans.translate(position.getX(), position.getY(), position.getZ());
		setLocalTranslation(trans);
	}
}
