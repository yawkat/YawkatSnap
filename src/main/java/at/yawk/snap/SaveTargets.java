package at.yawk.snap;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SaveTargets {
    private static final Map<String, Class<? extends SaveTarget>> saveTargetTypes = new HashMap<String, Class<? extends SaveTarget>>();
    
    private SaveTargets() {
    }
    
    static void registerSaveTargetType(String typeName, Class<? extends SaveTarget> type) {
        assert typeName != null;
        assert type != null;
        try {
            if (type.getConstructor(Properties.class) == null) {
                throw new IllegalArgumentException("Invalid save target type " + type.getName() + ": no java.util.Properties constructor defined");
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Invalid save target type " + type.getName() + ": no java.util.Properties constructor defined");
        } catch (SecurityException e) {
            throw new Error(e);
        }
        
        saveTargetTypes.put(typeName, type);
    }
    
    static {
        registerSaveTargetType("ftp", FtpSaveTarget.class);
    }
    
    public static SaveTarget getSaveTarget(String type, Properties settings) {
        final Class<? extends SaveTarget> target = saveTargetTypes.get(type);
        if (target == null) {
            return null;
        }
        try {
            return target.getConstructor(Properties.class).newInstance(settings);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    public static String getSaveTargetType(Class<? extends SaveTarget> clazz) {
        for (String key : saveTargetTypes.keySet()) {
            if (saveTargetTypes.get(key) == clazz) {
                return key;
            }
        }
        return null;
    }
}
