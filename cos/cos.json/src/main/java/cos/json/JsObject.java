package cos.json;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JsObject {

    static final JsObject            EMPTY = new JsObject(Collections.emptyMap());
    final        Map<String, Object> values;

    JsObject(Map<String, Object> values) {
        this.values = values;
    }

    public JsObject() {
        this.values = new HashMap<>();
    }


    public JsArray getArray(String key) {
        return (JsArray) values.get(key);
    }

    public JsObject getObject(String key) {
        return (JsObject) values.get(key);
    }

    public Number getNumber(String key) {
        return (Number) values.get(key);
    }

    public @Nullable String getString(String key) {
        return (String) values.get(key);
    }

    public int getInt(String key) {
        return ((Number) values.get(key)).intValue();
    }

    public Boolean getBoolean(String key) {
        return (Boolean) values.get(key);
    }

}
