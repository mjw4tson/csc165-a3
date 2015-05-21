package engine.objects;

import engine.event.CrashEvent;
import games.circuitshooter.CircuitShooter;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.util.UUID;

import sage.audio.Sound;
import sage.audio.SoundType;
import sage.event.IEventListener;
import sage.event.IGameEvent;
import sage.physics.IPhysicsObject;
import sage.scene.Model3DTriMesh;

public class Avatar implements IEventListener {
	private UUID			uuid;
	private CircuitShooter	cs;
	private float			health;
	private IPhysicsObject	physicsObject;
	private Model3DTriMesh	triMesh;
	private int				totalKills	= 0;
	private Sound			hit;
	private Sound			fire;
	private Sound			dead;
	private Sound			pickUp;
	
	protected Avatar() {}
	
	public Avatar(String name, Model3DTriMesh triMesh, CircuitShooter cs, UUID id) {
		this.triMesh = triMesh;
		this.health = 100.0f;
		
		if (id == null)
			uuid = UUID.randomUUID();
		else
			uuid = id;
		
		this.cs = cs;
		
		setupSound();
	}
	
	/**
	 * Avatar Attribute Methods
	 */
	public UUID getUUID() {
		return uuid;
	}
	
	public float getHealth() {
		return health;
	}
	
	public void setHealth(float health) {
		this.health = health;
		
		if (this.health > 100.0f) {
			this.health = 100.0f;
		}
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
		playDead();
		
		if (this == cs.localPlayer) {
			setHealth(100);
			
			Point3D locP1 = new Point3D(getLocation());
			Vector3D newLoc = new Vector3D(cs.getRandomSignedInteger(1200), locP1.getY(), cs.getRandomSignedInteger(600));
			triMesh.translate((float) (newLoc.getX() - locP1.getX()), 0,  (float) (newLoc.getZ() - locP1.getZ()));
			
			if (cs.getClient() != null) {
				cs.getClient().getOutputHandler().sendMoveMsg(getLocation());
			}
		}
	}

	/**
	 * Model Methods
	 */
	public Model3DTriMesh getTriMesh() {
		return triMesh;
	}
	
	/**
	 * Physics Methods
	 */
	public IPhysicsObject getPhysicsObject() {
		return physicsObject;
	}
	
	public void setPhysicsObject(IPhysicsObject physicsObject) {
		this.physicsObject = physicsObject;
	}
	
	@Override
	public boolean handleEvent(IGameEvent event) {
		CrashEvent cevent = (CrashEvent) event;
		int crashCount = cevent.getWhichCrash();
		
		if (crashCount % 2 == 0) {
			setHealth(100);
			playPickUp();
			System.out.println("Obtained a health pack, player health is now at 100HP");
		}
		
		return true;
	}
	
	public Vector3D getLocation() {
		return triMesh.getLocalTranslation().getCol(3);
	}
	
	
	/**
	 * Sound Related Methods
	 */
	private void setupSound() {
		pickUp = new Sound(cs.getPickUpResource(), SoundType.SOUND_EFFECT, 5, false);
		fire = new Sound(cs.getFireResource(), SoundType.SOUND_EFFECT, 5, false);
		dead = new Sound(cs.getDeadResource(), SoundType.SOUND_EFFECT, 5, false);
		hit = new Sound(cs.getHitResource(), SoundType.SOUND_EFFECT, 5, false);

		pickUp.initialize(cs.getAudioManager());
		fire.initialize(cs.getAudioManager());
		dead.initialize(cs.getAudioManager());
		hit.initialize(cs.getAudioManager());
		
		pickUp.setMaxDistance(300.0f);
		pickUp.setMinDistance(3.0f);
		pickUp.setRollOff(1.0f);
		
		hit.setMaxDistance(500.0f);
		hit.setMinDistance(3.0f);
		hit.setRollOff(1.0f);
		
		fire.setMaxDistance(500.0f);
		fire.setMinDistance(3.0f);
		fire.setRollOff(1.0f);
		
		dead.setMaxDistance(2000.0f);
		dead.setMinDistance(1.0f);
		dead.setRollOff(1.0f);
	}
	
	public void updateSoundLocation(Sound sound) {
		Point3D newLoc = new Point3D(getLocation().getX(), getLocation().getY(), getLocation().getZ());
		sound.setLocation(newLoc);
	}
	
	public void playFire() {
		System.out.println("Playing fire!");
		System.out.println("Old Location: " + fire.getLocation());
		updateSoundLocation(fire);
		System.out.println("New Location: " + fire.getLocation());
		fire.play();
	}
	
	public void playHit() {
		System.out.println("Playing hit!");
		updateSoundLocation(hit);
		hit.play();
	}
	
	public void playDead() {
		System.out.println("Playing dead!");
		updateSoundLocation(dead);
		dead.play();
	}
	
	public void playPickUp() {
		System.out.println("Playing Pickup!");
		updateSoundLocation(pickUp);
		pickUp.play();
	}
}
