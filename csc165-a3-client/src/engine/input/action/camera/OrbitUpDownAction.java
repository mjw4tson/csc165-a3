package engine.input.action.camera;

import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;

/**
 * Handles the calculations to orbit the 3P camera up and down.
 *
 * @author Kevin
 */
public class OrbitUpDownAction extends AbstractInputAction {
	private boolean				isController;
	private boolean				isPositive;
	private float				cameraElevation;
	private Camera3PController	ccontroller;
	
	public OrbitUpDownAction(boolean isC, boolean iP, Camera3PController cc) {
		this.isController = isC;
		this.isPositive = iP;
		this.ccontroller = cc;
		this.cameraElevation = cc.getCameraAzimuth();
	}
	
	@Override
	public void performAction(float time, Event evt) {
		float rotAmount;
		
		// Keyboard
		if (!isController) {
			if (isPositive) {
				rotAmount = 0.5f;
			} else {
				rotAmount = -0.5f;
			}
		// Controller
		} else {
			if (evt.getValue() < -0.4) {
				rotAmount = 0.5f;
			} else {
				if (evt.getValue() > 0.4) {
					rotAmount = -0.5f;
				} else {
					rotAmount = 0.0f;
				}
			}
		}

		cameraElevation = ccontroller.getCameraElevation();
		
        if (!(cameraElevation + rotAmount > 80 || cameraElevation + rotAmount < 1))
    		ccontroller.setCameraElevation(cameraElevation + rotAmount);
	}
	
}
