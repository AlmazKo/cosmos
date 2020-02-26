package cos.map;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

public final class JsArray extends AbstractList<Object> implements Json.JsNode {
    final         ArrayList<Object> values = new ArrayList<>();
    private final Json.JsNode       parent;

    JsArray(Json.JsNode parent) {
        this.parent = parent;
    }

    public JsArray getArray(int pos) {
        return (JsArray) values.get(pos);
    }

    public JsArray getObject(int pos) {
        return (JsArray) values.get(pos);
    }

    public Integer getInt(int pos) {
        return (Integer) values.get(pos);
    }

    public Boolean getBoolean(int pos) {
        return (Boolean) values.get(pos);
    }

    public Json.JsNode parent() {
        return parent;
    }

    @Override public Object get(int index) {
        return values.get(index);
    }

    @Override public Iterator<Object> iterator() {
        return values.iterator();
    }

    @Override public int size() {
        return values.size();
    }
}
