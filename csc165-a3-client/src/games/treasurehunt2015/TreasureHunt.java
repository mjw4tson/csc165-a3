package games.treasurehunt2015;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.InputManager;
import sage.networking.IGameConnection.ProtocolType;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.HUDImage;
import sage.scene.SceneNode;
import sage.scene.SkyBox;
import sage.scene.SkyBox.Face;
import sage.scene.shape.Cube;
import sage.scene.shape.Pyramid;
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
public class TreasureHunt extends BaseGame implements MouseWheelListener{
	
	// Engine objects.
	public Camera3PController	cc1;
	private IDisplaySystem		display;    						// The game display.
	private ICamera				camera1;						// The game camera.
	private InputHandler		ih;																	// Input handler
	private IInputManager		im;																	// The input manager
	private IEventManager		eventManager;
	private IRenderer			renderer;
	private boolean				finalBuild			= false;											// Determines if final build
	private float				time				= 0.0f;											// Stores the total time
	private int					scoreP1				= 0;												// The total score
	private int					numCrashes			= 0;
	private Cursor				crossHairCursor;
	private BlendState			transparencyState;
	private HUDNumber			hudNumberManager;
	
	private static String		directory			= "." + File.separator + "bin" + File.separator;
	
	// Game World Objects
	private Axis				worldAxis;
	private Pyramid				pyr1;
	private Pyramid				pyr2;
	private Pyramid				pyr3;
	private Pyramid				pyr4;
	private Cube				cu1;
	private Cube				cu2;
	private Cube				cu3;
	public Cube					cu4;
	private Cube				cu5;
	private TreasureChest		treasureChest;
	private SkyBox				skybox;
	public HillHeightMap		myHillHeightMap;
	public TerrainBlock			hillTerrain;
	
	// Players
	public Avatar				player1;
	public Vector<Avatar>		ghostPlayers;
	
	// SceneNode Controllers
	private BounceController	roSNController		= new BounceController();
	private ScaleController		scSNController		= new ScaleController();
	
	// SceneNode Groups
	private Group				cuGroup				= new Group("Cube Group");
	private Group				pyGroup				= new Group("Pyramid Group");
	private Group				hudGroupTeamOne		= new Group("Team One Group");

	private Group				hudGroupTeamOneTime	= new Group("Team One Group Time");

	
	// Texture Objects
	private Texture				skyBoxTextureTop;
	private Texture				skyBoxTextureBack;
	private Texture				skyBoxTextureEast;
	private Texture				skyBoxTextureWest;
	private Texture				skyBoxTextureFront;
	private Texture				skyBoxTextureBot;
	
	private String				dirEnvironment		= "images" + File.separator + "environment" + File.separator;
	private String				dirHud				= "images" + File.separator + "hud" + File.separator;
	//private String				dirModel			= "images" + File.separator + "models" + File.separator;
	
	// HUD
	private HUDImage			p1Time;
	private HUDImage			p1Score;
	private float				origin				= 65f;
	
	// Game Client
	private TreasureHuntClient  gameClient;
	private String 				serverAddr;
	private int					serverPort;
	private ProtocolType		pType;
	private boolean				isConnected;
	
	/*
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
		addEventHandlers();
		initEventManager(); // Get event manager.
		cc1 = new Camera3PController(camera1, player1);
		setupControls(); // Set up the game world controls.
	}

	/**
	 * Determine if game is single player
	 */
	private boolean runAsSinglePlayer() {
		Scanner s = new Scanner(System.in);
		String singlePlayer;
		
		do {
			System.out.print("Single player mode (Y/N): ");
			singlePlayer = s.next();
		} while (!"Y".equalsIgnoreCase(singlePlayer) && !"N".equalsIgnoreCase(singlePlayer)); 
		
		s.close();
		
		return "Y".equalsIgnoreCase(singlePlayer);
	}
	
