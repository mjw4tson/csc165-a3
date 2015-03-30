package engine.input.action.camera;

import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;

/**
 * Camera action for zooming in and out of the 3P orbit camera.
 * @author Kevin
 *
 */
public class ZoomAction extends AbstractInputAction{
	private Camera3PController theController;
	private boolean zoomOut;
	private float magnitude;
	
	public ZoomAction(Camera3PController cc, boolean zoomOut, float magnitude){
		this.theController = cc;
		this.zoomOut = zoomOut;
		this.magnitude = magnitude;
	}

	@Override
	public void performAction(	float time,
								Event event ) {
		if(zoomOut){
			theController.zoomOut(magnitude);
		} else {
			theController.zoomIn(magnitude);
		}
		
	}
	
}
