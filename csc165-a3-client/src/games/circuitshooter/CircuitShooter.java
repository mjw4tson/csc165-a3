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
import java.util.UUID;
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
import sage.scene.SceneNode;
import sage.scene.TriMesh;
import sage.scene.bounding.BoundingVolume;
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
import engine.scene.controller.ProjectileController;
import engine.scene.hud.HUDNumber;
import engine.scene.physics.PhysicsManager;
import games.circuitshooter.network.CircuitShooterClient;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

/**
 * Represents the TreasureHunt game, utilizes SAGE libraries to create game logic.
 *
 * @author ktajeran
 */
public class CircuitShooter extends BaseGame implements MouseWheelListener {
	
	// Engine objects.
	public Camera3PController		cc1;
	private IDisplaySystem			display;												// The game display.
	private ICamera					camera;												// The game camera.
	private InputHandler			ih;													// Input handler
	private IInputManager			im;													// The input manager
	private IEventManager			eventManager;
	private IRenderer				renderer;
	private boolean					finalBuild			= true;							// Determines if final build
	private int						numCrashes			= 0;
	private Cursor					crossHairCursor;
	private HUDNumber				hudNumberManager;
	
	private HUDNumber				hScore;
	private HUDNumber				hHealth;
	
	// Game World Objects
	public HillHeightMap			myHillHeightMap;
	
	// Players
	public Avatar					localPlayer;
	public Vector<Avatar>			ghostPlayers;
	
	// SceneNode Controllers
	private ProjectileController	projCtrl			= new ProjectileController();
	
	// SceneNode Groups
	private Group					hudGroupTeamOne		= new Group("Team One Group");
	private Group					environmentGroup	= new Group("Environment Group");
	private Group					hudGroupTeamOneTime	= new Group("Team One Group Time");
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
	public double					xBound				= 1300f;
	public double					yBound				= 700f;
	private Random					r					= new Random();
	
	// Physics
	private PhysicsManager			phyManager;
	
	// Audio
	IAudioManager					audioMgr;
	AudioResource					ambientResource, pickUpResource, fireResource, deadResource,
			hitResource;
	private Sound					ambientSound;											// static and moving sound sources
	private Group					solarSystemGroup;
	
	/**
	 * Sets up the initial game.
	 */
	@Override
	public void initGame() {
		if (!runAsSinglePlayer())
			initGameClient();
		
		initPhysics();
		initAudio();
		configureEnvironment();
		initGameEntities(); // Populate the game world.
		addEventHandlers();
		initEventManager(); // Get event manager.
		cc1 = new Camera3PController(camera, localPlayer.getTriMesh());
		setupControls(); // Set up the game world controls.
		setEarParameters();
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
			gameClient.getOutputHandler().sendJoinMsg();
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
		setEarParameters();
		
		updateGameWorld(elapsedTimeMS);
		updateSceneNodeControllers(elapsedTimeMS);
		cc1.update(elapsedTimeMS);
		
		super.update(elapsedTimeMS);
	}
	
	/**
	 * Updates all SceneNode controllers.
	 */
	private void updateSceneNodeControllers(float elapsedTimeMS) {
		solarSystemGroup.rotate(1.01f, new Vector3D(0, 1, 0));
		projCtrl.update(elapsedTimeMS);
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
		fireResource = audioMgr.createAudioResource(directory + dirAudio + "fire.wav",
				AudioResourceType.AUDIO_SAMPLE);
		hitResource = audioMgr.createAudioResource(directory + dirAudio + "hit.wav",
				AudioResourceType.AUDIO_SAMPLE);
		pickUpResource = audioMgr.createAudioResource(directory + dirAudio + "get.wav",
				AudioResourceType.AUDIO_SAMPLE);
		deadResource = audioMgr.createAudioResource(directory + dirAudio + "dead.wav",
				AudioResourceType.AUDIO_SAMPLE);
		
		ambientSound = new Sound(ambientResource, SoundType.SOUND_MUSIC, 5, true);
		ambientSound.initialize(audioMgr);
		ambientSound.play();
	}
	
