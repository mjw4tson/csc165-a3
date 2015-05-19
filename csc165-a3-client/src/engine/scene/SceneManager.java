package engine.scene;

import java.awt.Color;
import java.io.File;
import java.util.Iterator;
import java.util.Random;

import sage.app.BaseGame;
import sage.display.IDisplaySystem;
import sage.model.loader.OBJLoader;
import sage.model.loader.ogreXML.OgreXMLParser;
import sage.scene.Group;
import sage.scene.Model3DTriMesh;
import sage.scene.RotationController;
import sage.scene.SceneNode;
import sage.scene.SceneNode.CULL_MODE;
import sage.scene.SceneNode.RENDER_MODE;
import sage.scene.SkyBox;
import sage.scene.SkyBox.Face;
import sage.scene.TriMesh;
import sage.scene.shape.Cube;
import sage.scene.shape.Pyramid;
import sage.scene.shape.Rectangle;
import sage.scene.shape.Sphere;
import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.HillHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.Texture.WrapMode;
import sage.texture.TextureManager;
import engine.event.CrashEvent;
import engine.scene.controller.ScaleController;
import engine.scene.physics.PhysicsManager;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

/**
 * Helper class tasked with managing the state of the game world.
 * 
 * @author ktajeran
 */
public class SceneManager {
	
	private String			directory;
	private String			dirEnvironment	= "images" + File.separator + "environment"
													+ File.separator;
	private String			dirModel		= "images" + File.separator + "models" + File.separator;
	
	// Texture Objects
	private Texture			skyBoxTextureTop;
	private Texture			skyBoxTextureBack;
	private Texture			skyBoxTextureEast;
	private Texture			skyBoxTextureWest;
	private Texture			skyBoxTextureFront;
	private Texture			skyBoxTextureBot;
	private Texture			groundTexture;
	private Texture			ammoBoxTexture;
	private Texture			avatarTexture;
	private Texture			sandTexture;
	private Texture			fenceTexture;
	private Texture			medicTexture;
	private Texture			wallTexture;
	
	// Game World SceneNodes
	private SkyBox			skyBox;
	private Rectangle		floor;
	private TerrainBlock	hillTerrain;
	
	// Modeling
	private OBJLoader		loader			= new OBJLoader();
	
	//Other
	private Random			r				= new Random();
	
	public SceneManager(String dir) {
		directory = dir;
		setupTextures();
	}
	
	private void setupTextures() {
		// Texture file locations
		String skyFront = directory + dirEnvironment + "sky.jpg";
		String skyEast = directory + dirEnvironment + "sky.jpg";
		String skyWest = directory + dirEnvironment + "sky.jpg";
		String skyBot = directory + dirEnvironment + "sky.jpg";
		String skyTop = directory + dirEnvironment + "sky.jpg";
		String skyBack = directory + dirEnvironment + "sky.jpg";
		String ground = directory + dirEnvironment + "ground.jpg";
		String ammoBox = directory + dirModel + "ammo.png";
		String avatar = directory + dirModel + "avatar.png";
		String sand = directory + dirEnvironment + "ground.jpg";
		String fence = directory + dirModel + "fence.png";
		String medic = directory + dirModel + "medic.png";
		String wall = directory + dirEnvironment + "wall.png";
		
		// Load Textures
		skyBoxTextureTop = TextureManager.loadTexture2D(skyTop);
		skyBoxTextureBot = TextureManager.loadTexture2D(skyBot);
		skyBoxTextureEast = TextureManager.loadTexture2D(skyEast);
		skyBoxTextureFront = TextureManager.loadTexture2D(skyFront);
		skyBoxTextureWest = TextureManager.loadTexture2D(skyWest);
		skyBoxTextureBack = TextureManager.loadTexture2D(skyBack);
		groundTexture = TextureManager.loadTexture2D(ground);
		ammoBoxTexture = TextureManager.loadTexture2D(ammoBox);
		avatarTexture = TextureManager.loadTexture2D(avatar);
		sandTexture = TextureManager.loadTexture2D(sand);
		sandTexture.setWrapMode(WrapMode.Repeat);
		fenceTexture = TextureManager.loadTexture2D(fence);
		medicTexture = TextureManager.loadTexture2D(medic);
		wallTexture = TextureManager.loadTexture2D(wall);
	}
	
