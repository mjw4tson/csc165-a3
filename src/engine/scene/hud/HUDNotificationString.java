package engine.scene.hud;

import java.util.Random;

import sage.app.BaseGame;
import sage.scene.HUDString;

/**
 * A simple class that denotes a HUDString as a "temporary" notification string.
 * 
 * @author Kevin
 *
 */
public class HUDNotificationString extends HUDString {

	private float	timeToLive	= 0.0f;		// The time that the HUD string is "alive" for.
	private double	swayConstant;				// The magnitude of how much the hud notification "falls down" on the screen.
	private Random	rand		= new Random();

	public HUDNotificationString( String text, double sc, float ttl ) {
		super(text);
		swayConstant = sc;
		timeToLive = ttl;
	}

	public float getTimeToLive() {
		return timeToLive;
	}

	/**
	 * Determines if the HUDNotificationString is still displayed on the window.
	 * @return
	 */
	public boolean isDead() {
		if ( this.timeToLive <= 0.0f )
			return true;
		return false;
	}

	/**
	 * Decrement the TTL value and sway the string down if specified.
	 */
	public void setTimeToLive() {
		double temp = swayConstant;

		// Random horizontal sway
		if ( rand.nextDouble() > 0.5 ) {
			temp = -temp * 2;
		}
		this.timeToLive = this.timeToLive - 0.005f;
		this.setLocation(this.getLocation().getX() + temp, this.getLocation().getY() - swayConstant);

	}
}
