package engine.scene.shape;

import engine.event.CrashEvent;
import sage.event.IEventListener;
import sage.event.IGameEvent;
import sage.scene.shape.Cube;

/**
 * A class for the TreasureChest, has event handling for growing the chest.
 * 
 * @author Kevin
 *
 */
public class TreasureChest extends Cube implements IEventListener {

	/* 
	 * Handles collision detection. 
	 */
	public boolean handleEvent( IGameEvent event ) { 
		
		CrashEvent collision = (CrashEvent) event;
		
		int crashCount = collision.getWhichCrash();
		if ( crashCount % 2 == 0 )
			this.scale(1.3f, 1.3f, 1.3f);
		return true;
	}

}
