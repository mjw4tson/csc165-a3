package engine.scene.physics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import engine.objects.Avatar;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.physics.PhysicsEngineFactory;
import sage.scene.Group;
import sage.scene.SceneNode;
import sage.scene.TriMesh;

/**
 * Helper class that encapsulates the operations of the physics engine.
 * 
 * @author ktajeran
 */
public class PhysicsManager {
	
	private IPhysicsEngine	physicsEngine;
	
	private String			ENGINE	= "sage.physics.JBullet.JBulletPhysicsEngine";
	// Determines if the physics engine is enabled.
	private boolean			physicsEngineEnabled = true; 
	
	// Determines if physics are enabled.
	private boolean			pEnabled = true;
	
	public PhysicsManager(SceneNode localPlayer) {
		if(physicsEngineEnabled){
			System.out.println("Initializing the SAGE JBullet Physics Engine");
			physicsEngine = PhysicsEngineFactory.createPhysicsEngine(ENGINE);
			physicsEngine.initSystem();
			float[] gravity = { 0, -4f, 0 };
			physicsEngine.setGravity(gravity);
		} else {
			System.out.println("Skipping initiaization of the physics engine");
		}
	}
	
	public void updatePhysicsState(Iterable<SceneNode> sceneNodes){
		if (pEnabled && physicsEngineEnabled) {
			Matrix3D mat;
			Vector3D translateVec;
			physicsEngine.update(20.0f);
			for (SceneNode s : sceneNodes) {
				if(s instanceof Group){
					Iterator<SceneNode> i = ((Group) s).getChildren();
					while(i.hasNext()){
						SceneNode n = i.next();
						if(n.getPhysicsObject() != null){
							System.out.println(n.getName());
							mat = new Matrix3D(n.getPhysicsObject().getTransform());
							translateVec = mat.getCol(3);
							n.getLocalTranslation().setCol(3, translateVec);
						}
						
					}
					
					
				} else if (s.getPhysicsObject() != null && s instanceof Avatar == false) {
					mat = new Matrix3D(s.getPhysicsObject().getTransform());
					translateVec = mat.getCol(3);
					s.getLocalTranslation().setCol(3, translateVec);
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
		pObject.setBounciness(.5f);
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
		float up[] = { 0f, 1f, 0 }; // {0,1,0} is flat
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
