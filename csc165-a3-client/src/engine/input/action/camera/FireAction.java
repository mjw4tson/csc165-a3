package engine.input.action.camera;

import games.treasurehunt2015.TreasureHunt;
import net.java.games.input.Event;
import sage.audio.Sound;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;

/**
 * IAction for firing.
 *
 * @author ktajeran
 *
 */
public class FireAction extends AbstractInputAction {
	private TreasureHunt	th;

	public FireAction( TreasureHunt t ) {
		this.th = t;
	}

	/*
	 * This action toggles the state that determines whether or not the camera is locked.
	 */
	public void performAction( float time , Event event ) {
		th.playFireSound();
	}
}
