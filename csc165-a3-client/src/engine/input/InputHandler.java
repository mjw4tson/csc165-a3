package engine.input;

import engine.input.action.camera.MoveAction.Direction;
import engine.input.action.camera.MoveNodeAction;
import engine.input.action.camera.OrbitLeftRightAction;
import engine.input.action.camera.OrbitUpDownAction;
import engine.input.action.camera.SetLockedAction;
import engine.input.action.camera.SetSpeedAction;
import engine.input.action.camera.ZoomAction;
import games.treasurehunt2015.TreasureHunt;

import java.util.ArrayList;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import sage.camera.ICamera;
import sage.input.IInputManager;
import sage.input.IInputManager.INPUT_ACTION_TYPE;
import sage.input.action.IAction;
import sage.input.action.QuitGameAction;

/**
 * This class encapsulates the logic for handling input.
 *
 * @author ktajeran
 */
public class InputHandler {
	private IInputManager			im;
	private String					gpName;
	private String					kbName;
	private String					moName;
	private ArrayList<Controller>	devices;
	private Component[]				contComponents;
	private Controller				gameController;
	private boolean					finalBuild	= false;
	
	public InputHandler(IInputManager inmanager) {
		im = inmanager;
		gpName = im.getFirstGamepadName();
		kbName = im.getKeyboardName();
		moName = im.getMouseName();
		System.out.println("Input Devices Recognized: ");
		System.out.println("Gamepad - " + gpName);
		System.out.println("Keyboard - " + kbName);
		System.out.println("Mouse - " + moName);
		
		// Get controller and components.
		gameController = im.getControllerByName(gpName);
		if (gameController != null) {
			contComponents = gameController.getComponents();
		}
		
		// Get all devices.
		devices = im.getControllers();
		
		for (Controller cont : devices) {
			System.out.println("CONTROLLER: " + cont.getName());
		}
		
	}
	
	/**
	 * Get the game pad name.
	 *
	 * @return - The GP name.
	 */
	public String getGPName() {
		return this.gpName;
	}
	
	/**
	 * Get the keyboard name.
	 *
	 * @return - The keyboard name.
	 */
	public String getKBName() {
		return this.kbName;
	}
	
	/**
	 * Adds a specified control with the provided IAction
	 */
	public void addControl(	Identifier gpKey,
							Identifier kbKey,
							Identifier moKey,
							IAction action,
							INPUT_ACTION_TYPE gpActionType,
							INPUT_ACTION_TYPE kbActionType,
							INPUT_ACTION_TYPE moActionType) {
		// Add key mappings for each input type if bindings are supplied.
		if (gpName != null && gpKey != null) {
			im.associateAction(gpName, gpKey, action, gpActionType);
		}
		if (kbName != null && kbKey != null) {
			if (finalBuild == false) {
				im.associateAction(devices.get(3), kbKey, action, kbActionType);
			} else {
				im.associateAction(kbName, kbKey, action, kbActionType);
			}
			
		}
		if (moName != null && moKey != null) {
			im.associateAction(moName, moKey, action, moActionType);
			
		}
		
	}
	
