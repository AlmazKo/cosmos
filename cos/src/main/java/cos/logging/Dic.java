package cos.logging;

import org.jetbrains.annotations.Nullable;

import static cos.logging.Util.appendString;


public final class Dic {
    private final String[] data;
    private int size = 0;

    public Dic() {
        data = new String[32];
    }

    public Dic(int size) {
        data = new String[size * 2];
    }

    public void set(String key, String value) {
        if (key == null) return;

        for (int i = 0; i < size; i += 2) {
            if (data[i].equals(key)) {
                data[i + 1] = value;
                return;
            }
        }

        if (size >= data.length) {
            return;
        }

        data[size] = key;
        data[size + 1] = value;
        size += 2;
    }

    public @Nullable String get(String key) {
        for (int i = 0; i < size; i += 2) {
            if (data[i].equals(key)) {
                return data[i + 1];
            }
        }
        return null;
    }

    public void remove(String key) {
        for (int i = 0; i < size; i += 2) {
            if (data[i].equals(key)) {
                data[i + 1] = null;
                return;
            }
        }
    }

    public void clear() {
        for (int i = 0; i < size; i += 2) {
            data[i + 1] = null;
        }
    }

    public CharSequence toCharSequence() {
        if (size == 0) return "{}";

        var sb = new StringBuilder();
        sb.append('{');
        for (int i = 0; i < size; i += 2) {
            sb.append(data[i]);
            sb.append('=');
            sb.append(data[i + 1]);

            sb.append(',');
            sb.append(' ');
        }

        sb.append('}');
        return sb;
    }

    int append(byte[] buf, int index) {
        buf[index++] = '{';

        for (int i = 0; i < size; i += 2) {
            index = appendString(data[i], buf, index);
            buf[index++] = '=';
            index = appendString(data[i + 1], buf, index);
            buf[index++] = ',';
            buf[index++] = ' ';
        }

        buf[index - 2] = '}';
        buf[index - 1] = 0;
        return index - 1;
    }

    @Override
    public String toString() {
        return toCharSequence().toString();
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
