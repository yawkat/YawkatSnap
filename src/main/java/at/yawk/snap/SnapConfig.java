package at.yawk.snap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class SnapConfig {
    private SaveTarget saveTarget;
    private IdGenerator idGenerator;
    private ImageCropDisplay cropDisplay;
    private String targetUrl;
    
    private final File configFile;
    private final Properties properties = new Properties();
    
    public SnapConfig(File configFile) throws IOException {
        this.configFile = configFile;
        if (configFile.exists()) {
            load();
        }
        fillDefaults();
        setCropDisplay(new FullscreenFrameCropDisplay());
    }
    
    public SaveTarget getSaveTarget() {
        return saveTarget;
    }
    
    public void setSaveTarget(SaveTarget saveTarget) {
        this.saveTarget = saveTarget;
        save();
    }
    
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }
    
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        save();
    }
    
    public ImageCropDisplay getCropDisplay() {
        return cropDisplay;
    }
    
    public void setCropDisplay(ImageCropDisplay cropDisplay) {
        this.cropDisplay = cropDisplay;
        save();
    }
    
    public String getTargetUrl() {
        return targetUrl;
    }
    
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
    
    private void load() throws IOException {
        properties.load(new FileReader(configFile));
        saveTarget = SaveTargets.getSaveTarget(properties.getProperty("save.type"), properties);
        idGenerator = IdGenerators.getIdGenerator(properties.getProperty("idgen.type"), properties);
        targetUrl = properties.getProperty("clipboard");
    }
    
    private void save() {
        final String saveType = SaveTargets.getSaveTargetType(saveTarget.getClass());
        if (saveType != null) {
            properties.setProperty("save.type", saveType);
        }
        final String idType = IdGenerators.getIdGeneratorType(idGenerator.getClass());
        if (idType != null) {
            properties.setProperty("idgen.type", idType);
        }
        properties.setProperty("clipboard", targetUrl);
        try {
            properties.store(new FileWriter(configFile), "Configuration file for YawkatSnap");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void fillDefaults() {
        if (saveTarget == null) {
            saveTarget = new SaveTarget() {
                @Override
                public String saveTo(BufferedImage image, IdGenerator idGenerator, UpdateMonitor monitor) throws Exception {
                    throw new IllegalStateException("No save target has been specified yet!");
                }
                
                @Override
                public void setProperties(Properties properties) {
                }
            };
        }
        if (idGenerator == null) {
            idGenerator = new IdGenerator() {
                @Override
                public String generateId(long timeMilliSeconds, long timeSeconds) {
                    throw new IllegalStateException("No id generator has been specified yet!");
                }
                
                @Override
                public void serializeSettings(Properties properties) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        if (targetUrl == null) {
            targetUrl = "%id";
        }
    }
    
    public boolean isAutostart() {
        return new AutoStartProgram("YawkatSnap").isAutoStart(YawkatSnap.class);
    }
    
    public void setAutostart(boolean autostart) {
        new AutoStartProgram("YawkatSnap").setAutoStart(YawkatSnap.class, autostart);
    }

    public Properties getProperties() {
        return properties;
    }
}
