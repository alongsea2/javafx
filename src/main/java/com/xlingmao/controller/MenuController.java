package com.xlingmao.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONObject;
import com.xlingmao.service.ConfigService;
import com.xlingmao.util.HttpClientUtil;
import com.xlingmao.util.PropertiesUtil;

import javafx.fxml.FXML;

public class MenuController extends BaseViewController {
	
	
	private ConfigService configService = new ConfigService();
	
	
	@FXML
	private void checkUpdate(){
		String currentVersion = configService.selectByKey("version").getValue();
		String url = PropertiesUtil.getValue("aurl")+"/sys/checkVersion";
		String mid = PropertiesUtil.getValue("aid");
		
		Map<String,String> headers = new HashMap<String,String>();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("machineId",mid));
		params.add(new BasicNameValuePair("version",currentVersion));
		
		
		//check version
		String result = HttpClientUtil.postWithHeadersAndParams(url, headers, params);
		
		JSONObject resultJson = JSONObject.parseObject(result);
		
		int code = resultJson.getIntValue("code");
		String msg = resultJson.getString("message");
		
		if(code == 2006){
			showInfoDialog(msg, null);
		}else if(code == 2000){
			//TODO download update patch
		}
		
	}
	
	@FXML
	private void about(){
		
	}

}
