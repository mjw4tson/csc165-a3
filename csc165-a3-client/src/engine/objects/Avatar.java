package engine.objects;

import java.util.UUID;

import sage.event.IEventListener;
import sage.event.IGameEvent;
import sage.physics.IPhysicsObject;
import sage.scene.Model3DTriMesh;
import engine.event.CrashEvent;

public class Avatar implements IEventListener {
	private UUID			uuid;
	private float			health;
	private IPhysicsObject	physicsObject;
	private Model3DTriMesh	triMesh;
	private int totalKills = 0;
	
	public Avatar(String name, Model3DTriMesh triMesh) {
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
	
	public Model3DTriMesh getTriMesh() {
		return triMesh;
	}
	
	@Override
	public boolean handleEvent(IGameEvent event) {
		CrashEvent cevent = (CrashEvent) event;
		int crashCount = cevent.getWhichCrash();
		if (crashCount % 2 == 0){
			this.setHealth(100);
			System.out.println("Obtained a health pack, player health is now at 100HP");
			
		}else{

		}
			
		return true;
	}

	public int getTotalKills() {
		return totalKills;
	}

	public boolean isDead(){
		if(this.health <= 0){
			return true;
		} else {
			return false;
		}
	}

	public void setTotalKills(int totalKills) {
		this.totalKills = totalKills;
	}
}
