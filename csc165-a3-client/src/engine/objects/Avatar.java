package engine.objects;

import java.awt.Color;
import java.util.UUID;

import sage.scene.shape.Sphere;

public class Avatar extends Sphere {
	private UUID uuid;
	
	protected Avatar() {}
	
	public Avatar(String name, double radius, int stacks, int slices, Color color) {
		super(name, radius, stacks, slices, color);
		
		uuid = UUID.randomUUID();
	}
	
	public UUID getUUID() {
		return uuid;
	}
}
