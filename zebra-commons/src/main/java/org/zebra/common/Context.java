package org.zebra.common;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Context {
    private static final Logger logger = Logger.getLogger(Context.class);
    private Map<String, Object> features = new HashMap<String, Object>();

    public void setVariable(String key, Object value) {
        this.features.put(key, value);
    }

    public Object getVariable(String key) {
        if (this.features.containsKey(key)) {
            return this.features.get(key);
        }
        return null;
    }
}
