package com.xlingmao.dto;

import com.android.ddmlib.IDevice;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.net.Socket;

/**
 * Created by dell on 2016/11/3.
 */
public class ImageDeviceDto {
    private ImageView imageView;
    private IDevice device;
    private Socket socket;
    private boolean isOffLine = true;
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean isOffLine() {
        return isOffLine;
    }

    public void setOffLine(boolean offLine) {
        isOffLine = offLine;
    }

    public IDevice getDevice() {
        return device;
    }

    public void setDevice(IDevice iDevice) {
        this.device = iDevice;
    }


    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

}