	/**
	 * Updates the game bound world.
	 * 
	 * @param wallGroup
	 */
	public void updateBoundaryEnvironment(Group wallGroup) {
		Iterator<SceneNode> children = wallGroup.getChildren();
		
		Rectangle s;
		
		while (children.hasNext()) {
			s = (Rectangle) children.next();
			s.setTexture(wallTexture);
			s.setCullMode(CULL_MODE.NEVER);
		}
	}
	
	/**
	 * Adds the ammo boxes to the game world.
	 * 
	 * @param ammoGroup
	 */
	public void addAmmoBoxes(	Group ammoGroup,
								PhysicsManager pMan) {
		TriMesh ammoBoxTM = loader.loadModel(directory + dirModel + "ammo.obj");
		ammoBoxTM.updateLocalBound();
		ammoBoxTM.setTexture(ammoBoxTexture);
		ammoBoxTM.translate(60, .8f, 75);
		ammoBoxTM.scale(6, 6, 6);
		ammoGroup.addChild(ammoBoxTM);
	}
	
	public Model3DTriMesh addAvatar() {
		Model3DTriMesh avatarTM = null;
		OgreXMLParser loader = new OgreXMLParser();
		String baseDir = directory + dirModel;
		
		try {
			Group model = loader.loadModel(baseDir + "Cube.mesh.xml", baseDir
					+ "Material.001.material", baseDir + "Cube.skeleton.xml");
			model.updateGeometricState(0, true);
			Iterator<SceneNode> itr = model.iterator();
			
			avatarTM = (Model3DTriMesh) itr.next();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		avatarTexture.setApplyMode(Texture.ApplyMode.Replace);
		avatarTM.setTexture(avatarTexture);
		
		return avatarTM;
	}
	
	/**
	 * Adds the health boxes to the game world.
	 * 
	 * @param ammoGroup
	 */
	public void addHealthBoxes(	Group healthGroup,
								PhysicsManager pMan) {
		TriMesh healthBoxTM = loader.loadModel(directory + dirModel + "medic.obj");
		TriMesh healthBoxTM2 = loader.loadModel(directory + dirModel + "medic.obj");
		TriMesh healthBoxTM3 = loader.loadModel(directory + dirModel + "medic.obj");
		healthBoxTM.updateLocalBound();
		healthBoxTM.setTexture(medicTexture);
		healthBoxTM.translate(getRandomSignedInteger(1300), 0.5f, getRandomSignedInteger(700));
		healthBoxTM2.updateLocalBound();
		healthBoxTM2.setTexture(medicTexture);
		healthBoxTM2.translate(getRandomSignedInteger(1300), 0.5f, getRandomSignedInteger(700));
		healthBoxTM3.updateLocalBound();
		healthBoxTM3.setTexture(medicTexture);
		healthBoxTM3.translate(getRandomSignedInteger(1300), 0.5f, getRandomSignedInteger(700));
		
		pMan.bindPhysicsProperty(healthBoxTM, 5.0f);
		pMan.bindPhysicsProperty(healthBoxTM2, 5.0f);
		pMan.bindPhysicsProperty(healthBoxTM3, 5.0f);
		healthGroup.addChild(healthBoxTM);
		healthGroup.addChild(healthBoxTM2);
		healthGroup.addChild(healthBoxTM3);
	}
	
	/**
	 * Adds fencing to the game world.
	 * 
	 * @param fenceGroup
	 */
	public void addFencing(Group fenceGroup) {
		TriMesh fenceTM = loader.loadModel(directory + dirModel + "fence.obj");
		fenceTM.updateLocalBound();
		fenceTM.setTexture(fenceTexture);
		fenceTM.translate(-100, 9f, 100);
		fenceTM.scale(8, 8, 8);
		fenceGroup.addChild(fenceTM);
	}
	
	/**
	 * Creates the SkyBox.
	 * 
	 * @param bg
	 * @param origin
	 * @return
	 */
	public SkyBox addSkybox(BaseGame bg,
							float origin) {
		skyBox = new SkyBox();
		skyBox.scale(550, 550, 550);
		skyBox.translate(65, 0, 55 + origin);
		skyBox.setTexture(Face.Up, skyBoxTextureTop);
		skyBox.setTexture(Face.Down, skyBoxTextureBot);
		skyBox.setTexture(Face.East, skyBoxTextureEast);
		skyBox.setTexture(Face.North, skyBoxTextureFront);
		skyBox.setTexture(Face.West, skyBoxTextureWest);
		skyBox.setTexture(Face.South, skyBoxTextureBack);
		skyBox.setZBufferStateEnabled(false);
		
		return skyBox;
	}
	
	/**
	 * Creates the floor for the game world.
	 * 
	 * @return
	 */
	public void addGameFloor(	Group environmentGroup,
								PhysicsManager pMan) {
		// Create the world
		floor = new Rectangle();
		floor.scale(2600, 1400, 10);
		floor.rotate(90, new Vector3D(1, 0, 0));
		floor.translate(0, -.8f, 0);
		floor.setTexture(groundTexture);
		pMan.bindFloorPhysics(floor);
		environmentGroup.addChild(floor);
	}
	
	/**
	 * Create a hierarchical structured solar system.
	 * 
	 * @return
	 */
	public Group createSolarSystem() {
		Group temp = new Group("Solar System");
		Group temp2 = new Group("Planet System Rotation 1");
		Group temp3 = new Group("Planet System Rotation 2");
		Group temp4 = new Group("Moon");
		
		Sphere sun = new Sphere("Sun", 60, 30, 30, Color.ORANGE);
		sun.translate(20, 0, 0);
		Sphere planet = new Sphere("Planet", 30, 20, 20, Color.MAGENTA);
		planet.translate(-80, 20, 20);
		planet.scale(.6f, .6f, .6f);
		Sphere moon = new Sphere("Moon", 10, 30, 30, Color.GRAY);
		moon.translate(-110, 40, -5);
		moon.scale(.5f, .5f, .5f);
		
		temp.addChild(sun);
		temp.addChild(temp2);
		temp.addChild(temp3);
		temp3.addChild(planet);
		temp3.addChild(temp4);
		temp4.addChild(moon);
		
		temp.setIsTransformSpaceParent(true);
		temp2.setIsTransformSpaceParent(true);
		temp3.setIsTransformSpaceParent(true);
		temp4.setIsTransformSpaceParent(true);
		sun.setIsTransformSpaceParent(true);
		planet.setIsTransformSpaceParent(true);
		moon.setIsTransformSpaceParent(true);
		
		temp2.scale(2, 2, 2);
		temp3.scale(3, 3, 3);
		temp4.rotate(20, new Vector3D(0, 1, 0));
		
		return temp;
		
	}
	
	/**
	 * Initializes the terrain.
	 */
	public TerrainBlock initTerrain(IDisplaySystem display) { // create height map and terrain block
		HillHeightMap myHillHeightMap = new HillHeightMap(429, 2000, 40.0f, 50.0f, (byte) 2, 12345);
		myHillHeightMap.setHeightScale(1.1f);
		hillTerrain = createTerBlock(myHillHeightMap);
		
		// create texture and texture state to color the terrain
		TextureState terrainState;
		sandTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		terrainState = (TextureState) display.getRenderer().createRenderState(
				RenderState.RenderStateType.Texture);
		terrainState.setTexture(sandTexture, 0);
		terrainState.setEnabled(true);
		
		// apply the texture to the terrain
		hillTerrain.setRenderState(terrainState);
		return hillTerrain;
	}
	
	/**
	 * Creates a terrain block.
	 * 
	 * @param heightMap
	 * @return
	 */
	private TerrainBlock createTerBlock(AbstractHeightMap heightMap) {
		float heightScale = 1.19f;
		Vector3D terrainScale = new Vector3D(1, heightScale, 1);
		
		// use the size of the height map as the size of the terrain
		int terrainSize = heightMap.getSize();
		
		// specify terrain origin so heightmap (0,0) is at world origin
		float cornerHeight = heightMap.getTrueHeightAtPoint(0, 0) * heightScale;
		Point3D terrainOrigin = new Point3D(200, -cornerHeight, 720);
		
		// create a terrain block using the height map
		String name = "Terrain:" + heightMap.getClass().getSimpleName();
		TerrainBlock tb = new TerrainBlock(name, terrainSize, terrainScale,
				heightMap.getHeightData(), terrainOrigin);
		return tb;
	}
	
	public Rectangle getFloor() {
		return floor;
	}
	
	public SkyBox getSkyBox() {
		return skyBox;
	}
	
	public TerrainBlock getHillTerrain() {
		return hillTerrain;
	}
	
	/**
	 * Obtains a random positive/negative integer
	 */
	private int getRandomSignedInteger(int limit) {
		int temp = r.nextInt(limit);
		
		if (r.nextBoolean()) {
			temp = -temp;
		}
		
		return temp;
	}
	
}
