package at.yawk.snap;

import java.awt.Event;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jxgrabkey.HotkeyConflictException;
import jxgrabkey.JXGrabKey;

import com.melloware.jintellitype.JIntellitype;

public abstract class KeyboardHandler {
    public static final int MASK_CTRL = 0x1;
    public static final int MASK_ALT = 0x2;
    public static final int MASK_SHIFT = 0x4;
    
    private static final KeyboardHandler INSTANCE = createInstance();
    
    private KeyboardHandler() {}
    
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
    public abstract Object registerHotkey(final int hotkey, final int mask, final Runnable listener) throws Exception;
    
    public abstract void unregisterHotkey(final Object identifier) throws Exception;
    
    public static final KeyboardHandler getInstance() {
        return INSTANCE;
    }
    
    private static final KeyboardHandler createInstance() {
        final String lowercaseOs = System.getProperty("os.name").toLowerCase();
        if (lowercaseOs.indexOf("win") != -1) {
            return new WindowsKeyboardHandler();
        } else if (lowercaseOs.indexOf("nix") != -1 || lowercaseOs.indexOf("nux") != -1) {
            return new UnixKeyboardHandler();
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + lowercaseOs);
        }
    }
    
    private static abstract class JxLikeKeyboardHandler extends KeyboardHandler {
        private int uniqueIdentifier = 0;
        
        public JxLikeKeyboardHandler() {
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    JxLikeKeyboardHandler.this.cleanupImpl();
                }
            }));
        }
        
        @Override
        public synchronized Object registerHotkey(final int hotkey, final int mask, final Runnable listener) throws Exception {
            final Integer id = ++this.uniqueIdentifier;
            int jimask = 0;
            if ((mask & MASK_ALT) != 0) {
                jimask |= 1;
            }
            if ((mask & MASK_CTRL) != 0) {
                jimask |= 2;
            }
            if ((mask & MASK_SHIFT) != 0) {
                jimask |= 4;
            }
            this.registerHotkeyImpl(id, jimask, hotkey, listener);
            return id;
        }
        
        @Override
        public synchronized void unregisterHotkey(final Object identifier) throws Exception {
            if (identifier == null) {
                throw new NullPointerException();
            }
            if (!(identifier instanceof Integer)) {
                throw new IllegalArgumentException("Invalid identifier");
            }
            this.unregisterHotkey(identifier);
        }
        
        protected abstract void cleanupImpl();
        
        protected abstract void registerHotkeyImpl(final int id, final int mask, final int key, final Runnable task) throws Exception;
        
        protected abstract void unregisterHotkey(final int id) throws Exception;
    }
    
    private static final class WindowsKeyboardHandler extends JxLikeKeyboardHandler {
        @Override
        protected void cleanupImpl() {
            JIntellitype.getInstance().cleanUp();
        }
        
        @Override
        protected void unregisterHotkey(final int id) {
            JIntellitype.getInstance().unregisterHotKey(id);
        }
        
        @Override
        protected void registerHotkeyImpl(final int id, final int mask, final int key, final Runnable task) {
            JIntellitype.getInstance().registerHotKey(id, mask, key);
            JIntellitype.getInstance().addHotKeyListener(new com.melloware.jintellitype.HotkeyListener() {
                @Override
                public void onHotKey(final int arg0) {
                    if (id == arg0) {
                        try {
                            task.run();
                        } catch (final Throwable t) {
                            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), t);
                        }
                    }
                }
            });
        }
    }
    
    private static final class UnixKeyboardHandler extends JxLikeKeyboardHandler {
        protected UnixKeyboardHandler() {
            // from JIntelliType native loader
            try {
                // Load JNI library
                System.loadLibrary("JXGrabKey");
            } catch (final Throwable ex) {
                try {
                    final String jarPath = "jxgrabkey";
                    final String tmpDir = System.getProperty("java.io.tmpdir");
                    try {
                        final String dll = "/JXGrabKey.so";
                        this.fromJarToFs(jarPath + dll, tmpDir + dll);
                        System.load(tmpDir + dll);
                    } catch (final UnsatisfiedLinkError e) {
                        final String dll = "/JXGrabKey64.so";
                        this.fromJarToFs(jarPath + dll, tmpDir + dll);
                        System.load(tmpDir + dll);
                    }
                } catch (final Throwable ex2) {
                    throw new RuntimeException("Could not load JXGrabKey.so from local file system or from inside JAR", ex2);
                }
            }
        }
        
        private void fromJarToFs(final String jarPath, final String filePath) throws IOException {
            final File file = new File(filePath);
            if (file.exists()) {
                final boolean success = file.delete();
                if (!success) {
                    throw new IOException("couldn't delete " + filePath);
                }
            }
            InputStream is = null;
            OutputStream os = null;
            try {
                is = ClassLoader.getSystemClassLoader().getResourceAsStream(jarPath);
                os = new FileOutputStream(filePath);
                final byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            } finally {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            }
        }
        
        @Override
        protected void cleanupImpl() {
            // This is broken and gets stuck in an infinite loop
            // JXGrabKey.getInstance().cleanUp();
        }
        
        @Override
        protected void unregisterHotkey(final int id) {
            JXGrabKey.getInstance().unregisterHotKey(id);
        }
        
        @Override
        protected void registerHotkeyImpl(final int id, final int mask, final int key, final Runnable task) throws HotkeyConflictException {
            JXGrabKey.getInstance().registerAwtHotkey(id, mask, key);
            JXGrabKey.getInstance().addHotkeyListener(new jxgrabkey.HotkeyListener() {
                @Override
                public void onHotkey(final int arg0) {
                    if (id == arg0) {
                        try {
                            task.run();
                        } catch (final Throwable t) {
                            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), t);
                        }
                    }
                }
            });
        }
    }
}
