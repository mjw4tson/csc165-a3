package engine.input.action.camera;

import engine.objects.Avatar;
import games.circuitshooter.CircuitShooter;
import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;

/**
 * IAction for firing.
 *
 * @author ktajeran
 *
 */
public class FireAction extends AbstractInputAction {
	private static long FIRE_RATE = 500; // 500 = 2 shots/second
	private static long LAST_FIRE_TIME;
	
	private Avatar avatar;
	private CircuitShooter	th;

	public FireAction(CircuitShooter th, Avatar avatar) {
		this.avatar = avatar;
		this.th = th;
		LAST_FIRE_TIME = System.currentTimeMillis();
	}

	/**
	 * This action fires a projectile from the avatar in the direction it's facing.
	 */
	public void performAction( float time , Event event ) {
		long currentTime = System.currentTimeMillis();

		if (currentTime - LAST_FIRE_TIME > FIRE_RATE) {
			th.fire(avatar);
			LAST_FIRE_TIME = currentTime;
		}
	}
}
