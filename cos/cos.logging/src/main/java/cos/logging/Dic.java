package cos.logging;

public final class Dic {

    //todo: remove alloc
    private Object[] data = new Object[16];

    public Dic put(String key, Object value) {
        for (int i = 0; i < data.length; i += 2) {
            Object k = data[i];
            if (k == null) {
                data[i] = key;
                data[i + 1] = value;
                break;
            } else if (k.equals(key)) {
                data[i + 1] = value;
                break;
            }
        }

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



}
