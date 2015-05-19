var JavaPackages = new JavaImporter(
 Packages.sage.scene.Group,
 Packages.sage.scene.shape.Cube,
 Packages.sage.scene.shape.Rectangle,
 Packages.graphicslib3D.Vector3D,
 Packages.graphicslib3D.Point3D);
with (JavaPackages){
	var boundaryGroup = new Group();
	var origin = 65;
	
	// Set the wall boundaries
	var xBound = 1300;
	var yBound = 700;
	
	// Adds the game boundaries to the world.
	var cu1 = new Rectangle("1300",2600, 120);
	cu1.translate(0, 1, 700);
	
	// Adds the game boundaries to the world.
	var cu2 = new Rectangle("1300",2600, 120);
	cu2.translate(0, 1, -700);
		
	// Adds the game boundaries to the world.
	var cu3 = new Rectangle("E Shorter Wall",1400, 120);
	cu3.rotate(90, new Vector3D(0, 1, 0));
	cu3.translate(-1300, 1, 0);
	
	// Adds the game boundaries to the world.
	var cu4 = new Rectangle("W Shorter Wall",1400, 120);
	cu4.rotate(90, new Vector3D(0, 1, 0));
	cu4.translate(1300, 1, 0);

	boundaryGroup.addChild(cu1);
	boundaryGroup.addChild(cu2);
	boundaryGroup.addChild(cu3);
	boundaryGroup.addChild(cu4);
	
}