	private void setupPlayerOne(TreasureHunt bg) {
		// Controls for the first player, using mouse and keyboard.
		SetSpeedAction setSpeed = new SetSpeedAction(bg.player1);
		SetLockedAction setLocked = new SetLockedAction(bg.player1);
		
		MoveNodeAction mvNodeLeft = new MoveNodeAction(bg.player1, Direction.LEFT, setSpeed,
				bg.hillTerrain);
		MoveNodeAction mvNodeRight = new MoveNodeAction(bg.player1, Direction.RIGHT, setSpeed,
				bg.hillTerrain);
		MoveNodeAction mvNodeForward = new MoveNodeAction(bg.player1, Direction.FORWARD, setSpeed,
				bg.hillTerrain);
		MoveNodeAction mvNodeBackward = new MoveNodeAction(bg.player1, Direction.BACKWARD,
				setSpeed, bg.hillTerrain);
		
		OrbitLeftRightAction orbitRight = new OrbitLeftRightAction(false, true, bg.cc1, setLocked,
				bg.player1);
		OrbitLeftRightAction orbitLeft = new OrbitLeftRightAction(false, false, bg.cc1, setLocked,
				bg.player1);
		OrbitUpDownAction orbitUp = new OrbitUpDownAction(false, true, bg.cc1, setLocked,
				bg.player1);
		OrbitUpDownAction orbitDown = new OrbitUpDownAction(false, false, bg.cc1, setLocked,
				bg.player1);
		
		// Orbit Up.
		this.addControl(null, net.java.games.input.Component.Identifier.Key.UP, null, orbitUp,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Orbit Down.
		this.addControl(null, net.java.games.input.Component.Identifier.Key.DOWN, null, orbitDown,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Orbit Right.
		this.addControl(null, net.java.games.input.Component.Identifier.Key.RIGHT, null,
				orbitRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Orbit Left.
		this.addControl(null, net.java.games.input.Component.Identifier.Key.LEFT, null, orbitLeft,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move forward.
		this.addControl(null, net.java.games.input.Component.Identifier.Key.W, null, mvNodeForward,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move left.
		this.addControl(null, net.java.games.input.Component.Identifier.Key.A, null, mvNodeLeft,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move right.
		this.addControl(null, net.java.games.input.Component.Identifier.Key.D, null, mvNodeRight,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move backward.
		this.addControl(null, net.java.games.input.Component.Identifier.Key.S, null,
				mvNodeBackward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Set speed action.
		this.addControl(null, net.java.games.input.Component.Identifier.Key.LSHIFT, null, setSpeed,
				null, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE, null);
		
		// Set Locked action.
		this.addControl(null, net.java.games.input.Component.Identifier.Key.SPACE, null, setLocked,
				null, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE, null);
		
	}
	
	private void setupController(TreasureHunt bg) {
		// Controls for the first player, using mouse and keyboard.
		SetSpeedAction setSpeed = new SetSpeedAction(bg.player1);
		SetLockedAction setLocked = new SetLockedAction(bg.player1);
		
		MoveNodeAction mvNodeLeft = new MoveNodeAction(bg.player1, Direction.LEFT, setSpeed,
				bg.hillTerrain);
		MoveNodeAction mvNodeRight = new MoveNodeAction(bg.player1, Direction.RIGHT, setSpeed,
				bg.hillTerrain);
		MoveNodeAction mvNodeForward = new MoveNodeAction(bg.player1, Direction.FORWARD, setSpeed,
				bg.hillTerrain);
		MoveNodeAction mvNodeBackward = new MoveNodeAction(bg.player1, Direction.BACKWARD,
				setSpeed, bg.hillTerrain);
		
		OrbitLeftRightAction orbitLeftRight = new OrbitLeftRightAction(true, true, bg.cc1,
				setLocked, bg.player1);
		
		OrbitUpDownAction orbitUpDown = new OrbitUpDownAction(true, false, bg.cc1, setLocked,
				bg.player1);
		
		ZoomAction zoomOutP2 = new ZoomAction(bg.cc1, true, 2);
		ZoomAction zoomInP2 = new ZoomAction(bg.cc1, false, 2);
		
		// Zoom in P1
		this.addControl(net.java.games.input.Component.Identifier.Button._4, null, null, zoomInP2,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Zoom out P1
		this.addControl(net.java.games.input.Component.Identifier.Button._5, null, null, zoomOutP2,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Orbit Up and down.
		this.addControl(net.java.games.input.Component.Identifier.Axis.RY, null, null, orbitUpDown,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		//Orbit Left and Right.
		this.addControl(net.java.games.input.Component.Identifier.Axis.RX, null, null,
				orbitLeftRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move forward.
		this.addControl(net.java.games.input.Component.Identifier.Axis.Y, null, null,
				mvNodeForward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move left.
		this.addControl(net.java.games.input.Component.Identifier.Axis.X, null, null, mvNodeLeft,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move right.
		this.addControl(net.java.games.input.Component.Identifier.Axis.X, null, null, mvNodeRight,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move backward.
		this.addControl(net.java.games.input.Component.Identifier.Axis.Y, null, null,
				mvNodeBackward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Set speed action.
		this.addControl(net.java.games.input.Component.Identifier.Button._0, null, null, setSpeed,
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE,
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE, null);
		
		// Set Locked action.
		this.addControl(net.java.games.input.Component.Identifier.Button._1, null, null, setLocked,
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE,
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE, null);
	}
	
	public IInputManager setupControls(	ICamera camera1,
										IInputManager im,
										TreasureHunt bg) {
		float rotationAmnt = 0.05f;
		
		// Game controls
		QuitGameAction qgAction = new QuitGameAction(bg);
		
		// Quit the game.
		this.addControl(null, net.java.games.input.Component.Identifier.Key.ESCAPE, null, qgAction,
				null, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE, null);
		
		setupPlayerOne(bg);
		
		
		/*
		 * this.addControl(null, net.java.games.input.Component.Identifier.Key.W, null, mvNode, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		 */
		return im;
	}
	
	/**
	 * Returns the updated input manager.
	 *
	 * @return - The updated input manager.
	 */
	public IInputManager getIInputManager() {
		return im;
	}
	
}