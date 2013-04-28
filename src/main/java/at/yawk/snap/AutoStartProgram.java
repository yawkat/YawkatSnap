package at.yawk.snap;

import java.io.File;
import java.net.URISyntaxException;

@SuppressWarnings("unused")
public class AutoStartProgram {
    private final String name;
    
    public AutoStartProgram(String name) {
        this.name = name;
    }
    
    public boolean isAutoStart(Class<?> mainClass) {
        return isAutoStart(mainClass.getName());
    }
    
    private boolean isAutoStart(String mainClassName) {
        /*
        return YawkatSnapNatives.checkAutoStart(getJavaPath(), getProgramPath(), mainClassName, name) == 0;
        */
        return false;
    }
    
    public boolean setAutoStart(Class<?> mainClass, boolean autoStart) {
        return setAutoStart(mainClass.getName(), autoStart);
    }
    
    private boolean setAutoStart(String mainClassName, boolean autoStart) {
        /*
        if (autoStart) {
            return YawkatSnapNatives.setAutoStart(getJavaPath(), getProgramPath(), mainClassName, name) == 0;
        } else {
            return YawkatSnapNatives.unsetAutoStart(getJavaPath(), getProgramPath(), mainClassName, name) == 0;
        }
        */
        return false;
    }
    
    private String getJavaPath() {
        return new File(new File(System.getProperty("java.home")), "bin/javaw.exe").getAbsolutePath().replace("\\", "\\\\");
    }
    
    private String getProgramPath() {
        try {
            return new File(AutoStartProgram.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath().replace("\\", "\\\\");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
