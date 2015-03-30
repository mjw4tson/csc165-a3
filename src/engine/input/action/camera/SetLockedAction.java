package engine.input.action.camera;

import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;

/**
 * IAction for toggling of locking the avatar with the Camera3PController.
 *
 * @author ktajeran
 *
 */
public class SetLockedAction extends AbstractInputAction {
	private boolean		locked	= false;
	private SceneNode	player;

	public SetLockedAction( SceneNode n ) {
		player = n;
	}

	/**
	 * Getter to determine if the player is in a locked state.
	 *
	 * @return
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * Returns the player of this speed action.
	 *
	 * @return
	 */
	public SceneNode getPlayer() {
		return player;
	}

	/*
	 * This action toggles the state that determines whether or not the camera is locked.
	 */
	public void performAction( float time , Event event ) {
		System.out.println("Changing player locked state");
		locked = !locked;
	}
}
