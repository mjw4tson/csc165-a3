package games.treasurehunt2015;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.InputManager;
import sage.model.loader.OBJLoader;
import sage.networking.IGameConnection.ProtocolType;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.physics.PhysicsEngineFactory;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.HUDImage;
import sage.scene.SceneNode;
import sage.scene.SceneNode.CULL_MODE;
import sage.scene.SkyBox;
import sage.scene.SkyBox.Face;
import sage.scene.TriMesh;
import sage.scene.shape.Cube;
import sage.scene.shape.Pyramid;
import sage.scene.shape.Rectangle;
import sage.scene.state.BlendState;
import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.HillHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;
import sage.util.VersionInfo;
import engine.event.CrashEvent;
import engine.graphics.GameDisplaySystem;
import engine.input.InputHandler;
import engine.input.action.camera.Camera3PController;
import engine.objects.Avatar;
import engine.scene.controller.BounceController;
import engine.scene.controller.ScaleController;
import engine.scene.hud.HUDNumber;
import engine.scene.shape.Axis;
import engine.scene.shape.TreasureChest;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

/**
 * Represents the TreasureHunt game, utilizes SAGE libraries to create game logic.
 *
 * @author ktajeran
 */
public class TreasureHunt extends BaseGame implements MouseWheelListener {
	
	// Engine objects.
	public Camera3PController	cc1;
	private IDisplaySystem		display;													// The game display.
	private ICamera				camera1;													// The game camera.
	private InputHandler		ih;														// Input handler
	private IInputManager		im;														// The input manager
	private IEventManager		eventManager;
	private IRenderer			renderer;
	private boolean				finalBuild				= false;							// Determines if final build
	private float				time					= 0.0f;							// Stores the total time
	private int					scoreP1					= 0;								// The total score
	private int					numCrashes				= 0;
	private Cursor				crossHairCursor;
	private BlendState			transparencyState;
	private HUDNumber			hudNumberManager;
	
	private static String		directory				= "." + File.separator + "bin"
																+ File.separator;
	
	// Game World Objects
	private Axis				worldAxis;
	
	private TreasureChest		treasureChest;
	private SkyBox				skybox;
	public HillHeightMap		myHillHeightMap;
	public TerrainBlock			hillTerrain;
	
	// Players
	public Avatar				localPlayer;
	public Vector<Avatar>		ghostPlayers;
	
	// SceneNode Controllers
	private BounceController	roSNController			= new BounceController();
	private ScaleController		scSNController			= new ScaleController();
	
	// SceneNode Groups
	private Group				cuGroup					= new Group("Cube Group");
	private Group				pyGroup					= new Group("Pyramid Group");
	private Group				hudGroupTeamOne			= new Group("Team One Group");
	private Group				environmentGroup		= new Group("Environment Group");
	private Group				hudGroupTeamOneTime		= new Group("Team One Group Time");
	private Group				lavaGroup				= new Group("Lava Group");
	
	// Texture Objects
	private Texture				skyBoxTextureTop;
	private Texture				skyBoxTextureBack;
	private Texture				skyBoxTextureEast;
	private Texture				skyBoxTextureWest;
	private Texture				skyBoxTextureFront;
	private Texture				skyBoxTextureBot;
	private Texture				groundTexture;
	private Texture				lavaTexture;
	private Texture				ammoBoxTexture;
	
	private String				dirEnvironment			= "images" + File.separator + "environment"
																+ File.separator;
	private String				dirHud					= "images" + File.separator + "hud"
																+ File.separator;
	private String				dirScripts				= "scripts" + File.separator;
	private String				dirModel				= "images" + File.separator + "models"
																+ File.separator;
	
	// Scripting
	private ScriptEngine		jsEngine;
	
	// HUD
	private HUDImage			p1Time;
	private HUDImage			p1Score;
	private float				origin					= 65f;
	
	// Game Client
	private TreasureHuntClient	gameClient;
	private String				serverAddr;
	private int					serverPort;
	private ProtocolType		pType;
	private boolean				isConnected;
	private static int			ghostCount				= 0;
	
