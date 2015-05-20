package engine.objects;

import java.util.UUID;

import sage.event.IEventListener;
import sage.event.IGameEvent;
import sage.physics.IPhysicsObject;
import sage.scene.Model3DTriMesh;
import engine.event.CrashEvent;
import games.circuitshooter.CircuitShooter;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

public class Avatar implements IEventListener {
	private UUID			uuid;
	private CircuitShooter	cs;
	private float			health;
	private IPhysicsObject	physicsObject;
	private Model3DTriMesh	triMesh;
	private int				totalKills	= 0;
	
	public Avatar(String name, Model3DTriMesh triMesh, CircuitShooter cs, UUID id) {
		this.triMesh = triMesh;
		this.health = 100.0f;
		
		if (id == null)
			uuid = UUID.randomUUID();
		else
			uuid = id;
		
		this.cs = cs;
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
		
		if (crashCount % 2 == 0) {
			this.setHealth(100);
			System.out.println("Obtained a health pack, player health is now at 100HP");
		}
		
		return true;
	}
	
	public int getTotalKills() {
		return totalKills;
	}
	
	public boolean isDead() {
		return health <= 0 ? true : false;
	}
	
	public void incrementKills() {
		totalKills++;
	}
	
	public void setTotalKills(int totalKills) {
		this.totalKills = totalKills;
	}
	
	public void respawn() {
		cs.playDead();
		setHealth(100);
		
		Point3D locP1 = new Point3D(getLocation());
		Vector3D newLoc = new Vector3D(cs.getRandomSignedInteger(1200), locP1.getY(), cs.getRandomSignedInteger(600));
		getTriMesh().translate((float) (newLoc.getX() - locP1.getX()), 0,  (float) (newLoc.getZ() - locP1.getZ()));
		
		if (cs.getClient() != null) {
			cs.getClient().getOutputHandler().sendMoveMsg(getLocation());
		}
	}
	
	public Vector3D getLocation() {
		return getTriMesh().getLocalTranslation().getCol(3);
	}
}
