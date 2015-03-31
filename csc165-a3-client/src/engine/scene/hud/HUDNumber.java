package engine.scene.hud;

import sage.scene.Group;
import sage.scene.HUDImage;

/**
 * This class returns a group of hud images that composes a provided number or time value.
 * @author Kevin
 *
 */
public class HUDNumber {
	String			groupName;
	private String	dirHud	= "/images/hud/";
	private String	directory;
	
	public HUDNumber(String name, String dir) {
		this.groupName = name;
		this.directory = dir;
		
	}
	
	/**
	 * Return a group of HUDImages that represents a number.
	 * @param numberToPrint
	 * @param xLoc -The x Location
	 * @param yLoc - The Y location.
	 * @return
	 */
	public Group printValues(	int numberToPrint,
								float xLoc,
								float yLoc) {
		Group group = new Group(groupName);
		HUDImage valueImage;
		int value = numberToPrint;
		int temp = 0;
		float incrementValue = 0;
		
		if (value == 0) {
			valueImage = new HUDImage(directory + dirHud + "0.png");
			valueImage.rotateImage(180);
			valueImage.scale(.02f, .035f, .1f);
			valueImage.setLocation(xLoc, yLoc);
			group.addChild(valueImage);
			
		} else {
			while (value >= 1) {
				temp = value % 10;
				// Draw first value
				value = value / 10;
				valueImage = new HUDImage(directory + dirHud + temp + ".png");
				valueImage.rotateImage(180);
				valueImage.scale(.02f, .035f, .1f);
				valueImage.setLocation(xLoc - incrementValue, yLoc);
				group.addChild(valueImage);
				incrementValue = incrementValue + 0.018f;
			}
			
		}
		
		return group;
	}
	
}