	public void setEarParameters() {
		Matrix3D avDir = (Matrix3D) (localPlayer.getTriMesh().getWorldRotation().clone());
		float camAz = cc1.getCameraAzimuth();
		avDir.rotateY(180.0f - camAz);
		Vector3D camDir = new Vector3D(0, 0, 1);
		camDir = camDir.mult(avDir);
		
		Vector3D locVec = localPlayer.getLocation();
		audioMgr.getEar().setLocation(new Point3D(locVec.getX(), locVec.getY(), locVec.getZ()));
		audioMgr.getEar().setOrientation(camDir, new Vector3D(0, 1, 0));
	}
	
	public AudioResource getFireResource() {
		return fireResource;
	}
	
	public AudioResource getHitResource() {
		return hitResource;
	}
	
	public AudioResource getDeadResource() {
		return deadResource;
	}
	
	public AudioResource getPickUpResource() {
		return pickUpResource;
	}
	
	public IAudioManager getAudioMgr() {
		return audioMgr;
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
		super.update(elapsedTimeMS);
		camera.setLocation(camera.getLocation());
		localPlayer.getTriMesh().updateAnimation(elapsedTimeMS * 4);
		
		moveSkybox(camera);
		
		Iterator<SceneNode> iterator = getGameWorld().iterator();
		SceneNode s;
		
		BoundingVolume avatarVol = localPlayer.getTriMesh().getWorldBound();
		
		while (iterator.hasNext()) {
			s = iterator.next();
			
			if (s instanceof Group) {
				Iterator<SceneNode> groupChildren = ((Group) s).getChildren();
				SceneNode gs;
				while (groupChildren.hasNext()) {
					gs = groupChildren.next();
					
					// Check if a bullet hit the local player.
					if (gs instanceof Projectile && gs.getWorldBound() != null
							&& gs.getWorldBound().intersects(avatarVol)) {
						Avatar bulletOwner = ((Projectile) (gs)).getSourceAvatar();
						
						if (bulletOwner != localPlayer) {
							System.out.println("Damaged by: " + bulletOwner.getUUID());
							localPlayer.setHealth(localPlayer.getHealth() - 30);
							localPlayer.playHit();
							
							if (gameClient != null) {
								gameClient.getOutputHandler().sendHitMsg(bulletOwner.getUUID(),
										localPlayer.isDead());
							}
						}
						
					}
					
					// Check if we hit a medic kit.
					if ((gs instanceof TriMesh && s.getName().equals("Health Box Group"))
							&& gs.getWorldBound() != null
							&& gs.getWorldBound().intersects(avatarVol)) {
						CrashEvent newCrash = new CrashEvent(++numCrashes);
						eventManager.triggerEvent(newCrash);
						groupChildren.remove();
						this.removeGameWorldObject(gs);
					}
				}
			}
		}
		
		if (localPlayer.isDead()) {
			localPlayer.respawn();
		}
		
		if(localPlayer.isHasChanged()){
			hHealth.updateValue((int) localPlayer.getHealth());
			hScore.updateValue((int) localPlayer.getTotalKills());
			localPlayer.setHasChanged(false);
		}
		
		removeOldProjectiles();

	}
	
	private void removeOldProjectiles() {
		Iterator<SceneNode> projectiles = projectileGroup.getChildren();
		ArrayList<Projectile> toRemove = new ArrayList<Projectile>();
		
		while (projectiles.hasNext()) {
			Projectile proj = (Projectile) projectiles.next();
			
			if (proj.isExpired()) {
				toRemove.add(proj);
			}
		}
		
		for (Projectile proj : toRemove) {
			projectileGroup.removeChild(proj);
		}
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
		camera.setPerspectiveFrustum(60, 2, 1, 2800);
		camera.setViewport(0.0, 1.0, 0.0, 1.0);
	}
	
