package engine.input.action.camera;

import games.circuitshooter.network.CircuitShooterClient;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;

/**
 * Handles the calculations to orbit the 3P camera left and right.
 *
 * @author Kevin
 */
public class OrbitLeftRightAction extends AbstractInputAction {
	private boolean					isController;
	private boolean					isPositive;
	private float					cameraAzimuth;
	private Camera3PController		ccontroller;
	private SetLockedAction			lockedAction;
	private CircuitShooterClient	client;
	private SceneNode				player;

	public OrbitLeftRightAction(boolean isC, boolean iP, Camera3PController cc, SetLockedAction l, SceneNode player, CircuitShooterClient client) {
		this.isController = isC;
		this.isPositive = iP;
		this.ccontroller = cc;
		this.cameraAzimuth = cc.getCameraAzimuth();
		this.lockedAction = l;
		this.player = player;
		this.client = client;
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
				rotAmount = -0.5f;
			} else if (evt.getValue() > 0.4) {
				rotAmount = 0.5f;
			} else {
				return;
			}
		}
		
		// Rotate the avatar the same amount if the camera is locked.
		if(lockedAction.isLocked()){
			player.rotate(rotAmount, new Vector3D(0,1,0));
			
			if (client != null) {
				client.getOutputHandler().sendRotateMsg(player.getLocalRotation());
			}
		}
		
		cameraAzimuth = ccontroller.getCameraAzimuth();
		cameraAzimuth += rotAmount;
		cameraAzimuth = cameraAzimuth % 360;
		ccontroller.setCameraAzimuth(this.cameraAzimuth);
	}

}
