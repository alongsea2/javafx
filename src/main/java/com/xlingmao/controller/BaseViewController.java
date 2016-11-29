package com.xlingmao.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.xlingmao.common.thread.ExecCommondThread;
import com.xlingmao.entity.DeviceGroup;
import com.xlingmao.service.DeviceGroupService;
import com.xlingmao.util.ServiceSingleUtil;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class BaseViewController {
	
	/**
	 * 选中的手机
	 */
	protected HashSet<String> selectedPhoneSet;
	
	private ExecutorService commondPool = Executors.newFixedThreadPool(180);
	
	/**
	 * 传输文件目录
	 */
	public static final String xlingmaPhonePath = "/mnt/sdcard/xlingmao/";
	
	/**
	 * 设置选中的手机
	 * @param selectPhoneBox
	 */
	public void setSelectedPhoneSet(HashSet<String> selectedPhoneSet) {
		this.selectedPhoneSet = selectedPhoneSet;
	}

	/**
	 * 展示信息提示框
	 * @param header
	 * @param content
	 */
	public void showInfoDialog(String header,String content){
	    showInfoDialog("消息", header, content);
	}
	
	/**
	 * 展示信息提示框
	 * @param title
	 * @param header
	 * @param content
	 */
	public void showInfoDialog(String title, String header, String content){
		Alert alert = new Alert(AlertType.INFORMATION);
		if(!StringUtils.isEmpty(title)){
			alert.setTitle(title);
		}
		if(!StringUtils.isEmpty(header)){
			alert.setHeaderText(header);
		}
		if(!StringUtils.isEmpty(content)){
			alert.setContentText(content);
		}
	    alert.showAndWait();
	}
	
	/**
	 * 展示错误提示框
	 * @param header
	 * @param content
	 */
	public void showErrorDialog(String header,String content){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("消息");
	    alert.setHeaderText(header);
	    alert.setContentText(content);
	    alert.showAndWait();
	}
	
	
	/**
	 * 等待时间
	 * @param millis
	 */
	public void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取当前项目路径
	 * @return
	 */
	public String getCurrentPcPath(){
		String rootPath = System.getProperty("user.dir") + "/files"; 
		File file = new File(rootPath);
		if(!file.isDirectory()){
			file.mkdir();
		}
		return rootPath + "/";
	}
	
	/**
	 * 写入文件
	 * @param filePath
	 * @param fileName
	 * @param text
	 */
	public void writeFile(String filePath,String fileName,String text){
		String fullPath = filePath + fileName;
		BufferedWriter writer = null;
		try {
			FileWriter file = new FileWriter(fullPath);
			writer = new BufferedWriter(file);
			writer.write(text);
			writer.flush();  
			writer.close();
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 写入文件并推送到手机(默认目录)
	 * @param fileName
	 * @param text
	 */
	public void writeAndPushFileWithDevices(String fileName,String text){
		writeFile(getCurrentPcPath(),fileName,text);
		execPushFileCommondWithDevices(getCurrentPcPath() + fileName);
	}
	
	/**
	 * 删除目录下的所有文件
	 * @param path
	 */
	public void deleteAllFilesOfDir(File path) {  
	    if (!path.exists())  
	        return;  
	    if (path.isFile()) {  
	        path.delete();  
	        return;  
	    }  
	    File[] files = path.listFiles();  
	    for (int i = 0; i < files.length; i++) {  
	        deleteAllFilesOfDir(files[i]);  
	    }  
	    path.delete();  
	} 
	
	/**
	 * 获取文件的后缀名
	 * @param path
	 */
	public String  getFileExtName(File file) {  
		String fileName = file.getName();
		String extName = fileName.substring(fileName.lastIndexOf(".")+1);
		return extName;
	}  
	
	/**
	 * 选中的手机执行adb shell命令
	 * @param commondAfterShell
	 */
	public void execCommondWithDevices(String commondAfterShell){
		DeviceGroupService groupService = (DeviceGroupService)ServiceSingleUtil.getService(DeviceGroupService.class);
		DeviceGroup group = null;
		for (String string : selectedPhoneSet) {
			group = groupService.selectDeviceByMappingName(string);
			commondPool.execute(new ExecCommondThread("adb -s " + group.getDeviceName() + " shell " + commondAfterShell));
		}
	}
	
	/**
	 * 选中的手机执行adb push推送文件
	 * @param filePath
	 */
	public void execPushFileCommondWithDevices(String filePathWithName){
		DeviceGroupService groupService = (DeviceGroupService)ServiceSingleUtil.getService(DeviceGroupService.class);
		DeviceGroup group = null;
		for (String string : selectedPhoneSet) {
			group = groupService.selectDeviceByMappingName(string);
			commondPool.execute(new ExecCommondThread("adb -s " + group.getDeviceName() + " push " + filePathWithName + " " + xlingmaPhonePath));
		}
	}
	
	/**
	 * 是否选择手机
	 * @return
	 */
	public boolean isnotSelectedPhone(){
		if(selectedPhoneSet.size() <= 0){
			showErrorDialog("选择手机", "当前没有选中的手机，请选择手机");
			return true;
		}else{
			return false;
		}
	}
}