	/**
	 * Builds the HUD.
	 */
	private void buildHUD() {

		hHealth = new HUDNumber("Health", directory, -0.845f, -0.878f);
		hScore = new HUDNumber("Score", directory, 0.05f, .902f);
		
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
		
		addGameWorldObject(hScore.getHUDNumber());
		addGameWorldObject(hHealth.getHUDNumber());
		
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
		healthGroup.scale(4, 4, 4);
		addGameWorldObject(healthGroup);
		
		// Add projectile group to game world
		addGameWorldObject(projectileGroup);
		projectileGroup.addController(projCtrl);
		projCtrl.addControlledNode(projectileGroup);
		
		addGameWorldObject(boundaryGroup);
		sceneManager.updateBoundaryEnvironment(boundaryGroup);
		
		solarSystemGroup = sceneManager.createSolarSystem();
		solarSystemGroup.translate(600, 300, 1000);
		solarSystemGroup.scale(.5f, .5f, .5f);
		addGameWorldObject(solarSystemGroup);
		
		// Add the player to the game world.
		localPlayer = new Avatar("Player 1", sceneManager.addAvatar(), this,
				gameClient != null ? gameClient.getUUID() : null);
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
		cleanupGameClient();
		cleanupAudio();
	}
	
	private void cleanupGameClient() {
		if (gameClient != null) {
			try {
				gameClient.getOutputHandler().sendByeMessage();
				gameClient.shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void cleanupAudio() {
		ambientResource.unload();
		pickUpResource.unload();
		fireResource.unload();
		deadResource.unload();
		hitResource.unload();
		
		audioMgr.shutdown();
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
			System.out.println("Name = " + f.getEngineName() + " language = " + f.getLanguageName()
					+ " extensions = " + f.getExtensions());
		}
		
		//Get the JS engine
		jsEngine = factory.getEngineByName("js");
		
		//Run the specified script.
		
		this.executeScript(jsEngine, scriptFileName);
		
		boundaryGroup = (Group) jsEngine.get("boundaryGroup");
		xBound = (double) jsEngine.get("xBound");
		yBound = (double) jsEngine.get("yBound");
		
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
		return localPlayer.getLocation();
	}
	
	public IAudioManager getAudioManager() {
		return audioMgr;
	}
	
	public void setIsConnected(boolean connected) {
		isConnected = connected;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public Avatar addGhostToGame(	UUID uuid,
									float x,
									float y,
									float z) {
		Avatar ghost = new Avatar("Ghost " + ++ghostCount, sceneManager.addAvatar(), this, uuid);
		ghost.getTriMesh().translate(x, y, z);
		addGameWorldObject(ghost.getTriMesh());
		
		return ghost;
	}
	
	public void removeGhostFromGame(Avatar ghost) {
		removeGameWorldObject(ghost.getTriMesh());
	}
	
	public GhostNPC addNpcToGame(	UUID id,
									float x,
									float y,
									float z) {
		GhostNPC npc = new GhostNPC(id, new Point3D(x, y, z));
		addGameWorldObject(npc);
		System.out.println("Adding NPC: " + id + "\tat point: " + new Point3D(x, y, z));
		return npc;
	}
	
	public CircuitShooterClient getClient() {
		return gameClient;
	}
	
	public void fire(Avatar player) {
		Projectile projectile = new Projectile(this, player);
		projectileGroup.addChild(projectile);
		player.playFire();
		
		if (gameClient != null && player == localPlayer) {
			gameClient.getOutputHandler().sendProjectileMsg();
		}
	}
	
	/**
	 * Obtains a random positive/negative integer
	 */
	public int getRandomSignedInteger(int limit) {
		int temp = r.nextInt(limit);
		
		if (r.nextBoolean()) {
			temp = -temp;
		}
		
		return temp;
	}
}
