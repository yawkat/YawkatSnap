package at.yawk.snap;

public class KeyboardHandler {
    private static int keyEventCodeToWindowsKey(int keyEventCode) {
        return keyEventCode;
    }
    
    public static boolean isKeyDown(int keyCode) {
        return YawkatSnapNatives.isKeyPressed(keyEventCodeToWindowsKey(keyCode)) != 0;
    }
}
