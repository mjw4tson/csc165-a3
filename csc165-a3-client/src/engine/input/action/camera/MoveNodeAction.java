package engine.input.action.camera;

import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;
import engine.input.action.camera.MoveAction.Direction;
import games.circuitshooter.CircuitShooter;
import games.circuitshooter.network.CircuitShooterClient;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

public class MoveNodeAction extends AbstractInputAction {
	private SceneNode				avatar;
	private Direction				direction;					// Determines which direction to move.
	private SetSpeedAction			runAction;
	private CircuitShooter			cs;
	private CircuitShooterClient	client;
	
	private float			speed			= 0.1f;
	private float			idleConstant	= 0.65f;	// Constant indicating the threshold of an idle axis value.
														
	public MoveNodeAction(SceneNode n, Direction d, SetSpeedAction r, CircuitShooter cs) {
		avatar = n;
		direction = d;
		runAction = r;
		client = cs.getClient();
		this.cs = cs;
	}
	
	/**
	 * Finds the speed and direction of this instance of a movement.
	 *
	 * @param e - The event information detailing the controller type.
	 */
	private void findSpeedAndDirection(Event e, float t) {
		float componentValue = e.getValue(); // The value of the component.
		
		boolean isXAxis = e.getComponent().getName().contains("X Axis"); // Determines if the controller is on the X axis.
		boolean isYAxis = e.getComponent().getName().contains("Y Axis"); // Determines if the controller is on the Y axis.
		
		float mult = 1;
		
		if(isXAxis || isYAxis){
			mult = .25f;
		}
		
		// Determine if the player is running.
		if (runAction.isRunning() && runAction.getPlayer() == this.avatar) {
			speed = .4f *  mult;
		} else {
			speed = .25f * mult;
		}
		
		if (isXAxis) {
			if (componentValue < -idleConstant) {
				direction = Direction.LEFT;
			} else if (componentValue > idleConstant) {
				direction = Direction.RIGHT;
				speed= - speed;
			} else {
				direction = Direction.IDLE;
			}
		} else if (isYAxis) {
			if (componentValue < -idleConstant) {
				direction = Direction.FORWARD;
			} else if (componentValue > idleConstant) {
				direction = Direction.BACKWARD;
				speed= - speed;
			} else {
				direction = Direction.IDLE;
			}
		} else {
			switch (direction) {
				case BACKWARD:
					speed = -speed;
					break;
				case RIGHT:
					speed = -speed;
					break;
				default:
			}
		}
		
		switch (direction) {
			case FORWARD:
				moveDirection(Direction.FORWARD, t, this.speed);
				break;
			case BACKWARD:
				moveDirection(Direction.BACKWARD, t, this.speed);
				break;
			case LEFT:
				moveDirection(Direction.LEFT, t, this.speed);
				break;
			case RIGHT:
				moveDirection(Direction.RIGHT, t, this.speed);
				break;
			default:
				break;
		}
	}
	
	/**
	 * This method handles the actual logic for moving in a specified direction.
	 *
	 * @param dir 			- The direction.
	 * @param negativeAxis 	- Is the direction negative?
	 * @param moveVal 		- The distance traveled.
	 * @param timeVal		- The amount of time elapsed.
	 * @return - A Vector3D that contains the new location of the camera.
	 */
	private void moveDirection(	Direction d, float t, float s) {
		Matrix3D rot = avatar.getLocalRotation();
		Vector3D dir;
		
		if (d == Direction.FORWARD || d == Direction.BACKWARD) {
			dir = new Vector3D(0, 0, 1);
		} else {
			dir = new Vector3D(1, 0, 0);
		}
				
		if (d != Direction.IDLE) {
			dir = dir.mult(rot);
			dir.scale(s * t);
			
			if (isValidPosition(dir.getX(), dir.getZ())) {
				avatar.translate((float) dir.getX(), (float) dir.getY(), (float) dir.getZ());
				
				System.out.println(avatar.getLocalTranslation().getCol(3));
				
				if (client != null) {
					client.getOutputHandler().sendMoveMsg(avatar.getLocalTranslation().getCol(3));
				}
			}
		}
	}
	
	
	/*
	 * Starts logic to move the avatar.
	 */
	public void performAction(float time, Event e) {
		findSpeedAndDirection(e, time);
	}
	
	private boolean isValidPosition(double x, double z) {
		Vector3D position = avatar.getLocalTranslation().getCol(3);
		
		System.out.println("New +X: " + (position.getX() + x) + "\tNew -X: " + (position.getX() - x) + "\tNew +Z: "+ (position.getZ() + z) + "\tNew -Z: " + (position.getZ() - z));
		
		if (position.getX() + x >= cs.xBound || position.getX() - x <= -cs.xBound || position.getZ() + z >= cs.yBound || position.getZ() - z <= -cs.yBound)
			return false;
		else 
			return true;
	}
}
