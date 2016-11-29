package com.xlingmao.controller;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class AcceptRequestViewController extends BaseViewController{
	
	@FXML
	private ToggleGroup numberGroup;
	
	/**
	 * 从上往下
	 */
	@FXML
	private RadioButton fromUpToLow;
	
	/**
	 * 每次点击数
	 */
	@FXML
	private TextField clickTimes;
	
	/**
	 * 上面的每次点击间隔时间开始
	 */
	@FXML
	private TextField UpSleepTimeStart;
	
	/**
	 * 上面的每次点击间隔时间结束
	 */
	@FXML
	private TextField UpSleepTimeEnd;
	
	/**
	 * 下面的每次点击间隔时间开始
	 */
	@FXML
	private TextField LowSleepTimeStart;
	
	/**
	 * 下面的每次点击间隔时间结束
	 */
	@FXML
	private TextField LowSleepTimeEnd;
	
	/**
	 * 上面的主动添加好友
	 */
	@FXML
	private CheckBox upAutoAddFriends;
	
	/**
	 * 下面的主动添加好友
	 */
	@FXML
	private CheckBox lowAutoAddFriends;
	
	
	@FXML
	private void handleAutoAccept(){
		if(isnotSelectedPhone()){
			return;
		}
		JSONObject json = new JSONObject();
		if(StringUtils.isNotBlank(UpSleepTimeStart.getText())){
			json.put("sleepTimeStart", Integer.parseInt(UpSleepTimeStart.getText()));
		}else{
			json.put("sleepTimeStart", 2);
		}
		if(StringUtils.isNotBlank(UpSleepTimeEnd.getText())){
			json.put("sleepTimeEnd", Integer.parseInt(UpSleepTimeEnd.getText()));
		}else{
			json.put("sleepTimeEnd", 5);
		}
		if(StringUtils.isNotBlank(clickTimes.getText())){
			json.put("clickTimes", Integer.parseInt(clickTimes.getText()));
		}else{
			json.put("clickTimes", 2);
		}
		if(fromUpToLow.isSelected()){
			json.put("clickNumber", "upToLow");
		}else{
			json.put("clickNumber", "lowToUp");
		}
		json.put("upAutoAddFriends", upAutoAddFriends.isSelected());
		writeAndPushFileWithDevices("acceptrequest.txt", json.toString());
		sleep(2000);
		execCommondWithDevices("uiautomator runtest auto.jar -c xlm.ac.wx.autocase.AccpetRequest");
		showInfoDialog("自动接受新朋友", "添加到执行任务列表成功");
	}

	@FXML
	private void handleAutoAgree(){
		showInfoDialog("自动同意附近人", "添加到执行任务列表成功");
	}
	
}
