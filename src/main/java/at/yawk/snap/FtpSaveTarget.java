package at.yawk.snap;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class FtpSaveTarget implements SaveTarget {
    private static final int CHUNK_LENGTH = 1024;
    
    private Properties properties;
    
    public FtpSaveTarget() {
        this(null);
    }
    
    public FtpSaveTarget(final Properties properties) {
        this.setProperties(properties);
    }
    
    @Override
    public String saveTo(final RenderedImage image, final IdGenerator idGenerator, final UpdateMonitor progressUpdate) throws Exception {
        final String username = this.properties.getProperty("save.ftp.username");
        final String password = this.properties.getProperty("save.ftp.password");
        final String hostname = this.properties.getProperty("save.ftp.host");
        final int port = Integer.parseInt(this.properties.getProperty("save.ftp.port"));
        final String directory = this.properties.getProperty("save.ftp.directory");
        final String filename = this.properties.getProperty("save.ftp.filename");
        final String filetype = this.properties.getProperty("save.ftp.filetype");
        
        progressUpdate.setValue(0F);
        final FTPClient ftpClient = new FTPClient();
        ftpClient.connect(hostname, port);
        if (username != null && password != null) {
            ftpClient.login(username, password);
        }
        ftpClient.enterLocalPassiveMode();
        progressUpdate.setValue(0.1F);
        final Set<String> names = new HashSet<String>(Arrays.asList(ftpClient.listNames(directory)));
        String newFileName;
        String id;
        do {
            id = idGenerator.generateId(System.currentTimeMillis(), System.currentTimeMillis() / 1000L);
            newFileName = filename.replace("%id", id);
        } while (names.contains(newFileName));
        progressUpdate.setValue(0.2F);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SnapImageIO.write(image, filetype, stream);
        stream.flush();
        final byte[] imageData = stream.toByteArray();
        progressUpdate.setValue(0.3F);
        ftpClient.storeFile(directory + newFileName, new CountingInputStream(new ByteArrayInputStream(imageData)) {
            private int counter = 0;
            
            @Override
            protected void afterRead(final int n) {
                super.afterRead(n);
                this.counter += n;
                if (this.counter >= CHUNK_LENGTH) {
                    this.counter %= CHUNK_LENGTH;
                    progressUpdate.setValue(0.7F * this.getByteCount() / imageData.length + 0.3F);
                }
            }
        });
        ftpClient.disconnect();
        return id;
    }
    
    @Override
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }
}
