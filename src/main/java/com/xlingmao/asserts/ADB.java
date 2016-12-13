/*
 * Copyright (C) 2009-2013 adakoda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xlingmao.asserts;

import com.android.ddmlib.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * adb连接安卓
 * 获取连接的device
 * thanks to https://github.com/adakoda/android-screen-monitor
 * */
public class ADB {
	
	private static Object lock = new Object();
	
    private static final int DEFAULT_WIDTH = 320;
    private static final int DEFAULT_HEIGHT = 480;

    private static final String EXT_PNG = "png";

    private static final int FB_TYPE_XBGR = 0;
    private static final int FB_TYPE_RGBX = 1;
    private static final int FB_TYPE_XRGB = 2;

    private static int[][] FB_OFFSET_LIST = {
            {0, 1, 2, 3}, // XBGR : Normal
            {3, 2, 1, 0}, // RGBX : Xperia Arc
            {2, 1, 0, 3}  // XRGB : FireFox OS(B2G)
    };

    private boolean mPortrait = true;

    private double mZoom = 1.0;

    private int mFbType = FB_TYPE_XBGR;

	private AndroidDebugBridge mAndroidDebugBridge;
	private static ADB self;
	
	private ADB(){
		initialize();
	}
	
	public static ADB getInstance(){
		if(self == null){
			synchronized (lock) {
				if(self ==null){
					self = new ADB();
				}
			}
		}
		return self;
	}
	

	public boolean initialize() {
		boolean success = true;

		String adbLocation = System
				.getProperty("com.android.screenshot.bindir");

		// You can specify android sdk directory using first argument
		// A) If you lunch jar from eclipse, set arguments in Run/Debug configurations to android sdk directory .
		//    /Applications/adt-bundle-mac-x86_64/sdk
		// A) If you lunch jar from terminal, set arguments to android sdk directory or $ANDROID_HOME environment variable.
		//    java -jar ./jar/asm.jar $ANDROID_HOME
		if (adbLocation == null) {
			//adbLocation = System.getenv("ANDROID_HOME");
			// Here, adbLocation may be android sdk directory
			if (adbLocation != null) {
				adbLocation += File.separator + "platform-tools";
			}
		}

		// for debugging (follwing line is a example)
//		adbLocation = "C:\\ ... \\android-sdk-windows\\platform-tools"; // Windows
//		adbLocation = "/ ... /adt-bundle-mac-x86_64/sdk/platform-tools"; // MacOS X
		
		if (success) {
			if ((adbLocation != null) && (adbLocation.length() != 0)) {
				adbLocation += File.separator + "adb";
			} else {
				adbLocation = "/Users/alongsea2/Library/Android/sdk/platform-tools/adb";
			}
			System.out.println("adb path is " + adbLocation);
			AndroidDebugBridge.init(false);
			mAndroidDebugBridge = AndroidDebugBridge.createBridge(adbLocation,
					true);
			if (mAndroidDebugBridge == null) {
				success = false;
			}
		}

		if (success) {
			int count = 0;
			while (mAndroidDebugBridge.hasInitialDeviceList() == false) {
				try {
					Thread.sleep(100);
					count++;
				} catch (InterruptedException e) {
				}
				if (count > 100) {
					success = false;
					break;
				}
			}
		}

		if (!success) {
			terminate();
		}

		return success;
	}

	public void terminate() {
		AndroidDebugBridge.terminate();
	}

	public List<IDevice> getDevices() {
        IDevice[] devices = mAndroidDebugBridge.getDevices();
        List<IDevice> iDevicesList = new ArrayList<>();
        for (IDevice device : devices) {
            iDevicesList.add(device);
        }
        return devices.length > 0 ? iDevicesList : null;
	}

