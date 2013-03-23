package at.yawk.snap;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class IdGenerators {
    private static final Map<String, Class<? extends IdGenerator>> idGeneratorTypes = new HashMap<String, Class<? extends IdGenerator>>();
    
    private IdGenerators() {
    }
    
    static void registerIdGeneratorType(String typeName, Class<? extends IdGenerator> type) {
        assert typeName != null;
        assert type != null;
        try {
            if (type.getConstructor(Properties.class) == null) {
                throw new IllegalArgumentException("Invalid id generator type " + type.getName() + ": no java.util.Properties constructor defined");
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Invalid id generator type " + type.getName() + ": no java.util.Properties constructor defined");
        } catch (SecurityException e) {
            throw new Error(e);
        }
        
        idGeneratorTypes.put(typeName, type);
    }
    
    static {
        registerIdGeneratorType("regex", RegexIdGenerator.class);
    }
    
    public static IdGenerator getIdGenerator(String type, Properties settings) {
        final Class<? extends IdGenerator> target = idGeneratorTypes.get(type);
        if (target == null) {
            return null;
        }
        try {
            return target.getConstructor(Properties.class).newInstance(settings);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    public static String getIdGeneratorType(Class<? extends IdGenerator> clazz) {
        for (String key : idGeneratorTypes.keySet()) {
            if (idGeneratorTypes.get(key) == clazz) {
                return key;
            }
        }
        return null;
    }
}
