package cos.map;

import java.util.ArrayList;
import java.util.TreeMap;

import static cos.map.Json.Phase.AFTER_KEY;
import static cos.map.Json.Phase.AFTER_KEY_DOTS;
import static cos.map.Json.Phase.AFTER_VALUE;
import static cos.map.Json.Phase.IN_ARRAY;
import static cos.map.Json.Phase.IN_KEY;
import static cos.map.Json.Phase.IN_NUMBER;
import static cos.map.Json.Phase.IN_OBJ;
import static cos.map.Json.Phase.IN_STR;

public interface Json {

//    Phase AFTER_KEY = ;

    final class Et {


        public Object getArray(String key) {

        }

        public Object getObject(String key) {

        }

        public int getInt(String key) {

        }

        public int getBoolean(String key) {

        }

        public boolean isNull() {

        }
    }

    enum Phase {
        IN_KEY, AFTER_KEY, AFTER_KEY_DOTS, IN_ARRAY, IN_OBJ,
        IN_STR, IN_NULL, IN_TRUE, IN_FALSE, IN_NUMBER, AFTER_VALUE
    }

    static void parse(String cs) {

        var tree = new TreeMap<String, Et>();

        Phase p = IN_OBJ;

        var depth = 0;
        int idx = 0;

        var currentEt = new Et();
        var keyBuf = new StringBuilder();
        var key = "";

        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);

            switch (p) {
                case IN_OBJ: {
                    if (c == '"') {
                        p = IN_KEY;
                    } else if (c == '}') {
                        //set parent context
                    }
                }
                case IN_KEY: {
                    if (c == '"') {
                        p = AFTER_KEY;
                        key = keyBuf.toString();
                    } else {
                        keyBuf.append(c);
                    }
                }
                case AFTER_KEY: {
                    if (c == ':') {
                        p = AFTER_KEY_DOTS;
                    }
                }
                case AFTER_KEY_DOTS: {
                    if (c == ' ') continue;

                    Object value = null;

                    if (c == '[') {
                        value = new ArrayList<>();
                        p = IN_ARRAY;
                    } else if (c == '{') {
                        value = new TreeMap<String, Et>();
                        p = IN_OBJ;
                    } else if (c == '"') {
                        p = IN_STR;
                    } else if (c == 'n') {
                        i += 3;
                        p = AFTER_VALUE;//set parent context
                    } else if (c == 't') {
                        value = true;
                        i += 3;
                        p = AFTER_VALUE; //set parent context
                    } else if (c == 'f') {
                        value = false;
                        i += 4;
                        p = AFTER_VALUE;//set parent context
                    } else {
                        p = IN_NUMBER;
                    }


                    tree.put(key, value);
                }
            }
        }
    }
}
