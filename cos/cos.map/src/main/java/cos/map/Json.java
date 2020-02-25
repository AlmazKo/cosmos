package cos.map;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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

    final class EtArray extends AbstractCollection<Object> implements Et {
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

        @Override public Iterator<Object> iterator() {
            return values.iterator();
        }

        @Override public int size() {
            return values.size();
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

    static EtObject parse(String cs) {
        boolean expectValue = true;
        Et et = null;
        String key = null;

        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);
            if (isWhitespace(c)) continue;

            if (expectValue) {
                expectValue = false;

                final var eo = et;

                Object value = null;
                switch (c) {
                    case '[' -> {
                        et = new EtArray(eo);
                        expectValue = true;
                        value = et;
                    }
                    case '{' -> {
                        et = new EtObject(eo);
                        value = et;
                    }
                    case '"' -> {
                        int endI = cs.indexOf('"', i + 1) - 1;
                        value = cs.substring(i + 1, endI + 1);
                        //todo remove escaped
                        // .replace("\\", "");
                        i = endI + 1;
                    }
                    case 'n' -> i += 3; //null
                    case 't' -> {
                        i += 3;
                        value = TRUE;
                    }
                    case 'f' -> {
                        i += 4;
                        value = FALSE;
                    }
                    default -> {
                        boolean isInt = true;
                        for (int ii = i; ii < cs.length(); ii++) {
                            var ci = cs.charAt(ii);
                            if (ci == 'e' || ci == 'E' || ci == '.') isInt = false;

                            if (isWhitespace(ci) || ci == ',' || ci == '}' || ci == ']') {
                                if (isInt) {
                                    value = Integer.parseInt(cs, i, ii, 10);
                                } else {
                                    value = Double.parseDouble(cs.substring(i, ii));
                                }
                                i = ii - 1;
                                break;
                            }
                        }
                    }
                }

                if (eo != null) {
                    if (eo instanceof EtObject) {
                        ((EtObject) eo).values.put(key, value);
                        key = null;
                    } else {
                        ((EtArray) eo).values.add(value);
                    }
                }
            } else {

                if (et instanceof EtObject) {
                    if (c == '"') {
                        int endI = cs.indexOf('"', i + 1) - 1;
                        key = cs.substring(i + 1, endI + 1);
                        i = endI + 1;
                    } else if (c == '}') {
                        if (et.up() != null) et = et.up();
                    } else if (c == ':') {
                        expectValue = true;
                    }
                } else if (et instanceof EtArray) {
                    if (c == ',') {
                        expectValue = true;
                    } else if (c == ']') {
                        if (et.up() != null) et = et.up();
                    }

                }

            }
        }

        return null;
    }


//
//
//            switch (ctx) {
//                case OBJECT -> {
//                    if (c == '"') {
//                        int endI = cs.indexOf('"', i + 1) - 1;
//                        key = cs.substring(i + 1, endI + 1);
//                        i = endI + 1;
//                        p = AFTER_KEY;
//                    } else if (c == '}') {
//                        et = et.up();
//                    }
//                }
////                case AFTER_KEY -> {
////                    if (c == ':') {
////                        p = PRE_VALUE;
////                    }
////                }
//                case ARRAY -> {
//                    if (c == ']') {
//                        et = et.up();
//                    }
//                }
////                case AFTER_VALUE -> {
////                    if (c == ',') {
////                        if (et instanceof EtObject) {
////                            p = IN_OBJ;
////                        } else if (et instanceof EtArray) {
////                            p = IN_ARRAY;
////                        }
////                    }
////                }
//                case VALUE -> {
//                    if (isBlank(c)) continue;
//
//                    final var eo = et;
//                    Object value = null;
//
//                    if (c == '[') {
//                        et = new EtArray(eo);
//                        ctx = Ctx.ARRAY;
//                        value = et;
//                    } else if (c == '{') {
////                        p = IN_OBJ;
//                        ctx = Ctx.OBJECT;
//                        et = new EtObject(eo);
//                        value = et;
//                    } else if (c == '"') {
//                        int endI = cs.indexOf('"', i + 1) - 1;
//                        value = cs.substring(i + 1, endI + 1);
//                        i = endI + 1;
//                    } else if (c == 'n') {
//                        i += 3; //null
//                    } else if (c == 't') {
//                        value = TRUE;
//                        i += 3;
//                    } else if (c == 'f') {
//                        value = FALSE;
//                        i += 4;
//                    } else {
//                        boolean isInt = true;
//                        for (int ii = i; ii < cs.length(); ii++) {
//                            var ci = cs.charAt(ii);
//                            if (ci == 'e' || ci == '.') isInt = false;
//
//                            if (ci == ' ' || ci == ',' || ci == '}' || ci == ']') {
//                                if (isInt) {
//                                    value = Integer.parseInt(cs, i, ii, 10);
//                                } else {
//                                    value = Double.parseDouble(cs.substring(i, ii));
//                                }
//                                i = ii - 1;
//                                break;
//                            }
//                        }
//
//                    }
//
//                    if (et instanceof EtObject) {
//                        p = IN_OBJ;
//                    }/* else if (et instanceof EtArray) {
//                        p = IN_ARRAY;
//                    }
//*/
//                    if (eo != null && value != null) {
//                        if (eo instanceof EtObject) {
//                            ((EtObject) eo).values.put(key, value);
//                        } else if (eo instanceof EtArray) {
//                            ((EtArray) eo).values.add(value);
//                        }
//                    }
//                }
//            }
//        }
//
//        return root;
//}

    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }
}
