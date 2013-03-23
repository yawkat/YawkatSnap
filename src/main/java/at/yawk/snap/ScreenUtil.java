package at.yawk.snap;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class ScreenUtil {
    private ScreenUtil() {
    }
    
    public static GraphicsDevice getGraphicsDevice() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
    }
}
