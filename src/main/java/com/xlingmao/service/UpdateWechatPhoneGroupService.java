package com.xlingmao.service;

import java.util.HashSet;
import java.util.List;

import com.xlingmao.common.thread.DeviceSocketThread;
import com.xlingmao.dto.ImageDeviceDto;
import com.xlingmao.model.GroupModel;
import com.xlingmao.model.GroupTreeModel;
import com.xlingmao.util.ServiceSingleUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.VBox;

/**
 * 更新微信营销管理手机群组服务，单例获取
 * @author beijixiong
 *
 */
public class UpdateWechatPhoneGroupService {
	
	private VBox selectPhoneBox;
	
	private Label selectedPhoneNum;
	
	private HashSet<String> selectedPhoneSet;

	public VBox getSelectPhoneBox() {
		return selectPhoneBox;
	}

	public void setSelectPhoneBox(VBox selectPhoneBox) {
		this.selectPhoneBox = selectPhoneBox;
	}

	public Label getSelectedPhoneNum() {
		return selectedPhoneNum;
	}

	public void setSelectedPhoneNum(Label selectedPhoneNum) {
		this.selectedPhoneNum = selectedPhoneNum;
	}

	public HashSet<String> getSelectedPhoneSet() {
		return selectedPhoneSet;
	}

	public void setSelectedPhoneSet(HashSet<String> selectedPhoneSet) {
		this.selectedPhoneSet = selectedPhoneSet;
	}
	
	public void updateGroup(){
		//TODO 优化，不清空，增量更新
		if(null != selectPhoneBox && null != selectedPhoneNum && null != selectedPhoneSet){
			selectPhoneBox.getChildren().clear();
			List<GroupModel> models = getAllModels();
			for (GroupModel groupModel : models) {
				CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<String>(groupModel.getGroupName());
				rootItem.setExpanded(true);
				TreeView<String> tree = new TreeView<String>(rootItem);  
				tree.setEditable(true);
				tree.setCellFactory(CheckBoxTreeCell.forTreeView());
				List<GroupTreeModel> groupTreeModels = groupModel.getGroupTreeModel();
				for (GroupTreeModel groupTreeModel : groupTreeModels) {
					ImageDeviceDto deviceDto = DeviceSocketThread.getImageMap().get(groupTreeModel.getDeviceName());
					if(null != deviceDto && !deviceDto.isOffLine()){
						CheckBoxTreeItem<String> checkBoxTreeItem = new CheckBoxTreeItem<String>(groupTreeModel.getMappingName());
						if(selectedPhoneSet.contains(groupTreeModel.getMappingName())){
							 checkBoxTreeItem.setSelected(true);
						}
						checkBoxTreeItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
							 public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
								 if (newValue) {
									 selectedPhoneSet.add(checkBoxTreeItem.valueProperty().get());
						         }else{
						        	 selectedPhoneSet.remove(checkBoxTreeItem.valueProperty().get());
						         }
								 selectedPhoneNum.setText(selectedPhoneSet.size() + "");
						     }
							 });
						rootItem.getChildren().add(checkBoxTreeItem);
					}
				}
				tree.setRoot(rootItem);
				tree.setShowRoot(true);
				selectPhoneBox.getChildren().add(tree);
			}
		}
	}
	
	private List<GroupModel> getAllModels(){
		GroupService groupService = (GroupService)ServiceSingleUtil.getService(GroupService.class);
		List<GroupModel> models = groupService.buildGroupTree();
		return models;
	}
	
}
