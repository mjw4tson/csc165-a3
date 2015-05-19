package games.circuitshooter;

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
import java.util.Random;
import java.util.Vector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sage.app.BaseGame;
import sage.audio.AudioManagerFactory;
import sage.audio.AudioResource;
import sage.audio.AudioResourceType;
import sage.audio.IAudioManager;
import sage.audio.Sound;
import sage.audio.SoundType;
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
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;
import sage.scene.TriMesh;
import sage.scene.shape.Cube;
import sage.scene.shape.Pyramid;
import sage.scene.shape.Rectangle;
import sage.terrain.HillHeightMap;
import sage.util.VersionInfo;
import engine.event.CrashEvent;
import engine.graphics.GameDisplaySystem;
import engine.input.InputHandler;
import engine.input.action.camera.Camera3PController;
import engine.objects.Avatar;
import engine.objects.GhostNPC;
import engine.objects.Projectile;
import engine.scene.SceneManager;
import engine.scene.controller.BounceController;
import engine.scene.controller.ScaleController;
import engine.scene.hud.HUDNumber;
import engine.scene.physics.PhysicsManager;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

/**
 * Represents the TreasureHunt game, utilizes SAGE libraries to create game logic.
 *
 * @author ktajeran
 */
public class CircuitShooter extends BaseGame implements MouseWheelListener,
		java.awt.event.MouseListener {
	
	// Engine objects.
	public Camera3PController		cc1;
	private IDisplaySystem			display;												// The game display.
	private ICamera					camera;												// The game camera.
	private InputHandler			ih;													// Input handler
	private IInputManager			im;													// The input manager
	private IEventManager			eventManager;
	private IRenderer				renderer;
	private boolean					finalBuild			= true;							// Determines if final build
	private float					time				= 0.0f;							// Stores the total time
	private int						numCrashes			= 0;
	private Cursor					crossHairCursor;
	private HUDNumber				hudNumberManager;
	
	// Game World Objects
	public HillHeightMap			myHillHeightMap;
	
	// Players
	public Avatar					localPlayer;
	public Vector<Avatar>			ghostPlayers;
	
	// SceneNode Controllers
	private BounceController		roSNController		= new BounceController();
	private ScaleController			scSNController		= new ScaleController();
	
	// SceneNode Groups
	private Group					hudGroupTeamOne		= new Group("Team One Group");
	private Group					environmentGroup	= new Group("Environment Group");
	private Group					hudGroupTeamOneTime	= new Group("Team One Group Time");
	private Group					ammoGroup			= new Group("Ammo Box Group");
	private Group					fenceGroup			= new Group("Fence Group");
	private Group					healthGroup			= new Group("Health Box Group");
	private Group					projectileGroup		= new Group("Projectile Group");
	private Group					boundaryGroup		= new Group("Boudnary Group");
	
	// Directory related
	private static String			directory			= "." + File.separator;
	private String					dirHud				= "images" + File.separator + "hud"
																+ File.separator;
	private String					dirScripts			= "scripts" + File.separator;
	private String					dirAudio			= "audio" + File.separator;
	
	// Scripting
	private ScriptEngine			jsEngine;
	
	// HUD
	private HUDImage				p1Time;
	private HUDImage				p1Score;
	private float					origin				= 65f;
	
	// Game Client
	private CircuitShooterClient	gameClient;
	private String					serverAddr;
	private int						serverPort;
	private ProtocolType			pType;
	private boolean					isConnected;
	private static int				ghostCount			= 0;
	
	// Environment
	private SceneManager			sceneManager;
	private float					xBound				= 1300f;
	private float					yBound				= 700f;
	private Random					r					= new Random();
	
	// Physics
	private PhysicsManager			phyManager;
	
	// Audio
	IAudioManager					audioMgr;
	AudioResource					ambientResource, pickUpResource, fireResource, deadResource;
	private Sound					ambientSound, pickUp, fire, dead;								// static and moving sound sources
	private Group					solarSystemGroup;
	
	/**
	 * Sets up the initial game.
	 */
	@Override
	public void initGame() {
		if (!runAsSinglePlayer())
			initGameClient();
		
		initPhysics();
		configureEnvironment();
		initGameEntities(); // Populate the game world.
		addEventHandlers();
		initEventManager(); // Get event manager.
		cc1 = new Camera3PController(camera, localPlayer.getTriMesh());
		setupControls(); // Set up the game world controls.
		initAudio();
		
	}
	
	private void initPhysics() {
		phyManager = new PhysicsManager();
	}
	
	public PhysicsManager getPhysicsManager() {
		return phyManager;
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
		
		try {
			gameClient = new CircuitShooterClient(InetAddress.getByName(serverAddr), serverPort,
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
		
		phyManager.updatePhysicsState(getGameWorld());
		
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
		solarSystemGroup.rotate(1.01f, new Vector3D(0, 1, 0));
	}
	
	/**
	 * This helper method handles the game initialization logic.
	 */
	private void initEventManager() {
		eventManager = EventManager.getInstance();
		eventManager.addListener(localPlayer, CrashEvent.class);
		super.update(0.0f);
	}
	
	public void initAudio() {
		audioMgr = AudioManagerFactory.createAudioManager("sage.audio.joal.JOALAudioManager");
		
		if (!audioMgr.initialize()) {
			System.out.println("Audio Manager failed to initialize!");
			return;
		}
		
		ambientResource = audioMgr.createAudioResource(directory + dirAudio + "test.wav",
				AudioResourceType.AUDIO_SAMPLE);
		pickUpResource = audioMgr.createAudioResource(directory + dirAudio + "get.wav",
				AudioResourceType.AUDIO_SAMPLE);
		fireResource = audioMgr.createAudioResource(directory + dirAudio + "fire.wav",
				AudioResourceType.AUDIO_SAMPLE);
		deadResource = audioMgr.createAudioResource(directory + dirAudio + "dead.wav",
				AudioResourceType.AUDIO_SAMPLE);
		
		ambientSound = new Sound(ambientResource, SoundType.SOUND_MUSIC, 5, true);
		pickUp = new Sound(pickUpResource, SoundType.SOUND_EFFECT, 5, false);
		fire = new Sound(fireResource, SoundType.SOUND_EFFECT, 5, false);
		dead = new Sound(deadResource, SoundType.SOUND_EFFECT, 5, false);
		
		ambientSound.initialize(audioMgr);
		pickUp.initialize(audioMgr);
		fire.initialize(audioMgr);
		dead.initialize(audioMgr);
		
		pickUp.setMaxDistance(50.0f);
		pickUp.setMinDistance(3.0f);
		pickUp.setRollOff(5.0f);
		
		fire.setMaxDistance(50.0f);
		fire.setMinDistance(3.0f);
		fire.setRollOff(5.0f);
		
		dead.setMaxDistance(50.0f);
		dead.setMinDistance(3.0f);
		dead.setRollOff(5.0f);
		
		setEarParameters();
		ambientSound.play();
		
	}
	
	public void setEarParameters() {
		Matrix3D avDir = (Matrix3D) (localPlayer.getTriMesh().getWorldRotation().clone());
		float camAz = cc1.getCameraAzimuth();
		avDir.rotateY(180.0f - camAz);
		Vector3D camDir = new Vector3D(0, 0, 1);
		camDir = camDir.mult(avDir);
		audioMgr.getEar().setLocation(new Point3D(0, 0, 0));
		audioMgr.getEar().setOrientation(camDir, new Vector3D(0, 1, 0));
	}
	
	/**
	 * Moves the SkyBox to account for any player movement.
	 */
	private void moveSkybox(ICamera cam) {
		Point3D camLoc = cam.getLocation();
		Matrix3D camTranslation = new Matrix3D();
		camTranslation.translate(camLoc.getX(), camLoc.getY(), camLoc.getZ());
		sceneManager.getSkyBox().setLocalTranslation(camTranslation);
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
		float temp;
		camera.setLocation(camera.getLocation());
		localPlayer.getTriMesh().updateAnimation(elapsedTimeMS * 4);
		
		moveSkybox(camera);
		
		Iterator<SceneNode> iterator = getGameWorld().iterator();
		SceneNode s;
		
		Point3D locP1 = new Point3D(localPlayer.getTriMesh().getLocalTranslation().getCol(3));
		
		while (iterator.hasNext()) {
			s = iterator.next();
			
			if (s instanceof Group) {
				Iterator<SceneNode> groupChildren = ((Group) s).getChildren();
				SceneNode gs;
				while (groupChildren.hasNext()) {
					gs = groupChildren.next();
					
					// Check if a bullet hit the local player.
					if (gs.getWorldBound() != null && gs.getWorldBound().contains(locP1)
							&& (gs instanceof Projectile)) {
						Avatar bulletOwner = ((Projectile) (gs)).getSourceAvatar();
						
						if (bulletOwner != localPlayer) {
							System.out.println("Damaged by: " + bulletOwner.getUUID());
							localPlayer.setHealth(localPlayer.getHealth() - 30);
						}
						
					}
					
					// Check if we hit a medic kit.
					if (gs.getWorldBound() != null && gs.getWorldBound().contains(locP1)
							&& (gs instanceof TriMesh && s.getName().equals("Health Box Group"))) {
						groupChildren.remove();
						this.removeGameWorldObject(gs);
						CrashEvent newCrash = new CrashEvent(numCrashes);
						eventManager.triggerEvent(newCrash);
						pickUp.play();
					}
					// Check if we hit a wall, move backwards away from the wall if we do. This can be improved by utilizing the 
					// physics engine if time permits.
					if (locP1.getX() >= xBound) {
						temp = (float) ((locP1.getX() - xBound) * -1);
						localPlayer.getTriMesh().translate(temp, 0, 0);
					} else if (locP1.getX() <= -xBound) {
						temp = (float) ((locP1.getX() + xBound) * -1);
						localPlayer.getTriMesh().translate(temp, 0, 0);
					} else if (locP1.getZ() >= yBound) {
						temp = (float) ((locP1.getZ() - yBound) * -1);
						localPlayer.getTriMesh().translate(0, 0, temp);
					} else if (locP1.getZ() <= -yBound) {
						temp = (float) ((locP1.getZ() + yBound) * -1);
						localPlayer.getTriMesh().translate(0, 0, temp);
					}
				}
			}
		}
		
		if (localPlayer.isDead()) {
			respawn();
		}
		
		removeGameWorldObject(hudGroupTeamOne);
		removeGameWorldObject(hudGroupTeamOneTime);
		
		hudGroupTeamOne = hudNumberManager.printValues((int) localPlayer.getHealth(), -0.850f,
				-0.88f);
		hudGroupTeamOneTime = hudNumberManager.printValues(localPlayer.getTotalKills(), 0.050f,
				0.90f);
		
		addGameWorldObject(hudGroupTeamOne);
		addGameWorldObject(hudGroupTeamOneTime);
		
	}
	
	/**
	 * Helper method that builds and initializes game world entities.
	 */
	public void initGameEntities() {
		sceneManager = new SceneManager(directory);
		buildCamera();
		buildHUD();
		loadGameWorldObjects();
		addGameWorldObject(sceneManager.initTerrain(display));
		
	}
	
	/**
	 * Method that handles the logic of creating and setting the camera(s) in the game world.
	 */
	private void buildCamera() {
		// Setup camera and display.
		renderer = getDisplaySystem().getRenderer();
		
		// Set crosshair as the defaut cursor.
		crossHairCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		renderer.getCanvas().setCursor(crossHairCursor);
		
		camera = new JOGLCamera(renderer);
		camera.setPerspectiveFrustum(60, 2, 1, 1000);
		camera.setViewport(0.0, 1.0, 0.0, 1.0);
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
		camera.addToHUD(p1Time);
		
		// Add P1 Score
		p1Score = new HUDImage(directory + dirHud + "p1_score.png");
		p1Score.setName("Player 1 Score");
		p1Score.setLocation(-.9f, -0.88f);
		p1Score.rotateImage(180);
		p1Score.scale(.1570f, .0575f, .1f);
		p1Score.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		camera.addToHUD(p1Score);
		
		hudGroupTeamOne = hudNumberManager.printValues(0, -0.850f, -0.88f);
		hudGroupTeamOneTime = hudNumberManager.printValues(0, 0.050f, 0.90f);
		
		addGameWorldObject(hudGroupTeamOne);
		addGameWorldObject(hudGroupTeamOneTime);
		
	}
	
	/**
	 * This method is tasked with building and placing game world objects in the world.
	 */
	private void loadGameWorldObjects() {
		// Add a skybox;
		addGameWorldObject(sceneManager.addSkybox(this, origin));
		
		// Create the game world floor.
		sceneManager.addGameFloor(environmentGroup, phyManager);
		addGameWorldObject(environmentGroup);
		
		// Get and build game world objects
		buildEnvironmentFromScript();
		
		// Adding health box to the game world.
		sceneManager.addHealthBoxes(healthGroup, phyManager);
		healthGroup.scale(10, 10, 10);
		addGameWorldObject(healthGroup);
		
		// Add projectile group to game world
		addGameWorldObject(projectileGroup);
		
		addGameWorldObject(boundaryGroup);
		sceneManager.updateBoundaryEnvironment(boundaryGroup);
		
		solarSystemGroup = sceneManager.createSolarSystem();
		solarSystemGroup.translate(600, 300, 1000);
		solarSystemGroup.scale(.5f, .5f, .5f);
		addGameWorldObject(solarSystemGroup);
		
		// Add the player to the game world.
		localPlayer = new Avatar("Player 1", sceneManager.addAvatar());
		localPlayer.getTriMesh().translate(50, 4.0f, 10 + origin);
		localPlayer.getTriMesh().startAnimation("my_animation");
		addGameWorldObject(localPlayer.getTriMesh());
	}
	
	/**
	 * Initializes the game core systems.
	 */
	protected void initSystem() {
		// call a local method to create a DisplaySystem object
		display = new GameDisplaySystem(1920, 1080, 32, 60, false,
				"sage.renderer.jogl.JOGLRenderer", "Circuit Shooter");
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
		this.getDisplaySystem().getRenderer().getCanvas().addMouseListener(this);
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
		im = ih.setupControls(camera, im, this, sceneManager);
	}
	
	/**
	 * Handles game rendering.
	 */
	protected void render() {
		renderer.setCamera(camera);
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
		
		boundaryGroup = (Group) jsEngine.get("boundaryGroup");
		xBound = (int) jsEngine.get("xBound");
		yBound = (int) jsEngine.get("yBound");
		
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
	
	public Vector3D getPlayerPosition() {
		Vector3D position = localPlayer.getTriMesh().getWorldTransform().getCol(3);
		
		return new Vector3D(position.getX(), position.getY(), position.getZ());
	}
	
	public IAudioManager getAudioManager() {
		return this.audioMgr;
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
		Avatar ghost = new Avatar("Ghost " + ++ghostCount, sceneManager.addAvatar());
		ghost.getTriMesh().translate(x, y, z);
		addGameWorldObject(ghost.getTriMesh());
		
		return ghost;
	}
	
	public void removeGhostFromGame(Avatar ghost) {
		removeGameWorldObject(ghost.getTriMesh());
	}
	
	public GhostNPC addNpcToGame(	int id,
									float x,
									float y,
									float z) {
		GhostNPC npc = new GhostNPC(id, new Vector3D(x, y, z));
		addGameWorldObject(npc);
		return npc;
	}
	
	public CircuitShooterClient getClient() {
		return gameClient;
	}
	
	public void fire(Avatar player) {
		Projectile projectile = new Projectile(this, player);
		projectileGroup.addChild(projectile);
		fire.play();
		
		if (gameClient != null) {
			gameClient.sendProjectileMsg();
		}
	}
	
	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
		ih.getFireAction().performAction(0, null);
	}
	
	// Unused interface methods
	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) {
	}
	
	@Override
	public void mouseExited(java.awt.event.MouseEvent e) {
	}
	
	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
	}
	
	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {
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
	
	private void respawn() {
		dead.play();
		localPlayer.setHealth(100);
		
		Point3D locP1 = new Point3D(localPlayer.getTriMesh().getLocalTranslation().getCol(3));
		Vector3D newLoc = new Vector3D(getRandomSignedInteger(1200), locP1.getY(),
				getRandomSignedInteger(600));
		move(localPlayer.getTriMesh(), locP1, newLoc);
	}
	
	private void move(	TriMesh object,
						Point3D oldLocation,
						Vector3D newLocation) {
		object.translate((float) (newLocation.getX() - oldLocation.getX()), 0,
				(float) (newLocation.getZ() - oldLocation.getZ()));
	}
}
