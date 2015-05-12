package engine.graphics;

import java.awt.Canvas;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import sage.display.DisplaySettingsDialog;
import sage.display.DisplaySystem;
import sage.display.IDisplaySystem;
import sage.renderer.IRenderer;
import sage.renderer.RendererFactory;

/**
 * This class represents a DisplaySystem that can be ran in a windowed or FSEM fashion.
 * 
 * @author Kevin
 */
public class GameDisplaySystem implements IDisplaySystem {
	
	// Display System attributes.
	private JFrame			myFrame;
	private GraphicsDevice	device;
	private IRenderer		myRenderer;
	private int				width, height, bitDepth, refreshRate;
	private Canvas			rendererCanvas;
	private boolean			isCreated;
	private boolean			isFullScreen;
	
	// Networking information
	private JRadioButton	singlePlayer;
	private JRadioButton	multiPlayer;
	private JTextField		serverIP;
	private JTextField		serverPort;
	
	/**
	 * Determines if single player is enabled.
	 * 
	 * @return
	 */
	public boolean isSinglePlayer() {
		return singlePlayer.isSelected();
	}
	
	/**
	 * Gets the server IP
	 * 
	 * @return
	 */
	public String getServerIP() {
		return serverIP.getText();
	}
	
	/**
	 * Gets the server port.
	 * 
	 * @return
	 */
	public int getServerPort() {
		return Integer.parseInt(serverPort.getText());
	}
	
	/**
	 * Constructor for building the GameDisplaySystem.
	 * 
	 * @param w
	 *            - The width.
	 * @param h
	 *            - The height.
	 * @param depth
	 *            - The bit depth.
	 * @param rate
	 *            - The refresh rate.
	 * @param isFS
	 *            - Is the display full screen?
	 * @param rName
	 *            - The name of the renderer.
	 * @param title
	 *            - The title of the frame.
	 */
	public GameDisplaySystem(int w, int h, int depth, int rate, boolean isFS, String rName,
			String title) {
		
		// Populate the GameDisplaySystem attributes.
		width = w;
		height = h;
		bitDepth = depth;
		refreshRate = rate;
		this.isFullScreen = isFS;
		
		// Get a renderer from the Renderer Factory.
		myRenderer = RendererFactory.createRenderer(rName);
		if (myRenderer == null) {
			throw new RuntimeException("Unable to find renderer '" + rName + "'");
		}
		rendererCanvas = myRenderer.getCanvas();
		myFrame = new JFrame(title);
		myFrame.add(rendererCanvas);
		
		// Initialize the screen with the specified properties when the GameDisplaySystem was created.
		initScreen();
		
		// Save the configured DisplaySystem, show the window and flag that the DisplaySystem was made.
		DisplaySystem.setCurrentDisplaySystem(this);
		myFrame.setVisible(true);
		isCreated = true;
	}
	
	/**
	 * This method will wait for the GameDisplaySystem to initialize before returning it.
	 * 
	 * @return - The game display system.
	 */
	public IDisplaySystem waitForInitialization() {
		System.out.print("\nWaiting for display creation...");
		int count = 0;
		// wait until display creation completes or a timeout occurs
		while (!this.isCreated()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException("Display creation interrupted");
			}
			count++;
			System.out.print("+");
			if (count % 80 == 0) {
				System.out.println();
			}
			if (count > 2000) // 20 seconds (approx.)
			{
				throw new RuntimeException("Unable to create display");
			}
		}
		System.out.println();
		System.out.println("Finished creating the display.");
		
		return this;
	}
	
	/**
	 * Configures the configuration window for display and networking settings.
	 * 
	 * @return
	 */
	private DisplaySettingsDialog getConfiguirationWindow() {
	DisplaySettingsDialog dialog = new DisplaySettingsDialog(device);
		
	// Add networking options to the display window
	singlePlayer = new JRadioButton("Single Player");
	multiPlayer = new JRadioButton("Multi Player");
	serverIP = new JTextField("Server IP Address", 20);
	serverPort = new JTextField("Server Port", 20);
	ButtonGroup radioGroup = new ButtonGroup();

	// Radio buttons for single and multi-player
	radioGroup.add(singlePlayer);
	radioGroup.add(multiPlayer);
		
	dialog.setLayout(new FlowLayout());
	dialog.add(singlePlayer);
	dialog.add(multiPlayer);
	dialog.add(serverIP);
	dialog.add(serverPort);
	singlePlayer.setSelected(true);
		
	dialog.setSize(400, 300);
	dialog.setResizable(false);
		
	return dialog;
	}
	
	/**
	 * Initializes the game screen.
	 * 
	 * @param dispMode
	 *            - The display mode.
	 * @param fullScreenRequested
	 *            - Determines if the game is full screen.
	 */
	private void initScreen() {
		// Get the default screen device out of the local graphics environment.
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		device = environment.getDefaultScreenDevice();
		
		// Show the display dialog and allow the user to specify their display settings.
		DisplaySettingsDialog dialog = getConfiguirationWindow();
		dialog.showIt();
		
		DisplayMode dispMode = dialog.getSelectedDisplayMode();
		
		if (device.isFullScreenSupported() && dialog.isFullScreenModeSelected()) {
			myFrame.setUndecorated(true);
			myFrame.setResizable(false);
			myFrame.setIgnoreRepaint(true);
			
			// Put the device in full screen mode
			device.setFullScreenWindow(myFrame);
			
			// Try to set full screen device DisplayMode
			if (dispMode != null && device.isDisplayChangeSupported()) {
				try {
					device.setDisplayMode(dispMode);
					myFrame.setSize(dispMode.getWidth(), dispMode.getHeight());
				} catch (Exception e) {
					System.err.println("Exception while setting device DisplayMode: " + e);
				}
			} else {
				System.err.println("Cannot set display mode");
				
			}
			
		} else {
			// Windowed mode
			myFrame.setSize(dispMode.getWidth(), dispMode.getHeight());
			myFrame.setLocationRelativeTo(null);
		}
	}
	
	@Override
	public void addKeyListener(KeyListener arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addMouseListener(MouseListener arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addMouseMotionListener(MouseMotionListener arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * Restores the display device to non-FSEM mode.
	 */
	@Override
	public void close() {
		if (device != null) {
			Window window = device.getFullScreenWindow();
			if (window != null) {
				window.dispose();
			}
			device.setFullScreenWindow(null);
			;
			
		}
	}
	
	@Override
	public void convertPointToScreen(Point arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getBitDepth() {
		return this.bitDepth;
	}
	
	@Override
	public int getHeight() {
		return this.height;
	}
	
	@Override
	public int getRefreshRate() {
		return this.refreshRate;
	}
	
	@Override
	public IRenderer getRenderer() {
		return this.myRenderer;
	}
	
	@Override
	public int getWidth() {
		return this.width;
	}
	
	@Override
	public boolean isCreated() {
		return this.isCreated;
	}
	
	@Override
	public boolean isFullScreen() {
		return this.isFullScreen;
	}
	
	@Override
	public boolean isShowing() {
		return this.isShowing();
	}
	
	@Override
	public void setBitDepth(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setCustomCursor(String arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setHeight(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setPredefinedCursor(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setRefreshRate(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setTitle(String title) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void setWidth(int width) {
		// TODO Auto-generated method stub
	}
	
}
