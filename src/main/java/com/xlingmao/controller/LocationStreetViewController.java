package com.xlingmao.controller;


import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class LocationStreetViewController extends BaseViewController{
	
	@FXML
	private WebView mapView;
	
	private String lon = "120.2444";
	
	private String lat = "30.2444";
	
	@FXML
	private void initialize(){
		WebEngine engine = mapView.getEngine();
		try {
			engine.load(this.getClass().getClassLoader().getResource("map.html").toString());
			JSObject window = (JSObject) engine.executeScript("window");  
			window.setMember("webEngine", this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * JS callback function
	 * @param location
	 */
	public void getLocation(String location){
		if(!StringUtils.isEmpty(location)){
			String locInfo[] = location.split(",");
			this.lon = locInfo[0];
			this.lat = locInfo[1];
		}
	}
	
	@FXML
	private void handleActLocate(){
		if(isnotSelectedPhone()){
			return;
		}
		JSONObject json = new JSONObject();
		json.put("lon", lon);
		json.put("lat", lat);
		writeAndPushFileWithDevices("locationstreet.txt", json.toString());
		sleep(2000);
		execCommondWithDevices("uiautomator runtest auto.jar -c xlm.ac.wx.autocase.LocationStreet");
		showInfoDialog("定位站街", "添加到执行任务列表成功");
	}

}
