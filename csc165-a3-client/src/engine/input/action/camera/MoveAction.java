package engine.input.action.camera;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

/**
 * IAction for moving the specified camera.
 *
 * @author ktajeran
 *
 */
public class MoveAction extends AbstractInputAction {
	private ICamera			camera;				// The camera to manipulate.
	private SetSpeedAction	runAction;				// The set speed action.
	private Direction		direction;				// Direction to move.
	private float			idleConstant	= 0.2f;

	/**
	 * The ForwardAction constructor, sets the local variables from the passed in parameters.
	 *
	 * @param c - The ICamera object.
	 * @param r - The SetSpeedAction.
	 */
	public MoveAction( ICamera c, SetSpeedAction r, Direction d ) {
		camera = c;
		runAction = r;
		direction = d;
	}

	/*
	 * The method containing the logic for the action to be invoked, in this case to move the camera forward.
	 */
	@Override
	public void performAction( float time , Event e ) {
		float moveConstant; // The amount to move by.
		float componentValue = e.getValue(); // The value of the component.

		boolean isXAxis = e.getComponent().getName().contains("X Axis"); // Determines if the controller is on the X axis.
		boolean isYAxis = e.getComponent().getName().contains("Y Axis"); // Determines if the controller is on the Y axis.
		boolean setLocation = true;

		// Get current camera vectors.
		Vector3D newLocation = new Vector3D();

		// Determine if the player is running.
		if ( runAction.isRunning() ) {
			moveConstant = .06f;
		} else {
			moveConstant = .03f;
		}
		
		if ( isXAxis ) {
			if ( componentValue < -idleConstant ) {
				direction = Direction.LEFT;

			} else if ( componentValue > idleConstant ) {
				direction = Direction.RIGHT;
			} else {
				direction = Direction.IDLE;
			}
		} else if ( isYAxis ) {
			if ( componentValue < -idleConstant ) {
				direction = Direction.FORWARD;
			} else if ( componentValue > idleConstant ) {
				direction = Direction.BACKWARD;
			} else {
				direction = Direction.IDLE;
			}
		} 
		
		switch (direction) {
			case FORWARD:
				newLocation = moveDirection(Direction.FORWARD, false, moveConstant, time);
				break;
			case BACKWARD:
				newLocation = moveDirection(Direction.BACKWARD, true, moveConstant, time);
				break;
			case LEFT:
				newLocation = moveDirection(Direction.LEFT, true, moveConstant, time);
				break;
			case RIGHT:
				newLocation = moveDirection(Direction.RIGHT, false, moveConstant, time);
				break;
			default:
				setLocation = false;
				break;
		}

		double newX = newLocation.getX();
		double newY = newLocation.getY();
		double newZ = newLocation.getZ();
		Point3D newLoc = new Point3D(newX, newY, newZ);

		if ( setLocation ) {
			camera.setLocation(newLoc);
		}

	}

	/**
	 * This method handles the actual logic for moving in a specified direction.
	 * 
	 * @param dir 			- The direction.
	 * @param negativeAxis	- Is the direction negative?
	 * @param moveVal		- The distance traveled.
	 * @param timeVal		- The amount of time elapsed.
	 * @return 				- A Vector3D that contains the new location of the camera.
	 */
	private Vector3D moveDirection( Direction dir , Boolean negativeAxis , float moveVal , float timeVal ) {
		Vector3D viewDir;
		Vector3D newLocVector;
		Vector3D curLocVector = new Vector3D(camera.getLocation());

		if ( dir == Direction.FORWARD || dir == Direction.BACKWARD ) {
			viewDir = camera.getViewDirection().normalize();
			if ( negativeAxis ) {
				newLocVector = curLocVector.minus(viewDir.mult(moveVal * timeVal));
			} else {
				newLocVector = curLocVector.add(viewDir.mult(moveVal * timeVal));
			}
		} else {
			viewDir = camera.getRightAxis().normalize();
			if ( negativeAxis ) {
				newLocVector = curLocVector.minus(viewDir.mult(moveVal * timeVal));
			} else {
				newLocVector = curLocVector.add(viewDir.mult(moveVal * timeVal));
			}
		}
		return newLocVector;
	}

	/**
	 * Enum holding directional constants.
	 *
	 * @author ktajeran
	 */
	public enum Direction {
		FORWARD, BACKWARD, LEFT, RIGHT, IDLE;
	}
}
