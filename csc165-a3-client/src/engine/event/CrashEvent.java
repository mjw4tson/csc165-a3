package engine.event;

import sage.event.AbstractGameEvent;

/**
 * An abstract game event that handles collision between game world nodes.
 * 
 * @author ktajeran
 *
 */
public class CrashEvent extends AbstractGameEvent {
	private int	whichCrash;

	/**
	 * Constructor for CrashEvent.
	 * 
	 * @param n
	 *            - The crashed item.
	 */
	public CrashEvent( int n ) {
		whichCrash = n;
	}

	/**
	 * Getter which returns which object collided.
	 * 
	 * @return - Which node crashed.
	 */
	public int getWhichCrash() {
		return whichCrash;
	}

}