    public FBImage getDeviceImage(IDevice mDevice) throws IOException {
        boolean success = true;
        boolean debug = false;
        FBImage fbImage = null;
        RawImage tmpRawImage = null;
        RawImage rawImage = null;
        if (success) {
            try {
                //long bs = System.currentTimeMillis();
                tmpRawImage = mDevice.getScreenshot(5, TimeUnit.SECONDS);
                //System.out.println(System.currentTimeMillis() - bs);
                if (tmpRawImage == null) {
                    success = false;
                } else {
                    if (debug == false) {
                        rawImage = tmpRawImage;
                        rawImage.bpp = 32;
                        rawImage.size = tmpRawImage.width
                                * tmpRawImage.height * 4;
                        rawImage.width = tmpRawImage.width;
                        rawImage.height = tmpRawImage.height;
                    } else {
                        rawImage = new RawImage();
                        rawImage.version = 1;
                        rawImage.bpp = 32;
                        rawImage.size = tmpRawImage.width
                                * tmpRawImage.height * 4;
                        rawImage.width = tmpRawImage.width;
                        rawImage.height = tmpRawImage.height;
                        rawImage.red_offset = 0;
                        rawImage.red_length = 8;
                        rawImage.blue_offset = 16;
                        rawImage.blue_length = 8;
                        rawImage.green_offset = 8;
                        rawImage.green_length = 8;
                        rawImage.alpha_offset = 0;
                        rawImage.alpha_length = 0;
                        rawImage.data = new byte[rawImage.size];

                        int index = 0;
                        int dst = 0;
                        for (int y = 0; y < rawImage.height; y++) {
                            for (int x = 0; x < rawImage.width; x++) {
                                int value = tmpRawImage.data[index++] & 0x00FF;
                                value |= (tmpRawImage.data[index++] << 8) & 0xFF00;
                                int r = ((value >> 11) & 0x01F) << 3;
                                int g = ((value >> 5) & 0x03F) << 2;
                                int b = ((value >> 0) & 0x01F) << 3;
                                // little endian
                                rawImage.data[dst++] = (byte) r;
                                rawImage.data[dst++] = (byte) g;
                                rawImage.data[dst++] = (byte) b;
                                rawImage.data[dst++] = (byte) 0xFF;
//									// big endian
//									rawImage.data[dst++] = (byte) 0xFF;
//									rawImage.data[dst++] = (byte) b;
//									rawImage.data[dst++] = (byte) g;
//									rawImage.data[dst++] = (byte) r;
                            }
                        }
                    }
                }
            } catch (IOException ioe) {
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (AdbCommandRejectedException e) {
                e.printStackTrace();
            } finally {
                if ((rawImage == null)
                        || ((rawImage.bpp != 16) && (rawImage.bpp != 32))) {
                    success = false;
                }
            }
        }

        if (success) {
            final int imageWidth;
            final int imageHeight;

            if (mPortrait) {
                imageWidth = rawImage.width;
                imageHeight = rawImage.height;
            } else {
                imageWidth = rawImage.height;
                imageHeight = rawImage.width;
            }

            fbImage = new FBImage(imageWidth, imageHeight,
                    BufferedImage.TYPE_INT_RGB, // BufferedImage.TYPE_INT_ARGB
                    rawImage.width, rawImage.height);

            final byte[] buffer = rawImage.data;
            final int redOffset = rawImage.red_offset;
            final int greenOffset = rawImage.green_offset;
            final int blueOffset = rawImage.blue_offset;
            final int alphaOffset = rawImage.alpha_offset;
            final int redMask = getMask(rawImage.red_length);
            final int greenMask = getMask(rawImage.green_length);
            final int blueMask = getMask(rawImage.blue_length);
            final int alphaMask = getMask(rawImage.alpha_length);
            final int redShift = (8 - rawImage.red_length);
            final int greenShift = (8 - rawImage.green_length);
            final int blueShift = (8 - rawImage.blue_length);
            final int alphaShift = (8 - rawImage.alpha_length);

            int index = 0;

            final int offset0;
            final int offset1;
            final int offset2;
            final int offset3;


            if (rawImage.bpp == 16) {
                offset0 = 0;
                offset1 = 1;
                if (mPortrait) {
                    int[] lineBuf = new int[rawImage.width];
                    for (int y = 0; y < rawImage.height; y++) {
                        for (int x = 0; x < rawImage.width; x++, index += 2) {
                            int value = buffer[index + offset0] & 0x00FF;
                            value |= (buffer[index + offset1] << 8) & 0xFF00;
                            int r = ((value >>> redOffset) & redMask) << redShift;
                            int g = ((value >>> greenOffset) & greenMask) << greenShift;
                            int b = ((value >>> blueOffset) & blueMask) << blueShift;
                            lineBuf[x] = 255 << 24 | r << 16 | g << 8 | b;
                        }
                        fbImage.setRGB(0, y, rawImage.width, 1, lineBuf, 0,
                                rawImage.width);
                    }
                } else {
                    for (int y = 0; y < rawImage.height; y++) {
                        for (int x = 0; x < rawImage.width; x++) {
                            int value = buffer[index + offset0] & 0x00FF;
                            value |= (buffer[index + offset1] << 8) & 0xFF00;
                            int r = ((value >>> redOffset) & redMask) << redShift;
                            int g = ((value >>> greenOffset) & greenMask) << greenShift;
                            int b = ((value >>> blueOffset) & blueMask) << blueShift;
                            value = 255 << 24 | r << 16 | g << 8 | b;
                            index += 2;
                            fbImage
                                    .setRGB(y, rawImage.width - x - 1,
                                            value);
                        }
                    }
                }
            } else if (rawImage.bpp == 32) {
                offset0 = FB_OFFSET_LIST[mFbType][0];
                offset1 = FB_OFFSET_LIST[mFbType][1];
                offset2 = FB_OFFSET_LIST[mFbType][2];
                offset3 = FB_OFFSET_LIST[mFbType][3];
                if (mPortrait) {
                    int[] lineBuf = new int[rawImage.width];
                    for (int y = 0; y < rawImage.height; y++) {
                        for (int x = 0; x < rawImage.width; x++, index += 4) {
                            int value = buffer[index + offset0] & 0x00FF;
                            value |= (buffer[index + offset1] & 0x00FF) << 8;
                            value |= (buffer[index + offset2] & 0x00FF) << 16;
                            value |= (buffer[index + offset3] & 0x00FF) << 24;
                            final int r = ((value >>> redOffset) & redMask) << redShift;
                            final int g = ((value >>> greenOffset) & greenMask) << greenShift;
                            final int b = ((value >>> blueOffset) & blueMask) << blueShift;
                            final int a;
                            if (rawImage.alpha_length == 0) {
                                a = 0xFF;
                            } else {
                                a = ((value >>> alphaOffset) & alphaMask) << alphaShift;
                            }
                            lineBuf[x] = a << 24 | r << 16 | g << 8 | b;
                        }
                        fbImage.setRGB(0, y, rawImage.width, 1, lineBuf, 0,
                                rawImage.width);
                    }
                } else {
                    for (int y = 0; y < rawImage.height; y++) {
                        for (int x = 0; x < rawImage.width; x++) {
                            int value;
                            value = buffer[index + offset0] & 0x00FF;
                            value |= (buffer[index + offset1] & 0x00FF) << 8;
                            value |= (buffer[index + offset2] & 0x00FF) << 16;
                            value |= (buffer[index + offset3] & 0x00FF) << 24;
                            final int r = ((value >>> redOffset) & redMask) << redShift;
                            final int g = ((value >>> greenOffset) & greenMask) << greenShift;
                            final int b = ((value >>> blueOffset) & blueMask) << blueShift;
                            final int a;
                            if (rawImage.alpha_length == 0) {
                                a = 0xFF;
                            } else {
                                a = ((value >>> alphaOffset) & alphaMask) << alphaShift;
                            }
                            value = a << 24 | r << 16 | g << 8 | b;
                            index += 4;
                            fbImage
                                    .setRGB(y, rawImage.width - x - 1,
                                            value);
                        }
                    }
                }

            }
        }
        return fbImage;
    }

    public int getMask(int length) {
        int res = 0;
        for (int i = 0 ; i < length ; i++) {
            res = (res << 1) + 1;
        }

        return res;
    }

}
