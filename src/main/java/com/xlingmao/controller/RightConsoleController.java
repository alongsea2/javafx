package com.xlingmao.controller;


import com.android.ddmlib.IDevice;
import com.xlingmao.common.AppConfig;
import com.xlingmao.common.thread.DeviceSocketThread;
import com.xlingmao.dto.ImageDeviceDto;
import com.xlingmao.entity.Group;
import com.xlingmao.model.GroupModel;
import com.xlingmao.model.GroupTreeModel;
import com.xlingmao.service.DeviceGroupService;
import com.xlingmao.service.GroupCheckService;
import com.xlingmao.service.GroupService;
import com.xlingmao.service.ScreenMonitorService;
import com.xlingmao.util.ServiceSingleUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;


import java.util.List;
import java.util.Optional;

/**
 * Created by dell on 2016/11/10.
 */
public class RightConsoleController{

    private GroupService groupService = (GroupService) ServiceSingleUtil.getService(GroupService.class);

    private DeviceGroupService deviceGroupService = (DeviceGroupService) ServiceSingleUtil.getService(DeviceGroupService.class);

    private static WechatManager wechatManager = null;

    private static BulkManager bulkManager = null;

    @FXML
    private CheckBox allControl;

    @FXML
    private Accordion groupTree;


    public void connectAll(){
        ScreenMonitorService.setIsConnectionAll(true);
        refreshTreeGroup();
    }

    public void disconnectAll(){
        ScreenMonitorService.setIsConnectionAll(false);
        refreshTreeGroup();
    }

    public void controlAll(){
        if(allControl.isSelected()){

        }else {

        }
    }

    public void bulkOperateShow(){
        if(bulkManager == null){
            bulkManager =  new BulkManager();
            bulkManager.shows();
        }else{
            bulkManager.shows();
        }
    }

    public void wechatMangerShow(){
        if(wechatManager == null){
            wechatManager =  new WechatManager();
            wechatManager.shows();
        }else{
            wechatManager.shows();
        }
    }

    public void initialize(){
        buildGroupTree();
        GroupCheckService gcs = new GroupCheckService(this,deviceGroupService);
        gcs.setPeriod(Duration.seconds(12));
        gcs.start();
    }

    public void refreshTreeGroup(){
        groupTree.getPanes().clear();
        buildGroupTree();
    }

    private void buildGroupTree(){
        List<GroupModel> groups = groupService.buildGroupTree();
        for (GroupModel group : groups) {
            ObservableList<GroupTreeModel> deviceTreeModel = FXCollections.observableArrayList();

            ListView<GroupTreeModel> listView = new ListView<>();
            TitledPane titlePane = new TitledPane();
            titlePane.setText(group.getGroupName());
            titlePane.setContextMenu(buildAddGroupMenu(titlePane));
            titlePane.setId("group-"+group.getId());
            deviceTreeModel.setAll(group.getGroupTreeModel());
            listView.setItems(deviceTreeModel);
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            listViewRefresh(listView);
            listView.setContextMenu(buildDeviceMenu(listView));
            titlePane.setContent(listView);
            groupTree.getPanes().add(titlePane);
        }
    }

    private ContextMenu buildAddGroupMenu(TitledPane titledPane){
        ContextMenu contextMenu = new ContextMenu();
        final Line line1 = new Line(60, 10, 150, 10);
        final Line line2 = new Line(60, 10, 150, 10);
        MenuItem addMenu = getMenuItemForTitlePane(GroupEvent.ADD_GROUP,line1,null);
        MenuItem delMenu = getMenuItemForTitlePane(GroupEvent.DEL_GROUP,line2,titledPane);
        contextMenu.getItems().addAll(addMenu,delMenu);
        return contextMenu;
    }

    private ContextMenu buildDeviceMenu(ListView<GroupTreeModel> listView){
        ContextMenu contextMenu = new ContextMenu();
        final Line line1 = new Line(60, 10, 150, 10);
        final Line line2 = new Line(60, 10, 150, 10);
        final Line line3 = new Line(60,10,150,10);
        MenuItem cut = getMenuItemForListView(GroupEvent.MOVE_GROUP,line1,listView);
        MenuItem copy= getMenuItemForListView(GroupEvent.REMOVE_FROM_GROUP, line2,listView);
        MenuItem reName = getMenuItemForListView(GroupEvent.RENAME_DEVICE,line3,listView);
        contextMenu.getItems().addAll(cut,copy,reName);
        return contextMenu;
    }

