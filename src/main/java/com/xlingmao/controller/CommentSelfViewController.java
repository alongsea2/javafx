package com.xlingmao.controller;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class CommentSelfViewController extends BaseViewController{
	
	@FXML
	private TextArea comment;
	
	@FXML
	private void hadleCommentSelf(){
		if(isnotSelectedPhone()){
			return;
		}
		if(StringUtils.isNotBlank(comment.getText())){
			JSONObject json = new JSONObject();
			json.put("comment", comment.getText());
			writeAndPushFileWithDevices("commentself.txt", json.toString());
			sleep(2000);
			execCommondWithDevices(" uiautomator runtest auto.jar -c xlm.ac.wx.autocase.CommentSelf");
		    showInfoDialog("评论自己", "添加到执行任务列表成功");
		}else{
			showErrorDialog("评论自己", "请输入评论内容");
		}
	}
}
