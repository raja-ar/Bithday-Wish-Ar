/*
 * author: Prajwol
 */
package com.raja.bwar.proxy.model;

import java.util.HashMap;
import java.util.Map;

public class MyFriend {
	private String fbID = " ";
	private String name = " ";
	private String bday = " "; // can be missing, be in form of mm/dd or
	// mm/dd/yyyy
	private String pic = " ";
	private String bdayMessage = " ";
	private boolean isAutoPost = false;

	public MyFriend() {

	}

	public MyFriend(String fbID, String name, String bday, String pic) {
		this.fbID = fbID == null ? " " : fbID;
		this.name = name == null ? " " : name;
		this.bday = bday == null ? " " : bday;
		this.pic = pic == null ? " " : pic;
	}

	public MyFriend(String fbID, String name, String bday, String pic,
			String bdayMessage, boolean isAutoPost) {
		this.fbID = fbID == null ? " " : fbID;
		this.name = name == null ? " " : name;
		this.bday = bday == null ? " " : bday;
		this.pic = pic == null ? " " : pic;
		this.bdayMessage = bdayMessage == null ? " " : bdayMessage;
		this.isAutoPost = isAutoPost;
	}

	public Map<String, String> getMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("fbID", fbID);
		map.put("pic", pic);
		map.put("name", name);
		map.put("bday", bday);
		return map;
	}

	public String getFbID() {
		return fbID;
	}

	public void setFbID(String fbID) {
		this.fbID = fbID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBday() {
		return bday;
	}

	public void setBday(String bday) {
		this.bday = bday;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getBdayMessage() {
		return bdayMessage;
	}

	public void setBdayMessage(String bdayMessage) {
		this.bdayMessage = bdayMessage;
	}

	public boolean isAutoPost() {
		return isAutoPost;
	}

	public void setAutoPost(boolean isAutoPost) {
		this.isAutoPost = isAutoPost;
	}

}
