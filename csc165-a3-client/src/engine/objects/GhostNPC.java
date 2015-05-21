package engine.objects;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;

import java.awt.Color;
import java.util.UUID;

import sage.scene.shape.Sphere;

public class GhostNPC extends Sphere {
	private UUID id;
	
	public GhostNPC(UUID id, Point3D position) {
		super(1, 10, 10, Color.CYAN);
		
		this.id = id;
		setPosition(position);
	}
	
	public UUID getId() {
		return id;
	}
	
	public Point3D getPosition() {
		return new Point3D(getLocalTranslation().getCol(3));
	}
	
	public void setPosition(Point3D position) {
		Matrix3D trans = new Matrix3D();
		trans.translate(position.getX(), position.getY(), position.getZ());
		setLocalTranslation(trans);
	}
}
