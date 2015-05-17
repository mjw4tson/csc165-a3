package engine.objects;

import java.awt.Color;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.scene.shape.Sphere;

public class Projectile extends Sphere {
	Vector3D 	direction;
	int			age;
	float 		speed;
	
	public Projectile(Avatar avatar) {
		Matrix3D position = avatar.getTriMesh().getLocalTranslation();
		setLocalTranslation(position);
		this.setColor(Color.RED);
		
		age = 10;
		speed = 10;
	}
	
	public boolean isDead() {
		return age <= 0 ? true : false;
	}
}
