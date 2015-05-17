package engine.input;

import engine.input.action.camera.FireAction;
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
	private boolean					finalBuild	= true;
	
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
	public void addControl(	Identifier gpKey, Identifier kbKey, Identifier moKey, IAction action, INPUT_ACTION_TYPE gpActionType, INPUT_ACTION_TYPE kbActionType, INPUT_ACTION_TYPE moActionType) {
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

	/**
	 * Controls for the local player, using mouse and keyboard.
	 * @param bg base game
	 * @param sm scene manager
	 */
	private void setupKeyboardMouse(TreasureHunt bg, SceneManager sm) {
		// Quit game action
		QuitGameAction qgAction = new QuitGameAction(bg);
				
		// Quit game control
		this.addControl(null, Key.ESCAPE, null, qgAction, null, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY, null);
		
		// Speed action
		SetSpeedAction setSpeed = new SetSpeedAction(bg.localPlayer.getTriMesh());
		
		// Set speed control
		this.addControl(null, Key.LSHIFT, null, setSpeed, null, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY, null);
		
		// Set locked action
		SetLockedAction setLocked = new SetLockedAction(bg.localPlayer.getTriMesh());
		
		// Set Locked control
		this.addControl(null, Key.RSHIFT, null, setLocked, null, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY, null);
		
		// Movement actions
		MoveNodeAction mvNodeLeft = new MoveNodeAction(bg.localPlayer.getTriMesh(), Direction.LEFT, setSpeed, sm.getHillTerrain(), bg);
		MoveNodeAction mvNodeRight = new MoveNodeAction(bg.localPlayer.getTriMesh(), Direction.RIGHT, setSpeed, sm.getHillTerrain(), bg);
		MoveNodeAction mvNodeForward = new MoveNodeAction(bg.localPlayer.getTriMesh(), Direction.FORWARD, setSpeed, sm.getHillTerrain(), bg);
		MoveNodeAction mvNodeBackward = new MoveNodeAction(bg.localPlayer.getTriMesh(), Direction.BACKWARD, setSpeed, sm.getHillTerrain(), bg);

		// Movement controls
		this.addControl(null, Key.W, null, mvNodeForward, null, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		this.addControl(null, Key.A, null, mvNodeLeft, null, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);		
		this.addControl(null, Key.D, null, mvNodeRight, null, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		this.addControl(null, Key.S, null, mvNodeBackward, null, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		
		// Orbit actions
		OrbitLeftRightAction orbitRight = new OrbitLeftRightAction(false, true, bg.cc1, setLocked, bg.localPlayer.getTriMesh());
		OrbitLeftRightAction orbitLeft = new OrbitLeftRightAction(false, false, bg.cc1, setLocked, bg.localPlayer.getTriMesh());
		OrbitUpDownAction orbitUp = new OrbitUpDownAction(false, true, bg.cc1);
		OrbitUpDownAction orbitDown = new OrbitUpDownAction(false, false, bg.cc1);
		
		// Orbit controls
		this.addControl(null, Key.UP, null, orbitUp, null, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		this.addControl(null, Key.DOWN, null, orbitDown, null, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		this.addControl(null, Key.RIGHT, null, orbitRight, null, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
		this.addControl(null, Key.LEFT, null, orbitLeft, null, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null);
	}
	
	/**
	 * Controls for local player using gamepad controller
	 * @param bg
	 * @param sm
	 */
	private void setupController(TreasureHunt bg, SceneManager sm) {
		// Quit game action
		QuitGameAction qgAction = new QuitGameAction(bg);
				
		// Quit game control
		this.addControl(Button._7, null, null, qgAction, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY, null, null);
		
		// Set speed action
		SetSpeedAction setSpeed = new SetSpeedAction(bg.localPlayer.getTriMesh());
		
		// Set speed control
		this.addControl(Button._0, null, null, setSpeed, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY, null, null);
		
		// Set locked action
		SetLockedAction setLocked = new SetLockedAction(bg.localPlayer.getTriMesh());
		
		// Set locked control
		this.addControl(Button._1, null, null, setLocked, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY,	null, null);
		
		// Fire action
		FireAction fAction = new FireAction(bg);
		
		// Fire control
		this.addControl(Button.TRIGGER, null, null, fAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null, null);
		
		// Move actions
		MoveNodeAction mvNodeLeft = new MoveNodeAction(bg.localPlayer.getTriMesh(), Direction.LEFT, setSpeed, sm.getHillTerrain(), bg);
		MoveNodeAction mvNodeRight = new MoveNodeAction(bg.localPlayer.getTriMesh(), Direction.RIGHT, setSpeed, sm.getHillTerrain(), bg);
		MoveNodeAction mvNodeForward = new MoveNodeAction(bg.localPlayer.getTriMesh(), Direction.FORWARD, setSpeed, sm.getHillTerrain(), bg);
		MoveNodeAction mvNodeBackward = new MoveNodeAction(bg.localPlayer.getTriMesh(), Direction.BACKWARD, setSpeed, sm.getHillTerrain(), bg);
		
		// Movement controls
		this.addControl(Axis.Y, null, null, mvNodeForward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null, null);
		this.addControl(Axis.X, null, null, mvNodeLeft, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null, null);
		this.addControl(Axis.X, null, null, mvNodeRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null, null);
		this.addControl(Axis.Y, null, null, mvNodeBackward,	IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null, null);
		
		// Orbit actions
		OrbitLeftRightAction orbitLeftRight = new OrbitLeftRightAction(true, true, bg.cc1, setLocked, bg.localPlayer.getTriMesh());
		OrbitUpDownAction orbitUpDown = new OrbitUpDownAction(true, false, bg.cc1);
		
		// Orbit controls
		this.addControl(Axis.RY, null, null, orbitUpDown, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null, null);
		this.addControl(Axis.RX, null, null, orbitLeftRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null, null);

		// Zoom actions
		ZoomAction zoomOutP2 = new ZoomAction(bg.cc1, true, 2);
		ZoomAction zoomInP2 = new ZoomAction(bg.cc1, false, 2);

		// Zoom controls
		this.addControl(Button._4, null, null, zoomInP2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN,	null, null);
		this.addControl(Button._5, null, null, zoomOutP2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN, null, null);
	}
	
	public IInputManager setupControls(ICamera camera1, IInputManager im, TreasureHunt bg, SceneManager sm) {
		setupKeyboardMouse(bg, sm);
		setupController(bg, sm);

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