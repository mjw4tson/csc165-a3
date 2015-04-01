var JavaPackages = new JavaImporter(
 Packages.sage.scene.Group,
 Packages.sage.scene.shape.Pyramid,
 Packages.sage.scene.shape.Cube,
 Packages.graphicslib3D.Point3D);
with (JavaPackages){
	var cubeGroup = new Group();
	var pyramidGroup = new Group();
	var origin = 65;
	
	// Add a pyramid into the game world.
	var pyr1 = new Pyramid(); 
	pyr1.translate(95, .3, 30 + origin);
		
	// Add a pyramid into the game world.
	var pyr2 = new Pyramid(); 
	pyr2.translate(40, .3, 68 + origin);
		
	// Add a pyramid into the game world.
	var pyr3 = new Pyramid(); 
	pyr3.translate(50, .3, 30 + origin);
		
	// Add a pyramid into the game world.
	var pyr4 = new Pyramid(); 
	pyr4.translate(80, .3, 30 + origin);
		
	// Add a cube into the game world.
	var cu1 = new Cube();
	cu1.translate(37, .3, 30 + origin);
		
	// Add a cube into the game world.
	var cu2 = new Cube();
	cu2.translate(60, .3, 75 + origin);
		
	// Add a cube into the game world.
	var cu3 = new Cube();
	cu3.translate(56, .3, 45 + origin);
		
	// Add a cube into the game world.
	var cu4 = new Cube();
	cu4.translate(75, .3, 25 + origin);
		
	// Add a cube into the game world.
	var cu5 = new Cube();
	cu5.translate(20, .3, 5 + origin);
		
	// Populate the Cube Group
	cubeGroup.addChild(cu1);
	cubeGroup.addChild(cu2);
	cubeGroup.addChild(cu3);
	cubeGroup.addChild(cu4);
	cubeGroup.addChild(cu5);
		
	// Populate the Pyramid Group
	pyramidGroup.addChild(pyr1);
	pyramidGroup.addChild(pyr2);
	pyramidGroup.addChild(pyr3);
	pyramidGroup.addChild(pyr4);
}