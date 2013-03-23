package at.yawk.snap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

public class NativeLoader {
    private static final String systemFolderName = "win64";
    private static final String systemNativeSuffix = ".dll";
    
    private static final Collection<String> loadedNatives = new HashSet<String>(4);
    
    private NativeLoader() {
    }
    
    static void ensureNativeLoaded(String name) {
        if (!loadedNatives.contains(name)) {
            try {
                loadNative(name);
                loadedNatives.add(name);
            } catch (IOException e) {
                throw new UnsatisfiedLinkError("no " + name + " in java.library.path");
            }
        }
    }
    
    private static void loadNative(File file) {
        System.load(file.getAbsolutePath());
    }
    
    private static void loadNative(String name) throws IOException {
        try {
            System.loadLibrary(name);
            return;
        } catch (UnsatisfiedLinkError e) {
        }
        final URL url = NativeLoader.class.getResource("/natives/" + systemFolderName + "/" + name + systemNativeSuffix);
        if(url == null) {
            throw new FileNotFoundException();
        }
        final File targetTempFile = File.createTempFile("native.", systemNativeSuffix);
        final OutputStream targetStream = new FileOutputStream(targetTempFile);
        final InputStream sourceStream = url.openStream();
        final byte[] cache = new byte[1024];
        int length;
        while ((length = sourceStream.read(cache)) > 0) {
            targetStream.write(cache, 0, length);
        }
        targetStream.close();
        sourceStream.close();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                targetTempFile.delete();
            }
        }));
        loadNative(targetTempFile);
    }
}
