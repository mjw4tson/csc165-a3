package engine.scene.hud;

import java.util.Iterator;

import sage.scene.Group;
import sage.scene.HUDImage;
import sage.scene.SceneNode;

/**
 * This class returns a group of hud images that composes a provided number or time value, with the maximum value being 999 for simplicity.
 * 
 * @author Kevin
 */
public class HUDNumber {
	String				groupName;
	private String		dirHud		= "/images/hud/";
	private String		directory;
	private HUDImage	tensPlace;
	private HUDImage	hundredsPlace;
	private HUDImage	onesPlace;
	private Group		hudString	= new Group("HUD String");
	
	public HUDNumber(String name, String dir, float xLoc, float yLoc) {
		this.groupName = name;
		this.directory = dir;
		
		tensPlace = new HUDImage(directory + dirHud + "0.png");
		tensPlace.setName("tensPlace");
		onesPlace = new HUDImage(directory + dirHud + "0.png");
		onesPlace.setName("onesPlace");
		hundredsPlace = new HUDImage(directory + dirHud + "0.png");
		hundredsPlace.setName("hundredsPlace");
		
		onesPlace.rotateImage(180);
		onesPlace.scale(.02f, .035f, .1f);
		onesPlace.setLocation(xLoc, yLoc);
		
		tensPlace.rotateImage(180);
		tensPlace.scale(.02f, .035f, .1f);
		tensPlace.setLocation(xLoc - .0188f, yLoc);
		
		hundredsPlace.rotateImage(180);
		hundredsPlace.scale(.02f, .035f, .1f);
		hundredsPlace.setLocation(xLoc - .036f, yLoc);
		
		hudString.addChild(hundredsPlace);
		hudString.addChild(tensPlace);
		hudString.addChild(onesPlace);
	}
	
	public Group getHUDNumber() {
		return hudString;
	}
	
	/**
	 * Updates the HUDImage with the specified value.
	 * @param value
	 */
	public void updateValue(int value){
		Iterator<SceneNode> children = hudString.getChildren();
		HUDImage temp;
		
		// Set a limit to 999 for the value.
		if(value > 999){
			value = 999;
		}
		
		int hundreds= value / 100;
		int tens = (value / 10) % 10;
		int ones = (value % 10);
		
		System.out.println(value + "h" + hundreds + "t" + tens + "o" + ones);
		
		while(children.hasNext()){
			temp = (HUDImage) children.next();
			if(temp.getName().equals("hundredsPlace")){
				temp.setImage(directory + dirHud + hundreds + ".png");
			} else if(temp.getName().equals("tensPlace")){
				temp.setImage(directory + dirHud + tens + ".png");
			} else {
				temp.setImage(directory + dirHud + ones + ".png");
			}
		}
		
	}
	
}
