package engine.scene.controller;

import graphicslib3D.Vector3D;
import sage.scene.Controller;
import sage.scene.SceneNode;

/**
 * A SceneNode controller tasked with bouncing the scene node horizontally.
 * 
 * @author Kevin
 */
public class BounceController extends Controller {
	Vector3D		axis			= new Vector3D(0, 5, 0);
	private boolean	postiveBounce	= false;
	private int		i				= 0;
	
	@Override
	public void update(double amount) {
		int value;
		
		if (!postiveBounce) {
			value = -1;
		} else {
			value = 1;
		}
		
		for (SceneNode n : this.controlledNodes) {
			n.translate(.1f * value, 0, 0);
			i = i + 1;
			if (i > 80) {
				postiveBounce = !postiveBounce;
				i = 0;
			}
		}
	}
}
