package engine.objects;

import java.util.UUID;

import sage.physics.IPhysicsObject;
import sage.scene.TriMesh;

public class Avatar {
	private UUID			uuid;
	private float			health;
	private IPhysicsObject	physicsObject;
	private TriMesh triMesh;
	
	public Avatar(String name, TriMesh triMesh) {
		this.triMesh = triMesh;
		this.health = 100.0f;
		uuid = UUID.randomUUID();
	}
	
	public float getHealth() {
		return health;
	}
	
	/**
	 * Set health, cap at 100.
	 * 
	 * @param health
	 */
	public void setHealth(float health) {
		this.health = health;
		if (this.health > 100.0f) {
			this.health = 100.0f;
		}
	}

	protected Avatar() {
		
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public IPhysicsObject getPhysicsObject() {
		return physicsObject;
	}

	public void setPhysicsObject(IPhysicsObject physicsObject) {
		this.physicsObject = physicsObject;
	}
	
	public TriMesh getTriMesh() {
		return triMesh;
	}
}