	// Environment
	private Rectangle			floor;
	private Rectangle			lavaSegmentN;
	private Rectangle			lavaSegmentS;
	private Rectangle			lavaSegmentE;
	private Rectangle			lavaSegmentW;
	
	// Modeling
	private OBJLoader 			loader = new OBJLoader();
	private TriMesh				ammoBoxTM;
	
	// Physics
	private IPhysicsEngine		physicsEngine;
	private IPhysicsObject		playerPObject, worldFloor;
	// Enables the updating of each game world object physics state, if applicable.
	private boolean				physicsEnabled			= false;
	// Enables the physics engine.
	private boolean				physicsEngineEnabled	= false;
	
	/**
	 * Sets up the initial game.
	 */
	@Override
	public void initGame() {
		if (!runAsSinglePlayer()) {
			initGameClient();
		}
		
		configureEnvironment();
		initGameEntities(); // Populate the game world.
		initTerrain();
		if (physicsEngineEnabled) {
			initPhysicsSystem();
		}
		addEventHandlers();
		initEventManager(); // Get event manager.
		cc1 = new Camera3PController(camera1, localPlayer);
		setupControls(); // Set up the game world controls.
	}
	
	/**
	 * Determine if game is single player
	 */
	private boolean runAsSinglePlayer() {
		return ((GameDisplaySystem) display).isSinglePlayer();
	}
	
