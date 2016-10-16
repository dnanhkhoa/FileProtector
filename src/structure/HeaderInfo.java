package structure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class HeaderInfo implements Serializable {

    private static final long         serialVersionUID = 1L;

    private final String              signature;
    private final Map<String, Object> map;

    public HeaderInfo(String signature) {
        this.signature = signature;
        this.map = new HashMap<>();
    }

    public String getSignature() {
        return signature;
    }

    public void add(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }
}
