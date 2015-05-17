package engine.objects;

import graphicslib3D.Vector3D;
import sage.scene.shape.Sphere;

public class Projectile extends Sphere {
	Vector3D 	direction;
	int			age;
	float 		speed;
	
	public Projectile(Vector3D direction) {
		this.direction = direction;
		age = 10;
		speed = 10;
	}
	
	public boolean isDead() {
		return age <= 0 ? true : false;
	}
}
