package cos.map;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static cos.map.Json.Phase.AFTER_KEY;
import static cos.map.Json.Phase.IN_ARRAY;
import static cos.map.Json.Phase.IN_KEY;
import static cos.map.Json.Phase.IN_OBJ;
import static cos.map.Json.Phase.PRE_VALUE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public interface Json {

//    Phase AFTER_KEY = ;

    interface Et {
        Et up();
    }

//    final class EtBool implements Et {
//        private final Et parent;
//
//        EtBool(Et parent) {
//            this.parent = parent;
//        }
//
//        public EtBool(Et parent, boolean b) {
//        }
//
//
//        @Override public Et up() {
//            return parent;
//        }
//    }

//    final class EtString implements Et {
//        private final Et parent;
//
//        EtString(Et parent) {
//            this.parent = parent;
//        }
//
//        public EtString(Et parent, String b) {
//        }
//
//
//        @Override public Et up() {
//            return parent;
//        }
//    }
//
//    final class EtNull implements Et {
//        private final Et parent;
//
//        EtNull(Et parent) {
//            this.parent = parent;
//        }
//
//
//        @Override public Et up() {
//            return parent;
//        }
//    }

//    final class EtNumber implements Et {
//        private final Et parent;
//        private final Number number;
//
//        EtNumber(Et parent, Number number) {
//            this.parent = parent;
//            this.number = number;
//        }
//
//
//        @Override public Et up() {
//            return parent;
//        }
//    }

    final class EtArray implements Et {
        final         ArrayList<Object> values = new ArrayList<>();
        private final Et                parent;

        EtArray(Et parent) {
            this.parent = parent;
        }

        public EtArray getArray(int pos) {
            return (EtArray) values.get(pos);
        }

        public EtArray getObject(int pos) {
            return (EtArray) values.get(pos);
        }

        public Integer getInt(int pos) {
            return (Integer) values.get(pos);
        }

        public Boolean getBoolean(int pos) {
            return (Boolean) values.get(pos);
        }

        public Et up() {
            return parent;
        }
    }

    final class EtObject implements Et {

        private final Et parent;

        EtObject(Et parent) {

            this.parent = parent;
        }

        final Map<String, Object> values = new LinkedHashMap<>();

        public EtObject getArray(String key) {
            return (EtObject) values.get(key);
        }

        public EtArray getObject(String key) {
            return (EtArray) values.get(key);
        }

        public Integer getInt(String key) {
            return (Integer) values.get(key);
        }

        public Boolean getBoolean(String key) {
            return (Boolean) values.get(key);
        }


        public Et up() {
            return parent;
        }
    }

    enum Phase {
        NOPE, IN_KEY, AFTER_KEY, PRE_VALUE, IN_ARRAY, IN_OBJ,, AFTER_VALUE
    }

    static EtObject parse(String cs) {

        Phase p = IN_OBJ;

        EtObject root = new EtObject(null);
        Et et = root;
        var keyBuf = new StringBuilder();
        var key = "";

        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);

            switch (p) {
                case IN_OBJ: {
                    if (c == '"') {
                        p = IN_KEY;
                    } else if (c == '}') {
                        et = et.up();
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
                        p = PRE_VALUE;
                    }
                }
                case IN_ARRAY: {
                    if (c == ']') {
                        et = et.up();
                    }
                }
                case AFTER_VALUE: {
                    if (c == ',') {
                        if (et instanceof EtObject) {
                            p = IN_OBJ;
                        } else if (et instanceof EtArray) {
                            p = IN_ARRAY;
                        }
                    }
                }
                case PRE_VALUE: {
                    if (c == ' ') continue;

                    final var eo = et;

                    Object value = null;

                    if (c == '[') {
                        p = IN_ARRAY;
                        et = new EtArray(eo);
                        value = et;
                    } else if (c == '{') {
                        p = IN_OBJ;
                        et = new EtObject(eo);
                        value = et;
                    } else if (c == '"') {
                        int endI = cs.indexOf('"', i + 1);
                        value = cs.substring(i, endI);
                        i = endI + 1;
                    } else if (c == 'n') {
                        value = null;
                        i += 3;
                    } else if (c == 't') {
                        value = TRUE;
                        i += 3;
                    } else if (c == 'f') {
                        value = FALSE;
                        i += 4;
                    } else {
                        boolean isInt = true;
                        for (int ii = i; ii < cs.length(); ii++) {
                            var ci = cs.charAt(i);
                            if (ci == 'e' || ci == '.') isInt = false;

                            if (ci == ' ' || ci == ',' || ci == '}' || ci == ']') {
                                if (isInt) {
                                    value = Integer.parseInt(cs, i, ii - 1, 10);
                                } else {
                                    value = Double.parseDouble(cs.substring(i, ii - 1));
                                }
                            }
                        }

                    }

                    if (value != null) {
                        if (eo instanceof EtObject) {
                            ((EtObject) eo).values.put(key, value);
                        } else if (eo instanceof EtArray) {
                            ((EtArray) eo).values.add(value);
                        }
                    }
                }
            }
        }

        return root;
    }
}
