package com.xlingmao.controller;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class SayHelloViewController extends BaseViewController{
	
	@FXML 
	private TextArea helloWords;
	
	@FXML
	private ToggleGroup operationGroup;

	@FXML
	private ToggleGroup sexGroup;
	
	/**
	 * 全部性别
	 */
	@FXML
	private RadioButton allSex;
	
	/**
	 * 男
	 */
	@FXML
	private RadioButton man;
	
	/**
	 * 女
	 */
	@FXML
	private RadioButton female;
	
	/**
	 * 多少人
	 */
	@FXML
	private TextField peopleNum;
	
	/**
	 * 每次点击间隔时间开始
	 */
	@FXML
	private TextField sleepTimeStart;
	
	/**
	 * 每次点击间隔时间结束
	 */
	@FXML
	private TextField sleepTimeEnd;
	
	@FXML
	private void handleProduceHelloWords(){
		//TODO 词库,以分号结尾
		helloWords.setText("你好，我是北极熊");
	}
	
	@FXML
	private void handleSayHello(){
		if(isnotSelectedPhone()){
			return;
		}
		if(StringUtils.isNotBlank(helloWords.getText())){
			//if(StringUtils.isNotBlank(peopleNum.getText())){
				//for(int i=0;i < Integer.parseInt(peopleNum.getText());i++){
					String sex = "";
					if(allSex.isSelected()){
						sex = "all";
					}
					if(man.isSelected()){
						sex = "man";
					}
					if(female.isSelected()){
						sex = "female";
					}
					//第一个是参数
					JSONObject json = new JSONObject();
					json.put("sex", sex);
					if(StringUtils.isNotBlank(peopleNum.getText())){
						json.put("peopleNum", Integer.parseInt(peopleNum.getText()));
					}else{
						json.put("peopleNum", 10);
					}
					if(StringUtils.isNotBlank(sleepTimeStart.getText())){
						json.put("sleepTimeStart", Integer.parseInt(sleepTimeStart.getText()));
					}else{
						json.put("sleepTimeStart", 2);
					}
					if(StringUtils.isNotBlank(sleepTimeEnd.getText())){
						json.put("sleepTimeEnd", Integer.parseInt(sleepTimeEnd.getText()));
					}else{
						json.put("sleepTimeEnd", 5);
					}
					json.put("helloWorlds", helloWords.getText());
					writeAndPushFileWithDevices("sayhello.txt",json.toString());
					sleep(2000);
					execCommondWithDevices("uiautomator runtest auto.jar -c xlm.ac.wx.autocase.SayHello");
				//}
			//}
			showInfoDialog("打招呼", "添加到执行任务列表成功");
		}else{
			showErrorDialog("打招呼", "请输入打招呼用语");
		}
	}
	
	
}