	/**
	 * Initializes game client
	 */
	private void initGameClient() {
		serverAddr = ((GameDisplaySystem) display).getServerIP();
		serverPort = ((GameDisplaySystem) display).getServerPort();
		System.out.println("Lauching multiplayer, connecting to " + serverAddr + ":" + serverPort);
		
		pType = ProtocolType.TCP;
		
		pType = ProtocolType.TCP;
		
		try {
			gameClient = new TreasureHuntClient(InetAddress.getByName(serverAddr), serverPort,
					pType, this);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (gameClient != null) {
			System.out.println("Connected to server, joining...");
			gameClient.sendJoinMsg();
		}
	}
	
	/**
	 * Handles logic for update the game state upon every engine loop iteration..
	 */
	public void update(float elapsedTimeMS) {
		if (gameClient != null) {
			gameClient.processPackets();
		}
		
		updateGameWorldPhysicsState(physicsEnabled);
		super.update(time);
		
		updateGameWorld(elapsedTimeMS);
		updateSceneNodeControllers();
		cc1.update(elapsedTimeMS);
		
		super.update(elapsedTimeMS);
	}
	
	/**
	 * Updates all of the game world objects physics states if applicable.
	 * 
	 * @param pEnabled
	 */
	private void updateGameWorldPhysicsState(boolean pEnabled) {
		if (pEnabled && physicsEngineEnabled) {
			Matrix3D mat;
			Vector3D translateVec;
			physicsEngine.update(20.0f);
			for (SceneNode s : getGameWorld()) {
				if (s.getPhysicsObject() != null) {
					mat = new Matrix3D(s.getPhysicsObject().getTransform());
					translateVec = mat.getCol(3);
					s.getLocalTranslation().setCol(3, translateVec);
					// should also get and apply rotation
				}
			}
		}
	}
	
	/**
	 * Updates all SceneNode controllers.
	 */
	private void updateSceneNodeControllers() {
		roSNController.update(0.4f);
		scSNController.update(1.072f);
	}
	
	/**
	 * This helper method handles the game initialization logic.
	 */
	private void initEventManager() {
		eventManager = EventManager.getInstance();
		eventManager.addListener(treasureChest, CrashEvent.class);
		super.update(0.0f);
	}
	
	/**
	 * Moves the SkyBox to account for any player movement.
	 */
	private void moveSkybox(ICamera cam) {
		Point3D camLoc = cam.getLocation();
		Matrix3D camTranslation = new Matrix3D();
		camTranslation.translate(camLoc.getX(), camLoc.getY(), camLoc.getZ());
		skybox.setLocalTranslation(camTranslation);
	}
	
	/**
	 * Method that handles updating the BaseGame and the game world objects.
	 *
	 * @param elapsedTimeMS
	 *            - The total time that has passed in the game world.
	 */
	private void updateGameWorld(float elapsedTimeMS) {
		time += elapsedTimeMS;
		super.update(elapsedTimeMS);
		
		camera1.setLocation(camera1.getLocation());
		
		moveSkybox(camera1);
		
		Iterator<SceneNode> iterator = getGameWorld().iterator();
		SceneNode s;
		
		Point3D locP1 = new Point3D(localPlayer.getLocalTranslation().getCol(3));
		
		while (iterator.hasNext()) {
			s = iterator.next();
			
			if (s instanceof Group) {
				Iterator<SceneNode> groupChildren = ((Group) s).getChildren();
				SceneNode gs;
				while (groupChildren.hasNext()) {
					gs = groupChildren.next();
					
					if (gs.getWorldBound() != null && gs.getWorldBound().contains(locP1)
							&& (gs instanceof Cube || gs instanceof Pyramid)) {
						this.scoreP1 = this.scoreP1 + 1;
						groupChildren.remove();
						this.removeGameWorldObject(gs);
						CrashEvent newCrash = new CrashEvent(numCrashes);
						eventManager.triggerEvent(newCrash);
					}
				}
			}
		}
		
		// Updates the time and score states on the hud.
		// TODO optimize how the hud is updated. 
		int timeTemp = (int) time / 1000;
		
		removeGameWorldObject(hudGroupTeamOne);
		removeGameWorldObject(hudGroupTeamOneTime);
		
		hudGroupTeamOne = hudNumberManager.printValues(scoreP1, -0.850f, -0.88f);
		hudGroupTeamOneTime = hudNumberManager.printValues(timeTemp, 0.050f, 0.90f);
		
		addGameWorldObject(hudGroupTeamOne);
		addGameWorldObject(hudGroupTeamOneTime);
		
	}
	
	/**
	 * Helper method that builds and initializes game world entities.
	 */
	public void initGameEntities() {
		buildCamera();
		obtainTextures();
		buildHUD();
		loadGameWorldObjects();
	}
	
	/**
	 * Initializes the terrain.
	 */
	private void initTerrain() { // create height map and terrain block
		myHillHeightMap = new HillHeightMap(300, 2000, 5.0f, 20.0f, (byte) 2, 12345);
		myHillHeightMap.setHeightScale(0.01f);
		hillTerrain = createTerBlock(myHillHeightMap);
		
		// create texture and texture state to color the terrain
		TextureState grassState;
		Texture sandTexture = TextureManager.loadTexture2D(directory + dirEnvironment
				+ "ground.jpg");
		sandTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		grassState = (TextureState) display.getRenderer().createRenderState(
				RenderState.RenderStateType.Texture);
		grassState.setTexture(sandTexture, 0);
		grassState.setEnabled(true);
		
		// apply the texture to the terrain
		hillTerrain.setRenderState(grassState);
		addGameWorldObject(hillTerrain);
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
	
	/**
	 * Helper method that handles the creation and population of texture images into the engine.
	 */
	private void obtainTextures() {
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
		
		// Load Textures
		skyBoxTextureTop = TextureManager.loadTexture2D(skyTop);
		skyBoxTextureBot = TextureManager.loadTexture2D(skyBot);
		skyBoxTextureEast = TextureManager.loadTexture2D(skyEast);
		skyBoxTextureFront = TextureManager.loadTexture2D(skyFront);
		skyBoxTextureWest = TextureManager.loadTexture2D(skyWest);
		skyBoxTextureBack = TextureManager.loadTexture2D(skyBack);
		groundTexture = TextureManager.loadTexture2D(ground);
		lavaTexture = TextureManager.loadTexture2D(lava);
		ammoBoxTexture= TextureManager.loadTexture2D(ammoBox);
	}
	
	/**
	 * Method that handles the logic of creating and setting the camera(s) in the game world.
	 */
	private void buildCamera() {
		// Setup camera and display.
		renderer = getDisplaySystem().getRenderer();
		buildTransparency();
		
		// Set crosshair as the defaut cursor.
		crossHairCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		renderer.getCanvas().setCursor(crossHairCursor);
		
		camera1 = new JOGLCamera(renderer);
		camera1.setPerspectiveFrustum(60, 2, 1, 1000);
		camera1.setViewport(0.0, 1.0, 0.0, 1.0);
	}
	
	/**
	 * Builds the HUD.
	 */
	private void buildHUD() {
		hudNumberManager = new HUDNumber("HUD Number Manager", directory);
		hudGroupTeamOne = hudNumberManager.printValues(0, -0.9f, -0.90f);
		
		// Add P1 Time
		p1Time = new HUDImage(directory + dirHud + "player1.png");
		p1Time.setName("Player 1 Time");
		p1Time.setLocation(0, 0.90);
		p1Time.rotateImage(180);
		p1Time.scale(.156f, .0575f, .1f);
		p1Time.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		camera1.addToHUD(p1Time);
		
		// Add P1 Score
		p1Score = new HUDImage(directory + dirHud + "p1_score.png");
		p1Score.setName("Player 1 Score");
		p1Score.setLocation(-.9f, -0.88f);
		p1Score.rotateImage(180);
		p1Score.scale(.1570f, .0575f, .1f);
		p1Score.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		camera1.addToHUD(p1Score);
		
		hudGroupTeamOne = hudNumberManager.printValues(0, -0.850f, -0.88f);
		hudGroupTeamOneTime = hudNumberManager.printValues(0, 0.050f, 0.90f);
		
		addGameWorldObject(hudGroupTeamOne);
		addGameWorldObject(hudGroupTeamOneTime);
		
	}
	
	/**
	 * Populates the blend state with transparency properties.
	 */
	private void buildTransparency() {
		transparencyState = null;
		transparencyState = (BlendState) renderer
				.createRenderState(RenderState.RenderStateType.Blend);
		
		transparencyState.setBlendEnabled(true);
		transparencyState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		transparencyState.setDestinationFunction(BlendState.DestinationFunction.DestinationAlpha);
		transparencyState.setTestEnabled(true);
		transparencyState.setTestFunction(BlendState.TestFunction.GreaterThan);
		transparencyState.setEnabled(true);
	}
	
	/**
	 * This method is tasked with building and placing game world objects in the world.
	 */
	private void loadGameWorldObjects() {
		// Add a skybox.
		skybox = new SkyBox();
		skybox.scale(550, 550, 550);
		skybox.translate(65, 0, 55 + origin);
		skybox.setTexture(Face.Up, skyBoxTextureTop);
		skybox.setTexture(Face.Down, skyBoxTextureBot);
		skybox.setTexture(Face.East, skyBoxTextureEast);
		skybox.setTexture(Face.North, skyBoxTextureFront);
		skybox.setTexture(Face.West, skyBoxTextureWest);
		skybox.setTexture(Face.South, skyBoxTextureBack);
		addGameWorldObject(skybox);
		
		//Create the world
		floor = new Rectangle();
		floor.scale(1000, 1000, 10);
		floor.rotate(90, new Vector3D(1, 0, 0));
		floor.translate(0, -.8f, 0);
		floor.setTexture(groundTexture);
		environmentGroup.addChild(floor);
		addGameWorldObject(environmentGroup);
		
		createLava();
		
		// Get and build game world objects
		buildEnvironmentFromScript();
		
		
		 // Adding ammo box to the game world.
		ammoBoxTM = loader.loadModel(directory + dirModel + "ammo.obj"); 
		ammoBoxTM.updateLocalBound(); 
		ammoBoxTM.setTexture(ammoBoxTexture);  
		ammoBoxTM.translate(-220, -20, 100);
		ammoBoxTM.scale(100, 100, 100); 
		ammoBoxTM.setCullMode(CULL_MODE.NEVER); 
		addGameWorldObject(ammoBoxTM); 
		
		
		// Add SceneNode controllers to each group.
		scSNController.addControlledNode(pyGroup);
		roSNController.addControlledNode(cuGroup);
		
		addGameWorldObject(pyGroup);
		addGameWorldObject(cuGroup);
		
		treasureChest = new TreasureChest();
		addGameWorldObject(treasureChest);
		treasureChest.translate(50, 2, 20);
		treasureChest.scale(.8f, .4f, .2f);
		
		// Add players
		localPlayer = new Avatar("Player 1", 1, 20, 20, Color.blue);
		localPlayer.translate(50, .8f, 10 + origin);
		
		addGameWorldObject(localPlayer);
		
		// Add a 3D axis to the game world.
		worldAxis = new Axis(50, Color.red, Color.green, Color.blue);
		addGameWorldObject(worldAxis);
	}
	
	/**
	 * Adds lava to the game world.
	 */
	private void createLava() {
		// Create lava
		lavaSegmentN = new Rectangle();
		lavaSegmentN.scale(2000, 2000, 10);
		lavaSegmentN.rotate(90, new Vector3D(1, 0, 0));
		lavaSegmentN.translate(1500, -.8f, 0);
		lavaSegmentN.setTexture(lavaTexture);
		
		lavaSegmentS = new Rectangle();
		lavaSegmentS.scale(2000, 2000, 10);
		lavaSegmentS.rotate(90, new Vector3D(1, 0, 0));
		lavaSegmentS.translate(-1500, -.8f, 0);
		lavaSegmentS.setTexture(lavaTexture);
		
		lavaSegmentE = new Rectangle();
		lavaSegmentE.scale(1000, 1000, 10);
		lavaSegmentE.rotate(90, new Vector3D(1, 0, 0));
		lavaSegmentE.translate(0, -.8f, -1000);
		lavaSegmentE.setTexture(lavaTexture);
		
		lavaSegmentW = new Rectangle();
		lavaSegmentW.scale(1000, 1000, 10);
		lavaSegmentW.rotate(90, new Vector3D(1, 0, 0));
		lavaSegmentW.translate(0, -.8f, 1000);
		lavaSegmentW.setTexture(lavaTexture);
		
		lavaGroup.addChild(lavaSegmentN);
		lavaGroup.addChild(lavaSegmentS);
		lavaGroup.addChild(lavaSegmentE);
		lavaGroup.addChild(lavaSegmentW);
		
		addGameWorldObject(lavaGroup);
	}
	
	/**
	 * Initializes the game core systems.
	 */
	protected void initSystem() {
		// call a local method to create a DisplaySystem object
		display = new GameDisplaySystem(1920, 1080, 32, 60, false,
				"sage.renderer.jogl.JOGLRenderer", "Treasure Hunt 2015");
		((GameDisplaySystem) display).waitForInitialization();
		setDisplaySystem(display);
		
		// create an Input Manager
		IInputManager inputManager = new InputManager();
		setInputManager(inputManager);
		// create an (empty) gameworld
		
		ArrayList<SceneNode> gameWorld = new ArrayList<SceneNode>();
		setGameWorld(gameWorld);
	}
	
	/**
	 * This helper method handles giving game world objects event handlers if needed.
	 */
	private void addEventHandlers() {
		
	}
	
	/**
	 * Configures the game based upon the build, whether it is a final build or a test build.
	 */
	public void configureEnvironment() {
		VersionInfo version = new VersionInfo();
		System.out.println("SAGE: " + version.getSystemVersionInfo());
		this.getDisplaySystem().getRenderer().getCanvas().addMouseWheelListener(this);
		
		// Set the directory to empty if the build is not final, as everything will be located on a
		// flat level in this case.
		if (!finalBuild) {
			directory = "." + File.separator;
		}
	}
	
	/**
	 * Accomplishes an orderly exit
	 */
	@Override
	protected void shutdown() {
		display.close();
		
		if (gameClient != null) {
			try {
				gameClient.sendByeMessage();
				gameClient.shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Handles setting up the game controls.
	 */
	private void setupControls() {
		im = getInputManager();
		ih = new InputHandler(im);
		im = ih.setupControls(camera1, im, this);
	}
	
	/**
	 * Handles game rendering.
	 */
	protected void render() {
		renderer.setCamera(camera1);
		super.render();
	}
	
	/**
	 * Handles zooming in the 3P camera for player 1 with the mouse scroll wheel.
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int rotations = e.getWheelRotation();
		if (rotations < 0) {
			cc1.zoomIn(2f);
		} else {
			cc1.zoomOut(2f);
		}
	}
	
	/**
	 * Method tasked with obtaining a script file and its JS scripting environment. Runs the script afterwards.
	 */
	public void buildEnvironmentFromScript() {
		ScriptEngineManager factory = new ScriptEngineManager();
		String scriptFileName = directory + dirScripts + "config.js";
		
		// Get all script engines on the current platform.
		List<ScriptEngineFactory> list = factory.getEngineFactories();
		
		System.out.println("Script Engine Factories found:");
		for (ScriptEngineFactory f : list) {
			System.out.println(" Name = " + f.getEngineName() + " language = "
					+ f.getLanguageName() + " extensions = " + f.getExtensions());
		}
		
		//Get the JS engine
		jsEngine = factory.getEngineByName("js");
		
		//Run the specified script.
		
		this.executeScript(jsEngine, scriptFileName);
		
		cuGroup = (Group) jsEngine.get("cubeGroup");
		pyGroup = (Group) jsEngine.get("pyramidGroup");
	}
	
	/**
	 * Executes a specified script.
	 * 
	 * @param engine
	 *            - The scripting engine environment to run the script in.
	 * @param scriptFileName
	 *            - The file name of the script.
	 */
	private void executeScript(	ScriptEngine engine,
								String scriptFileName) {
		try {
			FileReader fileReader = new FileReader(scriptFileName);
			engine.eval(fileReader); //execute the script statements in the file
			fileReader.close();
		} catch (FileNotFoundException e1) {
			System.out.println(scriptFileName + " not found " + e1);
		} catch (IOException e2) {
			System.out.println("IO problem with " + scriptFileName + e2);
		} catch (ScriptException e3) {
			System.out.println("ScriptException in " + scriptFileName + e3);
		} catch (NullPointerException e4) {
			System.out.println("Null ptr exception in " + scriptFileName + e4);
		}
	}
	
	/**
	 * Helper method to initialize the physics engine.
	 */
	protected void initPhysicsSystem() {
		String engine = "sage.physics.JBullet.JBulletPhysicsEngine";
		physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEngine.initSystem();
		float[] gravity = { 0, -1f, 0 };
		physicsEngine.setGravity(gravity);
		
		// Apply physics properties to the world floor
		float up[] = { -0.05f, 0.95f, 0 }; // {0,1,0} is flat
		worldFloor = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(), floor
				.getWorldTransform().getValues(), up, 0.0f);
		worldFloor.setBounciness(1.0f);
		floor.setPhysicsObject(worldFloor);
		
		// Bind Physics Property to the SceneNodes
		bindPhysicsProperty(localPlayer, 1.0f, playerPObject);
	}
	
	/**
	 * Method to apply physics properties to a specified SceneNode.
	 * 
	 * @param object
	 *            - The SceneNode to apply physics to
	 * @param mass
	 *            - The mass of the SceneNode object
	 * @param pObject
	 *            - The container to keep the physics properties of the object.
	 */
	private void bindPhysicsProperty(	SceneNode object,
										float mass,
										IPhysicsObject pObject) {
		pObject = physicsEngine.addSphereObject(physicsEngine.nextUID(), mass, object
				.getWorldTransform().getValues(), 1.0f);
		pObject.setBounciness(1.0f);
		object.setPhysicsObject(pObject);
	}
	
	public Vector3D getPlayerPosition() {
		Vector3D position = localPlayer.getWorldTransform().getCol(3);
		
		return new Vector3D(position.getX(), position.getY(), position.getZ());
	}
	
	public void setIsConnected(boolean connected) {
		isConnected = connected;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public Avatar addGhostToGame(	float x,
									float y,
									float z) {
		Avatar ghost = new Avatar("Ghost " + ++ghostCount, 1, 20, 20, Color.red);
		ghost.translate(x, y, z);
		addGameWorldObject(ghost);
		
		return ghost;
	}
	
	public void removeGhostFromGame(Avatar ghost) {
		removeGameWorldObject(ghost);
	}
	
	public TreasureHuntClient getClient() {
		return gameClient;
	}
}
