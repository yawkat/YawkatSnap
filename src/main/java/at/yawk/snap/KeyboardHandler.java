package at.yawk.snap;

import java.awt.Event;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

public class KeyboardHandler {
    public static final int MASK_CTRL = 0x1;
    public static final int MASK_ALT = 0x2;
    public static final int MASK_SHIFT = 0x4;
    
    private static int uniqueIdentifier = 0;
    
    /**
     * Register a new hotkey.
     * 
     * @param hotkey
     *            The key ID
     * @param swingMask
     *            Mask generated using the {@link Event} class. Supported are
     *            {@link Event#CTRL_MASK}, {@link Event#ALT_MASK} and
     *            {@link Event#SHIFT_MASK}
     * @param listener
     *            Callback to be run when the hotkey is pressed
     * @return A unique identifier that can be used to unregister this hotkey
     *         using {@link #unregisterHotkey(Object)}
     */
    public static synchronized Object registerHotkey(int hotkey, int mask, final Runnable listener) {
        final Integer id = ++uniqueIdentifier;
        int jimask = 0;
        if ((mask & MASK_ALT) != 0) {
            jimask |= JIntellitype.MOD_ALT;
        }
        if ((mask & MASK_SHIFT) != 0) {
            jimask |= JIntellitype.MOD_SHIFT;
        }
        if ((mask & MASK_CTRL) != 0) {
            jimask |= JIntellitype.MOD_CONTROL;
        }
        JIntellitype.getInstance().registerHotKey(id, jimask, hotkey);
        JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
            @Override
            public void onHotKey(int arg0) {
                if (id == arg0) {
                    try {
                        listener.run();
                    } catch (Throwable t) {
                        Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), t);
                    }
                }
            }
        });
        return id;
    }
    
    public static synchronized void unregisterHotkey(Object identifier) {
        if (identifier == null) {
            throw new NullPointerException();
        }
        if (!(identifier instanceof Integer)) {
            throw new IllegalArgumentException("Invalid identifier");
        }
        JIntellitype.getInstance().unregisterHotKey((Integer) identifier);
    }
}
