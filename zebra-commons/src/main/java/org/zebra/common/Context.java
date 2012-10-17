package org.zebra.common;

import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Context {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
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
