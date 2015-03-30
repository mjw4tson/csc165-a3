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
	private SetLockedAction		lockedAction;
	private SceneNode			player;
	
	public OrbitUpDownAction(boolean isC, boolean iP, Camera3PController cc, SetLockedAction l,
			SceneNode player) {
		this.isController = isC;
		this.isPositive = iP;
		this.ccontroller = cc;
		this.cameraElevation = cc.getCameraAzimuth();
		this.lockedAction = l;
		this.player = player;
	}
	
	@Override
	public void performAction(	float time,
								Event evt) {
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
		
		// Rotate the avatar the same amount if the camera is locked.
		if(lockedAction.isLocked()){
			player.rotate(rotAmount, new Vector3D(1,0,0));
		}
		
		
		cameraElevation = ccontroller.getCameraElevation();
		cameraElevation += rotAmount;
		cameraElevation = cameraElevation % 360;
		ccontroller.setCameraElevation(this.cameraElevation);
	}
	
}
