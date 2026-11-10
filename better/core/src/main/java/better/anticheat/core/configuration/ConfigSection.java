package better.anticheat.core.configuration;

import java.io.Serializable;
import java.util.*;

/*
 * Due to present issues with JitPack, this is a copy-paste of sharkbyte-configuration's snakeyaml module.
 */

public class ConfigSection {

    private final ConfigSection parent;
    private final String key;
    private final Map<String, Object> config;

    private ConfigSection(ConfigSection parent, Map<String, Object> config, String key) {
        this.parent = parent;
        this.config = config;
        this.key = key;
    }

    public ConfigSection(Map<String, Object> config) {
        this.parent = null;
        this.config = config;
        key = "";
    }

    public ConfigSection getParent() {
        return parent;
    }

    public ConfigSection getRoot() {
        ConfigSection section = this;
        while (section.getParent() != null) section = section.getParent();
        return section;
    }

    public Set<ConfigSection> getChildren() {
        Set<ConfigSection> children = new HashSet<>();

        for (String key : config.keySet()) {
            Object obj = config.get(key);

            if (!(obj instanceof Map)) continue;
            Map<?, ?> map = (Map<?, ?>) obj;
            if (!map.keySet().stream().allMatch(a -> a instanceof String)) continue;
            Map<String, Object> finalMap = (Map<String, Object>) map;

            children.add(new ConfigSection(this, finalMap, key));
        }

        return children;
    }

    public String getKey() {
        return key;
    }

    public ConfigSection getConfigSection(Object... keys) {
        ConfigSection section = this;
        for (Object key : keys) {
            if (!hasNode(key)) return null;

            Object obj = section.config.get(key.toString());

            if (!(obj instanceof Map)) continue;
            Map<?, ?> map = (Map<?, ?>) obj;
            if (!map.keySet().stream().allMatch(a -> a instanceof String)) continue;
            Map<String, Object> finalMap = (Map<String, Object>) map;

            section = new ConfigSection(section, finalMap, key.toString());
        }
        return section;    }

    public <E extends Serializable> List<E> getList(Class<E> classType, String node) {
        List<E> obj;

        try {
            if (config.get(node) instanceof List) obj = (List<E>) config.get(node);
            else {
                obj = new ArrayList<>();
                obj.add((E) config.get(node));
            }
        } catch (Exception e) {
            obj = new ArrayList<>();
        }

        if (obj == null) obj = new ArrayList<>();

        return obj;
    }

    public <E extends Serializable> E getObject(Class<E> classType, String node, E defaultValue) {
        E obj;

        try {
            obj = (E) config.get(node);
        } catch (Exception e) {
            obj = defaultValue;
        }

        return obj == null ? defaultValue : obj;
    }

    public boolean hasNode(Object node) {
        return config.get(node.toString()) != null;
    }

    public <E extends Serializable> void setList(Class<E> classType, String node, List<E> value) {
        config.put(node, value);
    }

    public <E extends Serializable> void setObject(Class<E> classType, String node, E object) {
        config.put(node, object);
    }

    public void addNode(Object key) {
        Map<String, Object> map = new LinkedHashMap<>();
        config.put(key.toString(), map);
    }

    public void removeNode(Object key) {
        config.remove(key.toString());
    }
}
