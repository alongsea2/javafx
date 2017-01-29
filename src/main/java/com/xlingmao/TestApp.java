package com.xlingmao;


import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by alongsea2 on 16/12/6.
 */
public class TestApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("test");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(TestApp.class.getResource("/view/initView/RootLayoutBack.fxml"));
        BorderPane rootLayout = loader.load();

        Button imageView = (Button) rootLayout.getLeft().lookup("#images");

        //imageView.setImage(new Image("http://img.hb.aicdn.com/9b3d483d12f91d4d87cd0d30dd264257cd0929b519c14-F3ulHn_/fw/480"));
        JFXDialog jfxDialog = new JFXDialog();
        JFXTextArea jfxTextArea = new JFXTextArea("fjsldkjfkl");
        jfxTextArea.setStyle("-fx-text-fill: #000000");
        jfxTextArea.setPrefHeight(300);
        jfxTextArea.setPrefWidth(400);
        StackPane stackPane = new StackPane();
        stackPane.setPrefWidth(300);
        stackPane.setPrefHeight(400);
        stackPane.setStyle("-fx-background-color: aliceblue");
        stackPane.getChildren().addAll(jfxDialog);
        jfxDialog.setContent(jfxTextArea);
        jfxDialog.setDialogContainer(stackPane);
        jfxDialog.setPrefHeight(300);
        jfxDialog.setPrefWidth(400);
        rootLayout.setRight(stackPane);
        jfxDialog.setOverlayClose(false);
        ServiceUtil serviceUtil = new ServiceUtil();
        imageView.setOnMouseClicked(event -> {
            //ThreadPoolUtil.getPoolA().execute(()->{
                serviceUtil.runFunc();
            //});
        });




        Scene scene = new Scene(rootLayout);

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}
