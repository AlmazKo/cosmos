package cos.json;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class JsArray extends AbstractList<Object> {
    static final JsArray      EMPTY = new JsArray(Collections.emptyList());
    final        List<Object> values;

    JsArray(List<Object> values) {
        this.values = values;
    }

    public JsArray() {
        this.values = new ArrayList<>();
    }

    public JsArray getArray(int pos) {
        return (JsArray) values.get(pos);
    }

    public JsObject getObject(int pos) {
        return (JsObject) values.get(pos);
    }

    public int getInt(int pos) {
        return ((Number) values.get(pos)).intValue();
    }

    public Boolean getBoolean(int pos) {
        return (Boolean) values.get(pos);
    }

    public String getString(int pos) {
        return (String) values.get(pos);
    }

    public Number getNumber(int pos) {
        return (Number) values.get(pos);
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
