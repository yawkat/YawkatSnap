package at.yawk.snap;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.util.Properties;

public class SnapConfig {
    private SaveTarget saveTarget;
    private IdGenerator idGenerator;
    private ImageCropDisplay cropDisplay;
    private String targetUrl;
    
    private final File configFile;
    private final Properties properties = new Properties(getDefaults());
    
    public SnapConfig(File configFile) throws IOException {
        this.configFile = configFile;
        if (configFile.exists()) {
            load();
        }
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
        if(saveTarget != null) {
            final String saveType = SaveTargets.getSaveTargetType(saveTarget.getClass());
            if (saveType != null) {
                properties.setProperty("save.type", saveType);
            }
        }
        if(idGenerator != null) {
            final String idType = IdGenerators.getIdGeneratorType(idGenerator.getClass());
            if (idType != null) {
                properties.setProperty("idgen.type", idType);
            }
        }
        if(targetUrl != null) {
            properties.setProperty("clipboard", targetUrl);
        }
        try {
            properties.store(new FileWriter(configFile), "Configuration file for YawkatSnap");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Properties getDefaults() {
        final Properties defaults = new Properties();
        try {
            defaults.load(YawkatSnap.class.getResourceAsStream("/example-config.properties"));
        } catch (IOException e) {
            throw new IOError(e);
        }
        return defaults;
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
