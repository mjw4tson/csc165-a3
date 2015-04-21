package engine.scene.physics;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.physics.PhysicsEngineFactory;
import sage.scene.SceneNode;

/**
 * Helper class that encapsulates the operations of the physics engine.
 * 
 * @author ktajeran
 */
public class PhysicsManager {
	
	private IPhysicsEngine	physicsEngine;
	private IPhysicsObject	playerPObject;
	
	private String			ENGINE	= "sage.physics.JBullet.JBulletPhysicsEngine";
	// Determines if the physics engine is enabled.
	private boolean			physicsEngineEnabled = false; 
	
	// Determines if physics are enabled.
	private boolean			pEnabled = false;
	
	public PhysicsManager(SceneNode localPlayer) {
		if(physicsEngineEnabled){
			System.out.println("Initializing the SAGE JBullet Physics Engine");
			physicsEngine = PhysicsEngineFactory.createPhysicsEngine(ENGINE);
			physicsEngine.initSystem();
			float[] gravity = { 0, -1f, 0 };
			physicsEngine.setGravity(gravity);
		} else {
			System.out.println("Skipping initiaization of the physics engine");
		}
	}
	
	public void updatePhysicsState(Iterable<SceneNode>  sceneNodes){
		if (pEnabled && physicsEngineEnabled) {
			Matrix3D mat;
			Vector3D translateVec;
			physicsEngine.update(20.0f);
			for (SceneNode s : sceneNodes) {
				if (s.getPhysicsObject() != null) {
					mat = new Matrix3D(s.getPhysicsObject().getTransform());
					translateVec = mat.getCol(3);
					s.getLocalTranslation().setCol(3, translateVec);
					// should also get and apply rotation
				}
			}
		}
		
	}
	
	/**
	 * Method to apply physics properties to a specified SceneNode.
	 * 
	 * @param object
	 *            - The SceneNode to apply physics to
	 * @param mass
	 *            - The mass of the SceneNode object
	 * @param pObject
	 *            - The container to keep the physics properties of the object.
	 */
	public IPhysicsObject bindPhysicsProperty(	SceneNode object,
												float mass) {
		IPhysicsObject pObject;
		pObject = physicsEngine.addSphereObject(physicsEngine.nextUID(), mass, object
				.getWorldTransform().getValues(), 1.0f);
		pObject.setBounciness(1.0f);
		object.setPhysicsObject(pObject);
		return pObject;
	}
	
	/**
	 * Binds the specified floor object with physics properties.
	 * 
	 * @param floorObject
	 * @return - The physics object for the specified floor SceneNode.
	 */
	public IPhysicsObject bindFloorPhysics(SceneNode floorObject) {
		// Apply physics properties to the world floor
		IPhysicsObject worldFloor;
		float up[] = { -0.05f, 0.95f, 0 }; // {0,1,0} is flat
		worldFloor = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(), floorObject
				.getWorldTransform().getValues(), up, 0.0f);
		worldFloor.setBounciness(1.0f);
		floorObject.setPhysicsObject(worldFloor);
		return worldFloor;
	}
	
	public boolean isPhysicsEngineEnabled() {
		return physicsEngineEnabled;
	}
	
}