    private MenuItem getMenuItemForListView(GroupEvent groupEvent, final Line line,ListView<GroupTreeModel> listView) {
        Label menuLabel = new Label(groupEvent.getDescriptions());
        menuLabel.setStyle("-fx-padding: 5 10 5 10");
        MenuItem mi = new MenuItem();
        mi.setGraphic(menuLabel);
        line.setStroke(Color.BLUE);

        menuLabel.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            List<GroupTreeModel> selected = listView.getSelectionModel().getSelectedItems();
            if(selected != null && !selected.isEmpty()){
                if(groupEvent.getEventType().equals(GroupEvent.MOVE_GROUP.getEventType())){
                    List<Group> groups = groupService.selectGroups();
                    ChoiceDialog<Group> choice = new ChoiceDialog<>();
                    choice.getItems().setAll(groups);
                    choice.setTitle("移动分组");
                    choice.setHeaderText("选择需要移动到的分组");
                    Optional<Group> op = choice.showAndWait();
                    if(op.isPresent()){
                        for (GroupTreeModel groupTreeModel : selected) {
                            deviceGroupService.updateDeviceGroupIdById(groupTreeModel.getId(),choice.getSelectedItem().getId());
                        }
                    }
                }else if(groupEvent.getEventType().equals(GroupEvent.REMOVE_FROM_GROUP.getEventType())){
                    for (GroupTreeModel groupTreeModel : selected) {
                        if(groupTreeModel.getGroupId() != AppConfig.DEFAULT_GROUP_ID) {
                            deviceGroupService.updateDeviceGroupIdById(groupTreeModel.getId(), AppConfig.DEFAULT_GROUP_ID);
                        }
                    }
                }else if(groupEvent.getEventType().equals(GroupEvent.RENAME_DEVICE.getEventType())) {
                    TextInputDialog textInputDialog = new TextInputDialog();
                    textInputDialog.setTitle("提示");
                    textInputDialog.setHeaderText("请输入名字");
                    textInputDialog.showAndWait().ifPresent(s -> {
                        if (StringUtils.isNotEmpty(s)) {
                            int i = 1;
                            for (GroupTreeModel groupTreeModel : selected) {
                                String tmp = s;
                                if(selected.size() > 1){
                                    tmp += "-" + i ;
                                }
                                deviceGroupService.updateDeviceNameById(groupTreeModel.getId(), tmp);
                                i++;
                            }
                        }
                    });

                }
            }
            refreshTreeGroup();
        });
        return mi;
    }

    private void listViewRefresh(ListView<GroupTreeModel> listView){
        listView.setCellFactory((list) -> new ListCell<GroupTreeModel>() {
            @Override
            protected void updateItem(GroupTreeModel item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    List<IDevice> devices = ScreenMonitorService.getDevices();
                    ImageDeviceDto imageDeviceDto = DeviceSocketThread.getImageMap().get(item.getDeviceName());
                    if(devices == null || devices.isEmpty()){
                        if(imageDeviceDto != null){
                            imageDeviceDto.setOffLine(true);
                        }
                        setText((StringUtils.isNotEmpty(item.getMappingName()) ? item.getMappingName() : item.getDeviceName()) + " (" + AppConfig.CONNECT_NO + ")");
                    }else{
                        String onlineStatus = imageDeviceDto == null || imageDeviceDto.isOffLine() ? AppConfig.CONNECT_NO : AppConfig.CONNECT_OK;
                        setText((StringUtils.isNotEmpty(item.getMappingName()) ? item.getMappingName() : item.getDeviceName()) + " (" + onlineStatus + ")");
                    }
                }
            }
        });
    }

    private MenuItem getMenuItemForTitlePane(GroupEvent groupEvent, final Line line,TitledPane titledPane) {
        Label menuLabel = new Label(groupEvent.getDescriptions());
        menuLabel.setStyle("-fx-padding: 5 10 5 10");
        MenuItem mi = new MenuItem();
        mi.setGraphic(menuLabel);
        line.setStroke(Color.BLUE);
        menuLabel.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (titledPane != null && groupEvent.getEventType().equals(GroupEvent.DEL_GROUP.getEventType())) {
                if (titledPane.getStyleClass().contains("first-titled-pane")) {
                    new Alert(Alert.AlertType.ERROR, "不能删除默认分组").show();
                } else {
                    new Alert(Alert.AlertType.CONFIRMATION, "你确定要删除这个分组吗?").showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            int groupId = Integer.parseInt(titledPane.getId().split("-")[1]);
                            deviceGroupService.updateDeviceGroupId(groupId, AppConfig.DEFAULT_GROUP_ID);
                            groupService.deleteGroup(groupId);
                        }
                    });
                }
            }else if(groupEvent.getEventType().equals(GroupEvent.ADD_GROUP.getEventType())){
                TextInputDialog textInputDialog = new TextInputDialog();
                textInputDialog.setTitle("提示");
                textInputDialog.setHeaderText("请输入分组名");
                textInputDialog.showAndWait().ifPresent(s -> {
                    if(StringUtils.isNotEmpty(s)){
                        Group group = new Group();
                        group.setGroupName(s);
                        groupService.insert(group);
                    }
                });
            }
        });
        return mi;
    }

    private enum GroupEvent{
        ADD_GROUP("addGroup","新增分组"),
        DEL_GROUP("delGroup","删除分组"),
        MOVE_GROUP("moveGroup","移动分组"),
        REMOVE_FROM_GROUP("removeFromGroup","从此分组移除"),
        RENAME_DEVICE("renameDevice","别名选择的设备"),
        ;
        private String eventType;
        private String descriptions;

        GroupEvent(String eventType,String descriptions) {
            this.eventType = eventType;
            this.descriptions = descriptions;
        }

        public static GroupEvent getEvent(String eventType){
            for (GroupEvent groupEvent : GroupEvent.values()) {
                if(eventType.equals(groupEvent.getEventType())){
                    return groupEvent;
                }
            }
            return null;
        }


        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public String getDescriptions() {
            return descriptions;
        }

        public void setDescriptions(String descriptions) {
            this.descriptions = descriptions;
        }
    }
}
