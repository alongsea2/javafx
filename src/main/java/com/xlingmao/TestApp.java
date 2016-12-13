package com.xlingmao;

import com.android.chimpchat.ChimpChat;
import com.android.chimpchat.ChimpManager;
import com.android.chimpchat.adb.AdbChimpDevice;
import com.android.chimpchat.core.IChimpDevice;
import com.android.ddmlib.IDevice;
import com.xlingmao.asserts.ADB;
import com.xlingmao.util.ClickHelpUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Created by alongsea2 on 16/12/6.
 */
public class TestApp extends Application {
    double tmpX;
    double tmpY;
    long beginDrag;
    double bX;
    double bY;
    boolean flag = false;
    Runtime s;
    List<IDevice> devices = ADB.getInstance().getDevices();
    @Override
    public void start(Stage primaryStage) throws Exception {
        s = Runtime.getRuntime();
        primaryStage.setTitle("test");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("/view/initView/RootLayoutBack.fxml"));
        BorderPane rootLayout = loader.load();

        ImageView imageView = (ImageView) rootLayout.getLeft().lookup("#images");

        imageView.setImage(new Image("http://img.hb.aicdn.com/9b3d483d12f91d4d87cd0d30dd264257cd0929b519c14-F3ulHn_/fw/480"));

        imageView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tmpX = event.getX();
                tmpY = event.getY();
                beginDrag = System.currentTimeMillis();
                System.out.println("====>" + beginDrag);
            }
        });

        imageView.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println(tmpX + " :" + tmpY + "|" + event.getX() + ": " + event.getY());
                try {

                    s.exec("/Users/alongsea2/Library/Android/sdk/platform-tools/adb shell input swipe " +tmpX+ " " + tmpY + " " + event.getX() + " " + event.getY() + " 2000");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                flag = true;
            }
        });

        imageView.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event2) {
                if(flag){
                  System.out.println("=====> cost" + (System.currentTimeMillis() - beginDrag));
                  System.out.println("=====> mouse dragged" + tmpX + ":" + tmpY + "|" +  event2.getX() + ":" + event2.getY());
                  flag = false;
                    for (IDevice device : devices) {
                        ClickHelpUtil.swipe(device,tmpX,tmpY,event2.getX(),event2.getY(),(System.currentTimeMillis() - beginDrag));
                    }
                }else{
                    System.out.println("press func");
                }

            }
        });



        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}
