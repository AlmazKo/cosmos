package cos.logging;

public final class Dic {

    //todo: remove alloc
    private final Object[] data = new Object[16];
    private int size = 0;

    public Dic add(String key, long value) {
        data[size] = key;
        data[size + 1] = value;
        size += 2;
        return this;
    }

    public Dic add(String key, int value) {
        data[size] = key;
        data[size + 1] = value;
        size += 2;
        return this;
    }

    public Dic add(String key, Object value) {

        data[size] = key;
        data[size + 1] = value.toString();
        size += 2;

        //todo: add dynamic alloc
        return this;
    }


    Object get(String key) {
        for (int i = 0; i < data.length; i += 2) {
            Object k = data[i];
            if (k.equals(key)) {
                return data[i + 1];
            }
        }

        return null;
    }

    public void clear() {
        size = 0;
    }

    public CharSequence toCharSequence() {
        if (size == 0) return "{}";

        var sb = new StringBuilder();
        sb.append('{');

        for (int i = 0; i < size; i += 2) {
            sb.append((String) data[i]);
            sb.append('=');
            sb.append(data[i + 1]);

            sb.append(',');
            sb.append(' ');
        }

        sb.append('}');
        return sb;
    }

    @Override
    public String toString() {
        return toCharSequence().toString();
    }
}
