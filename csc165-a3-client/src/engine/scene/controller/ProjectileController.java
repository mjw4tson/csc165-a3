package engine.scene.controller;

import engine.objects.Projectile;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.util.Iterator;

import sage.scene.Controller;
import sage.scene.Group;
import sage.scene.SceneNode;

public class ProjectileController extends Controller {
	@Override
	public void update(double elapsedTimeMS) {
		for(SceneNode node : controlledNodes) {
			if (node instanceof Group) {
		        Group group = (Group)node;
		        Iterator<SceneNode> itr = group.getChildren();
		        
		        while (itr.hasNext()) {
		        	SceneNode obj = itr.next();
		        	
		        	if (obj instanceof Projectile) {
		        		Projectile proj = (Projectile)obj;
						Matrix3D rot = (Matrix3D)proj.getLocalRotation().clone();
						Vector3D dir = new Vector3D(0, 0, 1);

				        dir = dir.mult(rot);
				        dir.scale((double) (proj.getSpeed() * elapsedTimeMS));
				        proj.translate((float)dir.getX(), (float)dir.getY(), (float)dir.getZ());
		        	}
		        }
			}
		}
	}
}
