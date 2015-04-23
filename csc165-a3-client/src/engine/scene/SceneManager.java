package engine.scene;

import engine.scene.physics.PhysicsManager;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.io.File;

import sage.app.BaseGame;
import sage.display.IDisplaySystem;
import sage.model.loader.OBJLoader;
import sage.scene.Group;
import sage.scene.SkyBox;
import sage.scene.SceneNode.CULL_MODE;
import sage.scene.SkyBox.Face;
import sage.scene.TriMesh;
import sage.scene.shape.Rectangle;
import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.HillHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.Texture.WrapMode;
import sage.texture.TextureManager;

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
	private Texture			sandTexture;
	private Texture			fenceTexture;
	private Texture			medicTexture;
	
	// Game World SceneNodes
	private SkyBox			skyBox;
	private Rectangle		floor;
	private TerrainBlock	hillTerrain;
	
	// Modeling
	private OBJLoader		loader			= new OBJLoader();
	
	public SceneManager(String dir) {
		directory = dir;
		setupTextures();
	}
	
	private void setupTextures() {
		// Texture file locations
		String skyFront = directory + dirEnvironment + "posz.jpg";
		String skyEast = directory + dirEnvironment + "posx.jpg";
		String skyWest = directory + dirEnvironment + "negx.jpg";
		String skyBot = directory + dirEnvironment + "negy.jpg";
		String skyTop = directory + dirEnvironment + "posy.jpg";
		String skyBack = directory + dirEnvironment + "negz.jpg";
		String ground = directory + dirEnvironment + "ground.jpg";
		String ammoBox = directory + dirModel + "ammo.png";
		String sand = directory + dirEnvironment + "ground.jpg";
		String fence = directory + dirModel + "fence.png";
		String medic = directory + dirModel + "medic.png";
		
		// Load Textures
		skyBoxTextureTop = TextureManager.loadTexture2D(skyTop);
		skyBoxTextureBot = TextureManager.loadTexture2D(skyBot);
		skyBoxTextureEast = TextureManager.loadTexture2D(skyEast);
		skyBoxTextureFront = TextureManager.loadTexture2D(skyFront);
		skyBoxTextureWest = TextureManager.loadTexture2D(skyWest);
		skyBoxTextureBack = TextureManager.loadTexture2D(skyBack);
		groundTexture = TextureManager.loadTexture2D(ground);
		ammoBoxTexture = TextureManager.loadTexture2D(ammoBox);
		sandTexture = TextureManager.loadTexture2D(sand);
		sandTexture.setWrapMode(WrapMode.Repeat);
		fenceTexture = TextureManager.loadTexture2D(fence);
		medicTexture = TextureManager.loadTexture2D(medic);
	}
	
	/**
	 * Adds the ammo boxes to the game world.
	 * 
	 * @param ammoGroup
	 */
	public void addAmmoBoxes(Group ammoGroup, PhysicsManager pMan) {
		TriMesh ammoBoxTM = loader.loadModel(directory + dirModel + "ammo.obj");
		ammoBoxTM.updateLocalBound();
		ammoBoxTM.setTexture(ammoBoxTexture);
		ammoBoxTM.translate(-10, 10.5f, 50);
		ammoBoxTM.scale(6, 6, 6);
		pMan.bindPhysicsProperty(ammoBoxTM, 5.0f);
		ammoGroup.addChild(ammoBoxTM);
	}
	
	/**
	 * Adds the health boxes to the game world.
	 * 
	 * @param ammoGroup
	 */
	public void addHealthBoxes(Group healthGroup, PhysicsManager pMan) {
		TriMesh healthBoxTM = loader.loadModel(directory + dirModel + "medic.obj");
		healthBoxTM.updateLocalBound();
		healthBoxTM.setTexture(medicTexture);
		healthBoxTM.translate(10, 0.5f, 50);
		healthBoxTM.scale(8, 8, 8);
		pMan.bindPhysicsProperty(healthBoxTM, 5.0f);
		healthGroup.addChild(healthBoxTM);
	}
	
	
	/**
	 * Adds fencing to the game world.
	 * @param fenceGroup
	 */
	public void addFencing(Group fenceGroup){
		TriMesh fenceTM = loader.loadModel(directory + dirModel + "fence.obj");
		fenceTM.updateLocalBound();
		fenceTM.setTexture(fenceTexture);
		fenceTM.translate(-700, 9f, 640);
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
		return skyBox;
	}
	
	/**
	 * Creates the floor for the game world.
	 * 
	 * @return
	 */
	public void addGameFloor(Group environmentGroup) {
		// Create the world
		floor = new Rectangle();
		floor.scale(2600, 1400, 10);
		floor.rotate(90, new Vector3D(1, 0, 0));
		floor.translate(0, -.8f, 0);
		floor.setTexture(groundTexture);
		environmentGroup.addChild(floor);
	}
	
	/**
	 * Initializes the terrain.
	 */
	public TerrainBlock initTerrain(IDisplaySystem display) { // create height map and terrain block
		HillHeightMap myHillHeightMap = new HillHeightMap(300, 2000, 5.0f, 20.0f, (byte) 2, 12345);
		myHillHeightMap.setHeightScale(0.01f);
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
		float heightScale = 0.13f;
		Vector3D terrainScale = new Vector3D(1, heightScale, 1);
		
		// use the size of the height map as the size of the terrain
		int terrainSize = heightMap.getSize();
		
		// specify terrain origin so heightmap (0,0) is at world origin
		float cornerHeight = heightMap.getTrueHeightAtPoint(0, 0) * heightScale;
		Point3D terrainOrigin = new Point3D(0, -cornerHeight, 0);
		
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
	
}
