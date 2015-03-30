package engine.scene.controller;

import sage.scene.Controller;
import sage.scene.SceneNode;

/**
 * This SceneNode controller toggles between downscaling and upscaling the attached SceneNodes.
 * @author Kevin
 *
 */
public class ScaleController extends Controller {
	float scaleAmount;
	float upScaleConstant;
	float downScaleConstant;
	int scaleCount = 0;
	boolean isUpscaling;
 
	@Override
	public void update( double scaleConstant ) {
		this.upScaleConstant = (float) scaleConstant;
		this.downScaleConstant = 1/this.upScaleConstant;

		// Check if we are upscaling or downscaling.
		if(isUpscaling){
			scaleAmount = upScaleConstant;
		} else {
			scaleAmount = downScaleConstant;
		}
		scaleCount = scaleCount + 1;
		
		// Flip scaling.
		if(scaleCount >= 40){
			scaleCount = 0;
			isUpscaling = !isUpscaling;
		}

		// Scale all attached SceneNodes.
		for(SceneNode n : this.controlledNodes){
			n.scale(scaleAmount, scaleAmount, scaleAmount);	
		}
	}

}
