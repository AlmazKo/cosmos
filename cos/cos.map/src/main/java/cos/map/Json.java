package cos.map;

import kotlinx.serialization.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public interface Json {

    interface JsNode {
        JsNode parent();
    }

    static Object parse(String string) {
        boolean expectValue = true;
        JsNode node = null;
        String key = null;
        Object value = NOTHING;
        int len = string.length();

        char[] data = string.toCharArray();

        for (int i = 0; i < len; i++) {
            char c = data[i];
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
                        i = endI;
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
                        i = endI;
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
        return cs.substring( endIdx, beginIdx);
        //todo remove escaped
        // .replace("\\", "");
    }


    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }
}
