package engine.input.action.camera;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.camera.ICamera;
import sage.scene.SceneNode;
import sage.util.MathUtils;

/**
 * Camera class for an Orbit controller.
 *
 * @author Kevin
 */
public class Camera3PController {
	private ICamera		cam;
	private SceneNode	target;
	private float		cameraAzimuth;
	private float		cameraElevation;
	private float		cameraDistanceFromTarget;
	private Point3D		targetPos;
	private Vector3D	worldUpVec;

	/**
	 * Constructor for the Camera3PController
	 *
	 * @param cam
	 *            - The camera
	 * @param target
	 *            - The target the camera looks at
	 * @param inputMgr
	 *            - The input manager.
	 * @param controllerName
	 *            - The name of the controller.
	 */
	public Camera3PController(ICamera cam, SceneNode target) {
		this.cam = cam;
		this.target = target;
		worldUpVec = new Vector3D(0, 1, 0);
		cameraDistanceFromTarget = 58.0f;
		cameraAzimuth = 180;
		cameraElevation = 20.f;
		update(0.0f);
	}
	
	/**
	 * Zooms the camera out.
	 * @param magnitude
	 */
	public void zoomOut(float magnitude){
		this.cameraDistanceFromTarget = this.cameraDistanceFromTarget + 1 * magnitude;
	}
	
	/**
	 * Zooms the camera in, will not zoom any further if the distance is below 0.
	 * @param magnitude
	 */
	public void zoomIn(float magnitude){
		float value = this.cameraDistanceFromTarget - 1 * magnitude;
		if(value >= .00001f){
			this.cameraDistanceFromTarget = this.cameraDistanceFromTarget - 1 * magnitude;
		} else {
			this.cameraDistanceFromTarget = .00001f;
		}
	}

	/**
	 * Returns the target of the camera.
	 *
	 * @return
	 */
	public SceneNode getNode() {
		return target;
	}

	/**
	 * Sets the SceneNode.
	 *
	 * @param n
	 */
	public void setSceneNode(SceneNode n) {
		this.target = n;
	}

	/**
	 * Updates the camera's location.
	 *
	 * @param time
	 */
	public void update(float time) {
		updateTarget();
		updateCameraPosition();
		cam.lookAt(targetPos, worldUpVec);
	}

	/**
	 * Updates the target position.
	 */
	private void updateTarget() {
		targetPos = new Point3D(target.getWorldTransform().getCol(3));
	}

	/**
	 * Updates the camera's position.
	 */
	private void updateCameraPosition() {
		double theta = cameraAzimuth;
		double phi = cameraElevation;
		double r = cameraDistanceFromTarget;

		// Calculates the new camera position in cartesian coordinates
		Point3D relativePosition = MathUtils.sphericalToCartesian(theta, phi, r);
		Point3D desiredCameraLoc = relativePosition.add(targetPos);
		cam.setLocation(desiredCameraLoc);
	}


	public float getCameraAzimuth() {
		return this.cameraAzimuth;
	}

	public void setCameraAzimuth(float cameraAzimuth) {
		this.cameraAzimuth = cameraAzimuth;
	}

	public float getCameraElevation() {
		return cameraElevation;
	}

	public void setCameraElevation(float cameraElevation) {
		this.cameraElevation = cameraElevation;
	}

}
