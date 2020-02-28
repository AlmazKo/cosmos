package cos.json;

import org.jetbrains.annotations.NotNull;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public interface Json2 {

    final class Parsing {
        final String cs;
        int i = 0;

        public Parsing(String cs) {
            this.cs = cs;
        }

        Object parseValue() {
            Object value;
            skipWhitespaces();
            switch ( cs.charAt(i)) {
                case '[' -> value = parseArray();
                case '{' -> value = parseObject();
                case '"' -> value = parseString();
                case 'n' -> {
                    i += 3;
                    value = null;
                }
                case 't' -> {
                    i += 3;
                    value = TRUE;
                }
                case 'f' -> {
                    i += 4;
                    value = FALSE;
                }
                default -> value = parseNumber();
            }

            return value;
        }

        private void skipWhitespaces() {
            while (i < cs.length()) {
                char c = cs.charAt(i);
                if (!isWhitespace(c)) return;
                i++;
            }
        }

        @NotNull String parseString() {
            int endIdx = cs.indexOf('"', i + 1);
            String value = cs.substring(i + 1, endIdx);
            i = endIdx;
            return value;
            //todo remove escaped
            // .replace("\\", "");
        }


        @NotNull Number parseNumber() {
            boolean isInt = true;
            int beginIdx = i;
            for (; ; i++) {
                var c = cs.charAt(i);
                if (c == 'e' || c == 'E' || c == '.') isInt = false;

                if (isWhitespace(c) || c == ',' || c == '}' || c == ']') {
                    try {
                        Number value;
                        if (isInt) {
                            value = Integer.parseInt(cs, beginIdx, i, 10);
                        } else {
                            value = Double.parseDouble(cs.substring(beginIdx, i));
                        }
                        i--;
                        return value;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Wrong JSON at position: " + beginIdx, e);
                    }
                }
            }
        }

        @NotNull JsObject parseObject() {
            JsObject obj = null;
            String key = null;
            for (i++; ; i++) {
                char c = cs.charAt(i);
                if (c == ',' || isWhitespace(c)) continue;

                if (c == '"') {
                    key = parseString();
                } else if (c == '}') {
                    return (obj == null) ? JsObject.EMPTY : obj;
                } else if (c == ':') {
                    if (obj == null) obj = new JsObject();
                    i++;
                    Object value = parseValue();
                    obj.values.put(key, value);
                }
            }
        }

        @NotNull JsArray parseArray() {
            JsArray array = null;
            for (i++; ; i++) {
                char c = cs.charAt(i);
                if (c == ',' || isWhitespace(c)) continue;

                if (c == ']') {
                    return (array == null) ? JsArray.EMPTY : array;
                } else {
                    if (array == null) array = new JsArray();
                    Object value = parseValue();
                    array.values.add(value);
                }
            }
        }
    }

    static Object parse(String s) {
        return new Parsing(s).parseValue();

    }

    Object NOTHING = new Object();

    static boolean isWhitespace(char c) {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }
}
