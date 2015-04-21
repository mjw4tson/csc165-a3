package engine.scene;

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
	private String			dirHud			= "images" + File.separator + "hud" + File.separator;
	private String			dirScripts		= "scripts" + File.separator;
	private String			dirModel		= "images" + File.separator + "models" + File.separator;
	
	// Texture Objects
	private Texture			skyBoxTextureTop;
	private Texture			skyBoxTextureBack;
	private Texture			skyBoxTextureEast;
	private Texture			skyBoxTextureWest;
	private Texture			skyBoxTextureFront;
	private Texture			skyBoxTextureBot;
	private Texture			groundTexture;
	private Texture			lavaTexture;
	private Texture			ammoBoxTexture;
	private Texture			sandTexture;
	
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
		String skyFront = directory + dirEnvironment + "skybox_front.png";
		String skyEast = directory + dirEnvironment + "skybox_east.png";
		String skyWest = directory + dirEnvironment + "skybox_west.png";
		String skyBot = directory + dirEnvironment + "skybox_bot.png";
		String skyTop = directory + dirEnvironment + "skybox_top.png";
		String skyBack = directory + dirEnvironment + "skybox_back.png";
		String ground = directory + dirEnvironment + "ground.jpg";
		String lava = directory + dirEnvironment + "lava.jpg";
		String ammoBox = directory + dirModel + "ammo.png";
		String sand = directory + dirEnvironment + "ground.jpg";
		
		// Load Textures
		skyBoxTextureTop = TextureManager.loadTexture2D(skyTop);
		skyBoxTextureBot = TextureManager.loadTexture2D(skyBot);
		skyBoxTextureEast = TextureManager.loadTexture2D(skyEast);
		skyBoxTextureFront = TextureManager.loadTexture2D(skyFront);
		skyBoxTextureWest = TextureManager.loadTexture2D(skyWest);
		skyBoxTextureBack = TextureManager.loadTexture2D(skyBack);
		groundTexture = TextureManager.loadTexture2D(ground);
		lavaTexture = TextureManager.loadTexture2D(lava);
		ammoBoxTexture = TextureManager.loadTexture2D(ammoBox);
		sandTexture = TextureManager.loadTexture2D(sand);
	}
	
	/**
	 * Adds the ammo boxes to the game world.
	 * @param ammoGroup
	 */
	public void addAmmoBoxes(Group ammoGroup) {
		TriMesh ammoBoxTM = loader.loadModel(directory + dirModel + "ammo.obj");
		ammoBoxTM.updateLocalBound();
		ammoBoxTM.setTexture(ammoBoxTexture);
		ammoBoxTM.translate(-220, -20, 100);
		ammoBoxTM.scale(100, 100, 100);
		ammoBoxTM.setCullMode(CULL_MODE.NEVER);
		ammoGroup.addChild(ammoBoxTM);
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
	 * Tasked with creating lava for the game world, adds it to the specified group.
	 * 
	 * @return
	 */
	public void addLava(Group lavaGroup) {
		Rectangle lavaSegmentN = new Rectangle();
		lavaSegmentN.scale(2000, 2000, 10);
		lavaSegmentN.rotate(90, new Vector3D(1, 0, 0));
		lavaSegmentN.translate(1500, -.8f, 0);
		lavaSegmentN.setTexture(lavaTexture);
		
		Rectangle lavaSegmentS = new Rectangle();
		lavaSegmentS.scale(2000, 2000, 10);
		lavaSegmentS.rotate(90, new Vector3D(1, 0, 0));
		lavaSegmentS.translate(-1500, -.8f, 0);
		lavaSegmentS.setTexture(lavaTexture);
		
		Rectangle lavaSegmentE = new Rectangle();
		lavaSegmentE.scale(1000, 1000, 10);
		lavaSegmentE.rotate(90, new Vector3D(1, 0, 0));
		lavaSegmentE.translate(0, -.8f, -1000);
		lavaSegmentE.setTexture(lavaTexture);
		
		Rectangle lavaSegmentW = new Rectangle();
		lavaSegmentW.scale(1000, 1000, 10);
		lavaSegmentW.rotate(90, new Vector3D(1, 0, 0));
		lavaSegmentW.translate(0, -.8f, 1000);
		lavaSegmentW.setTexture(lavaTexture);
		
		lavaGroup.addChild(lavaSegmentN);
		lavaGroup.addChild(lavaSegmentS);
		lavaGroup.addChild(lavaSegmentE);
		lavaGroup.addChild(lavaSegmentW);
	}
	
	/**
	 * Creates the floor for the game world.
	 * 
	 * @return
	 */
	public void addGameFloor(Group environmentGroup) {
		// Create the world
		floor = new Rectangle();
		floor.scale(1000, 1000, 10);
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
