package engine.objects;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.awt.Color;
import java.util.UUID;

import sage.physics.IPhysicsObject;
import sage.scene.shape.Sphere;

public class Avatar extends Sphere {
	private UUID			uuid;
	private float			health;
	private boolean			isJumping;
	private IPhysicsObject	physicsObject;
	
	public Avatar(String name, double radius, int stacks, int slices, Color color) {
		super(name, radius, stacks, slices, color);
		this.health = 100.0f;
		uuid = UUID.randomUUID();
		this.isJumping = false;
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
	
	public boolean isJumping() {
		return isJumping;
	}
	
	public void setJumping(boolean isJumping) {
		this.isJumping = isJumping;
	}

	public IPhysicsObject getPhysicsObject() {
		return physicsObject;
	}

	public void setPhysicsObject(IPhysicsObject physicsObject) {
		this.physicsObject = physicsObject;
	}
}
