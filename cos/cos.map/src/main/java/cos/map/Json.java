package cos.map;

import kotlinx.serialization.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public interface Json {

    interface JsNode {
        JsNode parent();
    }

    final class Parsing {

        String key     = null;
        Object current = NOTHING;
        final String cs;
        boolean expectValue = true;
        int     i           = 0;


        public Parsing(String cs) {
            this.cs = cs;
        }

        void next() {
            i++;
            char c = cs.charAt(i);

            if (expectValue) {
                Object value = parseValue(c);
                if (value instanceof JsObject) {
                    expectValue = false;
                }

                if (current instanceof JsObject) {
                    ((JsObject) current).values.put(key, value);

                    pp(value, c)

                } else if (current instanceof JsArray) {
                    ((JsArray) current).values.add(value);
                }
            }
        }

        Object parseValue(char c) {
            Object value = NOTHING;
            switch (c) {
                case '[' -> {
                    value = new JsArray(eo);
                }
                case '{' -> {
                    value = new JsObject(eo);
                }
                case '"' -> {
                    int endI = cs.indexOf('"', i + 1);
                    value = parseString(cs, endI, i + 1);
                    i = endI + 1;
                }
                case 'n' -> {
                    value = null;
                    i += 3;
                }
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
                                value = Double.parseDouble(parseString(cs, ii, i));
                            }
                            i = ii - 1;
                            break;
                        }
                    }
                }
            }

            return value;
        }

        void parse() {
            next();
        }


        void parseObject(JsonObject obj, char c) {


            if (c == '"') {
                int endI = cs.indexOf('"', i + 1);
                key = parseString(cs, endI, i + 1);
                i = endI + 1;

            } else if (c == '}') {
                return;
            } else if (c == ':') {
                parseValue(c);

            }

        }
    }

    static Object parse(String cs) {
        boolean expectValue = true;
        JsNode node = null;
        String key = null;
        Object value = NOTHING;

        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);
            if (isWhitespace(c)) continue;

            if (expectValue) {
                value = NOTHING;
                expectValue = false;
                final JsNode eo = node;

                switch (c) {
                    case '[' -> {
                        node = new JsArray(eo);
                        expectValue = true;
                        value = node;
                    }
                    case '{' -> {
                        node = new JsObject(eo);
                        value = node;
                    }
                    case '"' -> {
                        int endI = cs.indexOf('"', i + 1);
                        value = parseString(cs, endI, i + 1);
                        i = endI + 1;
                    }
                    case 'n' -> {
                        value = null;
                        i += 3;
                    }
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
                                    value = Double.parseDouble(parseString(cs, ii, i));
                                }
                                i = ii - 1;
                                break;
                            }
                        }
                    }
                }

                if (value == NOTHING) throw new IllegalArgumentException("Wrong JSON");

                if (eo != null) {
                    if (eo instanceof JsObject) {
                        ((JsObject) eo).values.put(key, value);
                        key = null;
                    } else {
                        ((JsArray) eo).values.add(value);
                    }
                }
            } else {

                if (node instanceof JsObject) {
                    if (c == '"') {
                        int endI = cs.indexOf('"', i + 1);
                        key = parseString(cs, endI, i + 1);
                        i = endI + 1;
                    } else if (c == '}') {
                        if (node.parent() != null) node = node.parent();
                    } else if (c == ':') {
                        expectValue = true;
                    }
                } else if (node instanceof JsArray) {
                    if (c == ',') {
                        expectValue = true;
                    } else if (c == ']') {
                        if (node.parent() != null) node = node.parent();
                    }

                }

            }
        }

        if (value == NOTHING) throw new IllegalArgumentException("Wrong JSON");

        return (node == null) ? value : node;
    }


    Object NOTHING = new Object();

    @NotNull static String parseString(String cs, int beginIdx, int endIdx) {
        return cs.substring(beginIdx, endIdx);
        //todo remove escaped
        // .replace("\\", "");
    }


    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }
}