	/**
	 * Initializes game client
	 */
	private void initGameClient() {
		Scanner s = new Scanner(System.in);
		System.out.print("Enter server address: ");
		serverAddr = s.next();
		
		System.out.print("Enter server port: ");
		serverPort = s.nextInt();
		s.close();
		
		pType = ProtocolType.TCP;
		
		try {
			gameClient = new TreasureHuntClient(InetAddress.getByName(serverAddr), serverPort, pType, this);
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
	
	/*
	 * Handles logic for update the game state upon every engine loop iteration..
	 */
	public void update(float elapsedTimeMS) {
		if (gameClient != null) {
			gameClient.processPackets();
		}
		
		updateGameWorld(elapsedTimeMS);
		updateSceneNodeControllers();
		cc1.update(elapsedTimeMS);
		
		super.update(elapsedTimeMS);
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
		
		Point3D locP1 = new Point3D(player1.getLocalTranslation().getCol(3));
		
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

		// Iterate through each hud image and update as necessary
		
		Iterator<SceneNode> iteratorOne = hudGroupTeamOne.getChildren();
		Iterator<SceneNode> iteratorOneTime = hudGroupTeamOneTime.getChildren();

		
		SceneNode s2;
		
		// Clean up images from previous update
		while (iteratorOne.hasNext()) {
			s2 = iteratorOne.next();
			camera1.removeFromHUD(s2);
		}

		while (iteratorOneTime.hasNext()) {
			s2 = iteratorOneTime.next();
			camera1.removeFromHUD(s2);
		}
		
	
		
		int timeTemp = (int) time / 1000;
		
		hudGroupTeamOne = hudNumberManager.printValues(scoreP1, -0.850f, -0.88f);
		hudGroupTeamOneTime = hudNumberManager.printValues(timeTemp, 0.050f, 0.90f);

		iteratorOne = hudGroupTeamOne.getChildren();
		
		
		iteratorOneTime = hudGroupTeamOneTime.getChildren();

		
		while (iteratorOne.hasNext()) {
			s2 = iteratorOne.next();
			s2.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
			camera1.addToHUD(s2);
		}

		
		while (iteratorOneTime.hasNext()) {
			s2 = iteratorOneTime.next();
			s2.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
			camera1.addToHUD(s2);
		}
	
		
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
		Texture grassTexture = TextureManager.loadTexture2D(directory + dirEnvironment
				+ "grass_tile.jpg");
		grassTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		grassState = (TextureState) display.getRenderer().createRenderState(
				RenderState.RenderStateType.Texture);
		grassState.setTexture(grassTexture, 0);
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
		
		// Load Textures
		skyBoxTextureTop = TextureManager.loadTexture2D(skyTop);
		skyBoxTextureBot = TextureManager.loadTexture2D(skyBot);
		skyBoxTextureEast = TextureManager.loadTexture2D(skyEast);
		skyBoxTextureFront = TextureManager.loadTexture2D(skyFront);
		skyBoxTextureWest = TextureManager.loadTexture2D(skyWest);
		skyBoxTextureBack = TextureManager.loadTexture2D(skyBack);
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
		addGameWorldObject(skybox); //TODO FIX
	
		// Add a pyramid into the game world.
		pyr1 = new Pyramid(); // white by default
		pyr1.translate(95, .3f, 30 + origin);
		
		// Add a pyramid into the game world.
		pyr2 = new Pyramid(); // white by default
		pyr2.translate(40, .3f, 68 + origin);
		
		// Add a pyramid into the game world.
		pyr3 = new Pyramid(); // white by default
		pyr3.translate(50, .3f, 30 + origin);
		
		// Add a pyramid into the game world.
		pyr4 = new Pyramid(); // white by default
		pyr4.translate(80,.3f, 30 + origin);
		
		// Add a cube into the game world.
		cu1 = new Cube();
		cu1.translate(37, .3f, 30 + origin);
		
		// Add a cube into the game world.
		cu2 = new Cube();
		cu2.translate(60, .3f, 75 + origin);
		
		// Add a cube into the game world.
		cu3 = new Cube();
		cu3.translate(56, .3f, 45 + origin);
		
		// Add a cube into the game world.
		cu4 = new Cube();
		cu4.translate(75, .3f, 25 + origin);
		
		// Add a cube into the game world.
		cu5 = new Cube();
		cu5.translate(20, .3f, 5 + origin);
		
		// Populate the Cube Group
		cuGroup.addChild(cu1);
		cuGroup.addChild(cu2);
		cuGroup.addChild(cu3);
		cuGroup.addChild(cu4);
		cuGroup.addChild(cu5);
		
		// Populate the Pyramid Group
		pyGroup.addChild(pyr1);
		pyGroup.addChild(pyr2);
		pyGroup.addChild(pyr3);
		pyGroup.addChild(pyr4);
		
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
		player1 = new Avatar("Player 1", 1, 20, 20, Color.blue);
		player1.translate(50, .8f, 10 + origin);
		
		addGameWorldObject(player1);
		
		// Add a 3D axis to the game world.
		worldAxis = new Axis(50, Color.red, Color.green, Color.blue);
		addGameWorldObject(worldAxis);
	}

	/*
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
	
	/*
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
	
	/*
	 * Handles game rendering.
	 */
	protected void render() {
		renderer.setCamera(camera1);
		super.render();		
	}

	/* 
	 * Handles zooming in the 3P camera for player 1 with the mouse scroll wheel.
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int rotations = e.getWheelRotation();
		if(rotations < 0 ){
			cc1.zoomIn(2f);
		} else {
			cc1.zoomOut(2f);
		}
		
	}
	
	public Vector3D getPlayerPosition() {
        Vector3D position = player1.getWorldTransform().getCol(3);
        
        return new Vector3D(position.getX(), position.getY(), position.getZ());
    }
	
	public void setIsConnected(boolean connected) {
		isConnected = connected;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
}
