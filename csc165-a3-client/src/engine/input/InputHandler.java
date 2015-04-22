package engine.input;

import engine.input.action.camera.MoveAction.Direction;
import engine.input.action.camera.MoveNodeAction;
import engine.input.action.camera.OrbitLeftRightAction;
import engine.input.action.camera.OrbitUpDownAction;
import engine.input.action.camera.SetLockedAction;
import engine.input.action.camera.SetSpeedAction;
import engine.input.action.camera.ZoomAction;
import engine.scene.SceneManager;
import games.treasurehunt2015.TreasureHunt;

import java.util.ArrayList;

import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.Identifier.Button;
import net.java.games.input.Component.Identifier.Key;
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
	
	private void setupPlayerOne(TreasureHunt bg, SceneManager sm) {
		// Controls for the first player, using mouse and keyboard.
		SetSpeedAction setSpeed = new SetSpeedAction(bg.localPlayer);
		SetLockedAction setLocked = new SetLockedAction(bg.localPlayer);
		
		MoveNodeAction mvNodeLeft = new MoveNodeAction(bg.localPlayer, Direction.LEFT, setSpeed, sm.getHillTerrain(), bg);
		MoveNodeAction mvNodeRight = new MoveNodeAction(bg.localPlayer, Direction.RIGHT, setSpeed,sm.getHillTerrain(), bg);
		MoveNodeAction mvNodeForward = new MoveNodeAction(bg.localPlayer, Direction.FORWARD, setSpeed, sm.getHillTerrain(), bg);
		MoveNodeAction mvNodeBackward = new MoveNodeAction(bg.localPlayer, Direction.BACKWARD, setSpeed, sm.getHillTerrain(), bg);
		
		OrbitLeftRightAction orbitRight = new OrbitLeftRightAction(false, true, bg.cc1, setLocked, bg.localPlayer);
		OrbitLeftRightAction orbitLeft = new OrbitLeftRightAction(false, false, bg.cc1, setLocked, bg.localPlayer);
		OrbitUpDownAction orbitUp = new OrbitUpDownAction(false, true, bg.cc1, setLocked, bg.localPlayer);
		OrbitUpDownAction orbitDown = new OrbitUpDownAction(false, false, bg.cc1, setLocked, bg.localPlayer);
		
		// Orbit Up.
		this.addControl(null, Key.UP, null, orbitUp,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Orbit Down.
		this.addControl(null, Key.DOWN, null, orbitDown,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Orbit Right.
		this.addControl(null, Key.RIGHT, null,
				orbitRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Orbit Left.
		this.addControl(null, Key.LEFT, null, orbitLeft,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move forward.
		this.addControl(null, Key.W, null, mvNodeForward,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move left.
		this.addControl(null, Key.A, null, mvNodeLeft,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move right.
		this.addControl(null, Key.D, null, mvNodeRight,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move backward.
		this.addControl(null, Key.S, null,
				mvNodeBackward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Set speed action.
		this.addControl(null, Key.LSHIFT, null, setSpeed,
				null, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE, null);
		
		// Set Locked action.
		this.addControl(null, Key.SPACE, null, setLocked,
				null, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE, null);
		
	}
	
	private void setupController(TreasureHunt bg, SceneManager sm) {
		// Controls for the first player, using mouse and keyboard.
		SetSpeedAction setSpeed = new SetSpeedAction(bg.localPlayer);
		SetLockedAction setLocked = new SetLockedAction(bg.localPlayer);
		
		MoveNodeAction mvNodeLeft = new MoveNodeAction(bg.localPlayer, Direction.LEFT, setSpeed, sm.getHillTerrain(), bg);
		MoveNodeAction mvNodeRight = new MoveNodeAction(bg.localPlayer, Direction.RIGHT, setSpeed, sm.getHillTerrain(), bg);
		MoveNodeAction mvNodeForward = new MoveNodeAction(bg.localPlayer, Direction.FORWARD, setSpeed, sm.getHillTerrain(), bg);
		MoveNodeAction mvNodeBackward = new MoveNodeAction(bg.localPlayer, Direction.BACKWARD, setSpeed, sm.getHillTerrain(), bg);
		
		OrbitLeftRightAction orbitLeftRight = new OrbitLeftRightAction(true, true, bg.cc1,
				setLocked, bg.localPlayer);
		
		OrbitUpDownAction orbitUpDown = new OrbitUpDownAction(true, false, bg.cc1, setLocked,
				bg.localPlayer);
		
		ZoomAction zoomOutP2 = new ZoomAction(bg.cc1, true, 2);
		ZoomAction zoomInP2 = new ZoomAction(bg.cc1, false, 2);
		
		// Zoom in P1
		this.addControl(Button._4, null, null, zoomInP2,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Zoom out P1
		this.addControl(Button._5, null, null, zoomOutP2,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Orbit Up and down.
		this.addControl(Axis.RY, null, null, orbitUpDown,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		//Orbit Left and Right.
		this.addControl(Axis.RX, null, null,
				orbitLeftRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move forward.
		this.addControl(Axis.Y, null, null,
				mvNodeForward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move left.
		this.addControl(Axis.X, null, null, mvNodeLeft,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move right.
		this.addControl(Axis.X, null, null, mvNodeRight,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Move backward.
		this.addControl(Axis.Y, null, null,
				mvNodeBackward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Set speed action.
		this.addControl(Button._0, null, null, setSpeed,
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE,
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE, null);
		
		// Set Locked action.
		this.addControl(Button._1, null, null, setLocked,
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE,
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE, null);
	}
	
	public IInputManager setupControls(	ICamera camera1,
										IInputManager im,
										TreasureHunt bg,
										SceneManager sm) {
		// Game controls
		QuitGameAction qgAction = new QuitGameAction(bg);
		
		// Quit the game.
		this.addControl(null, Key.ESCAPE, null, qgAction,
				null, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE, null);
		
		setupPlayerOne(bg,sm);
		setupController(bg, sm);
		
		
		/*
		 * this.addControl(null, Key.W, null, mvNode, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
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