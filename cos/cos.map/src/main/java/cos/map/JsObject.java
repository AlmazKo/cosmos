package cos.map;

import java.util.LinkedHashMap;
import java.util.Map;

public final class JsObject implements Json.JsNode {

//    static final JsObject        EMPTY = JsObject();
    private final Json.JsNode         parent;
    final         Map<String, Object> values = new LinkedHashMap<>();

    JsObject(Json.JsNode parent) {
        this.parent = parent;
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

    public String getString(String key) {
        return (String) values.get(key);
    }

    public Integer getInt(String key) {
        return (Integer) values.get(key);
    }

    public Boolean getBoolean(String key) {
        return (Boolean) values.get(key);
    }


    public Json.JsNode parent() {
        return parent;
    }
}
