package engine.input.action.camera;

import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;
import sage.terrain.TerrainBlock;
import engine.input.action.camera.MoveAction.Direction;
import games.treasurehunt2015.TreasureHunt;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

public class MoveNodeAction extends AbstractInputAction {
	private SceneNode		avatar;
	private Direction		direction;					// Determines which direction to move.
	private SetSpeedAction	runAction;
	private TerrainBlock	terrain;
	private TreasureHunt	bg;
	
	private float			speed			= 0.03f;
	private float			idleConstant	= 0.65f;	// Constant indicating the threshold of an idle axis value.
														
	public MoveNodeAction(SceneNode n, Direction d, SetSpeedAction r, TerrainBlock terrainBlock, TreasureHunt bg) {
		avatar = n;
		direction = d;
		runAction = r;
		terrain = terrainBlock;
		this.bg = bg;
	}
	
	/**
	 * Finds the speed and direction of this instance of a movement.
	 *
	 * @param e
	 *            - The event information detailing the controller type.
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
			speed = .087f *  mult;
		} else {
			speed = .010f * mult;
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
	 * @param dir
	 *            - The direction.
	 * @param negativeAxis
	 *            - Is the direction negative?
	 * @param moveVal
	 *            - The distance traveled.
	 * @param timeVal
	 *            - The amount of time elapsed.
	 * @return - A Vector3D that contains the new location of the camera.
	 */
	private void moveDirection(	Direction d,
								float t,
								float s) {
		Matrix3D rot = avatar.getLocalRotation();
		Vector3D dir;
		
		if (d == Direction.FORWARD || d == Direction.BACKWARD) {
			dir = new Vector3D(0, 0, 1);
		} else {
			dir = new Vector3D(1, 0, 0);
		}
				
		if (d == Direction.IDLE) {
			
		} else {
			dir = dir.mult(rot);
			dir.scale(s * t);
			avatar.translate((float) dir.getX(), (float) dir.getY(), (float) dir.getZ());
			updateVerticalPosition();
		}
		
	}
	
	/**
	 * Update vertical position based upon the terrain block.
	 */
	private void updateVerticalPosition() {
		Point3D avLoc = new Point3D(avatar.getLocalTranslation().getCol(3));
		float x = (float) avLoc.getX();
		float z = (float) avLoc.getZ();
		float terHeight = terrain.getHeightFromWorld(new Point3D(x,0,z));
		float desiredHeight = terHeight + (float) terrain.getOrigin().getY() + 1.5f;
		if (Float.isNaN(desiredHeight)) {
			
		} else {
			avatar.getLocalTranslation().setElementAt(1, 3, desiredHeight);
		}
		
		if (bg.getClient() != null) {
			bg.getClient().sendMoveMsg(bg.getPlayerPosition());
		}
	}
	
	/*
	 * Starts logic to move the avatar.
	 */
	public void performAction(	float time,
								Event e) {
		findSpeedAndDirection(e, time);
	}
}
