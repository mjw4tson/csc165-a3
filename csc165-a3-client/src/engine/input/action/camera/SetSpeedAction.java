package engine.input.action.camera;

import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;
import engine.objects.Avatar;

/**
 * IAction for toggling sprinting on the camera world.
 *
 * @author ktajeran
 *
 */
public class SetSpeedAction extends AbstractInputAction {
	private boolean		running	= false;
	private Avatar	player;

	public SetSpeedAction(Avatar n) {
		player = n;
	}

	/**
	 * Getter to determine if the player is in a running state.
	 *
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Returns the player of this speed action.
	 *
	 * @return
	 */
	public Avatar getPlayer() {
		return player;
	}

	/*
	 * This action toggles the state that determines whether or not the camera is running.
	 */
	public void performAction( float time , Event event ) {
		System.out.println("Changing player run state");
		running = !running;
	}
}
