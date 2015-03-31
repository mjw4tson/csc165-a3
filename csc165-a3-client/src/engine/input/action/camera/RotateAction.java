package engine.input.action.camera;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

/**
 * IAction for rotating the camera.
 *
 * @author ktajeran
 */
public class RotateAction extends AbstractInputAction {
	private ICamera		camera;				// The camera to manipulate.
	private Float		rotationAmount;		// The set speed action.
	private Rotation	rotationType;
	private float		idleConstant	= 0.4f;

	/**
	 * The ForwardAction constructor, sets the local variables from the passed in parameters.
	 *
	 * @param c
	 *            - The ICamera object.
	 * @param r
	 *            - The SetSpeedAction.
	 */
	public RotateAction(ICamera c, Float r, Rotation ro) {
		camera = c;
		rotationAmount = r;
		rotationType = ro;
	}

	/*
	 * The method containing the logic for the action to be invoked, in this case to move the camera forward.
	 */
	@Override
	public void performAction(	float time,
	                          	Event e) {
		Matrix3D rotAmount = new Matrix3D();
		float componentValue = e.getValue(); // The value of the component.

		boolean isRXAxis = e.getComponent().getName().contains("X Rotation");
		boolean isRYAxis = e.getComponent().getName().contains("Y Rotation");

		// Get current camera location.
		Vector3D upAx = camera.getUpAxis();
		Vector3D rightAx = camera.getRightAxis();
		Vector3D viewDir = camera.getViewDirection();

		if (isRXAxis) {
			if (componentValue < -idleConstant) {
				rotationType = Rotation.POSYAW;

			} else if (componentValue > idleConstant) {
				rotationType = Rotation.NEGYAW;
			} else {
				rotationType = Rotation.IDLE;
			}
		} else if (isRYAxis) {
			if (componentValue < -idleConstant) {
				rotationType = Rotation.POSPITCH;
			} else if (componentValue > idleConstant) {
				rotationType = Rotation.NEGPITCH;
			} else {
				rotationType = Rotation.IDLE;
			}
		}

		switch (rotationType) {
			case POSPITCH:
				rotAmount.rotate(rotationAmount, rightAx);
				break;
			case NEGPITCH:
				rotAmount.rotate(-rotationAmount, rightAx);
				break;
			case POSYAW:
				rotAmount.rotate(rotationAmount, upAx);
				break;
			case NEGYAW:
				rotAmount.rotate(-rotationAmount, upAx);
				break;
			default:
				break;

		}

		if (rotationType == Rotation.POSPITCH || rotationType == Rotation.NEGPITCH) {
			upAx = upAx.mult(rotAmount);
			camera.setUpAxis(upAx.normalize());
		} else {
			rightAx = rightAx.mult(rotAmount);
			camera.setRightAxis(rightAx.normalize());
		}
		viewDir = viewDir.mult(rotAmount);
		if (rotationType != Rotation.IDLE) {
			camera.setViewDirection(viewDir.normalize());
		}
	}

	/**
	 * Enum holding rotational constants.
	 *
	 * @author ktajeran
	 */
	public enum Rotation {
		POSYAW, NEGYAW, POSPITCH, NEGPITCH, IDLE;
	}
}
