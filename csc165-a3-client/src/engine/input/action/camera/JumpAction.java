package engine.input.action.camera;

import engine.objects.Avatar;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import sage.physics.IPhysicsEngine;

/**
 * IAction for jumping the avatar.
 *
 * @author ktajeran
 */
public class JumpAction extends AbstractInputAction {
	private Avatar	avatar;
	
	/**
	 * The ForwardAction constructor, sets the local variables from the passed in parameters.
	 *
	 * @param c
	 *            - The ICamera object.
	 * @param r
	 *            - The SetSpeedAction.
	 */
	public JumpAction(Avatar a) {
		avatar = a;
	}
	
	/*
	 * The method containing the logic for the action to be invoked, in this case to move the camera forward.
	 */
	@Override
	public void performAction(	float time,
								Event e) {
		avatar.setJumping(!avatar.isJumping());
	}

}
