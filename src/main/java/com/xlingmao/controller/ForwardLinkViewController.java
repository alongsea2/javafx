package com.xlingmao.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONObject;
import com.xlingmao.util.HttpClientUtil;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ForwardLinkViewController extends BaseViewController{
	
	/**
	 * 网址
	 */
	@FXML
	private TextField link;
	
	/**
	 * 说的话
	 */
	@FXML
	private TextArea say;
	
	@FXML
	private void handleForwardLink(){
		if(isnotSelectedPhone()){
			return;
		}
		if(StringUtils.isNoneBlank(link.getText())){
			JSONObject json = new JSONObject();
			json.put("link", link.getText());
			json.put("say", say.getText());
			writeAndPushFileWithDevices("forwardlink.txt", json.toString());
			sleep(2000);
			execCommondWithDevices("uiautomator runtest auto.jar -c xlm.ac.wx.autocase.ForwardLink");
			showInfoDialog("转发链接", "添加到执行任务列表成功");
		}else{
			showErrorDialog("缩短网址", "请输入合法的网址");
		}
	}
	
	@FXML
	private void handleShortUrl(){
		if(StringUtils.isNoneBlank(link.getText())){
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("url", link.getText()));
			String response = HttpClientUtil.postWithParams("http://dwz.cn/create.php", params);
			JSONObject json = JSONObject.parseObject(response);
			if(0 == json.getIntValue("status")){
				link.setText(json.getString("tinyurl"));
			}else{
				showErrorDialog("缩短网址", "请输入合法的网址");
			}
		}else{
			showErrorDialog("缩短网址", "请输入合法的网址");
		}
	}
	

}
