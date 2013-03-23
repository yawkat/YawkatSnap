package at.yawk.snap;

class YawkatSnapNatives {
    static {
        NativeLoader.ensureNativeLoaded("yawkatsnap");
    }
    
    static native int isKeyPressed(int windowsKey);
    
    static native int checkAutoStart(String javaPath, String programJarPath, String programMainClass, String programName);
    
    static native int setAutoStart(String javaPath, String programJarPath, String programMainClass, String programName);
    
    static native int unsetAutoStart(String javaPath, String programJarPath, String programMainClass, String programName);
}
