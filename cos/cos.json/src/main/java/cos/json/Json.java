package cos.json;


import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public final class Json {
    private final String src;
    private       int    cursor;

    Json(String cs) {
        this.src = cs;
    }

    Object parseValue() {
        Object value;
        skipWhitespaces();

        switch (src.charAt(cursor)) {
            case '[' -> value = parseArray();
            case '{' -> value = parseObject();
            case '"' -> value = parseString();
            case 'n' -> {
                cursor += 3;
                value = null;
            }
            case 't' -> {
                cursor += 3;
                value = TRUE;
            }
            case 'f' -> {
                cursor += 4;
                value = FALSE;
            }
            default -> value = parseNumber();
        }

        return value;
    }

    private void skipWhitespaces() {
        while (isWhitespace(src.charAt(cursor))) {
            cursor++;
        }
    }

    @NotNull String parseString() {
        cursor++;
        int endIdx = src.indexOf('"', cursor);
        String value = src.substring(cursor, endIdx);
        cursor = endIdx;
        return value;

//            if (value.indexOf('\\') == -1) return value;
//
//            char[] result = new char[endIdx - cursor];
//            char c, n;
//            int i = 0;
//            for (; cursor < endIdx; cursor++) {
//                c = src.charAt(cursor);
//                if (c == '\\') {
//                    n = src.charAt(++cursor);
//                    switch (n) {
//                        case '\\', '/' -> result[i++] = n;
//                        case 'b' -> result[i++] = '\b';
//                        case 'f' -> result[i++] = '\f';
//                        case 'r' -> result[i++] = '\r';
//                        case 'n' -> result[i++] = '\n';
//                        case 't' -> result[i++] = '\t';
////                            case 'u' -> result[j++] = Character.;
//                    }
//                } else {
//                    result[i++] = c;
//                }
//            }
//
//            return new String(result, 0, i);
//

    }


    @NotNull Number parseNumber() throws NumberFormatException {
        boolean isInt = true;
        final int beginIdx = cursor;

        for (; ; cursor++) {
            var c = src.charAt(cursor);
            if (isInt && c == 'e' || c == 'E' || c == '.') isInt = false;
            if (isWhitespace(c) || c == ',' || c == '}' || c == ']') break;
        }

        if (isInt && cursor - beginIdx < 10) {
            return Integer.parseInt(src, beginIdx, cursor--, 10);
        } else if (isInt) {
            return Long.parseLong(src, beginIdx, cursor--, 10);
        } else {
            return Double.parseDouble(src.substring(beginIdx, cursor--));
        }
    }

    @NotNull JsObject parseObject() {
        JsObject obj = null;
        String key = null;

        for (++cursor; ; cursor++) {
            char c = src.charAt(cursor);
            if (c == '"') {
                key = parseString();
            } else if (c == '}') {
                return (obj == null) ? JsObject.EMPTY : obj;
            } else if (c == ':') {
                if (obj == null) obj = new JsObject();
                cursor++;
                obj.values.put(key, parseValue());
            }
        }
    }

    @NotNull JsArray parseArray() {
        JsArray array = null;
        for (cursor++; ; cursor++) {
            char c = src.charAt(cursor);
            if (c == ',' || isWhitespace(c)) continue;

            if (c == ']') {
                return (array == null) ? JsArray.EMPTY : array;
            } else {
                if (array == null) array = new JsArray();
                array.values.add(parseValue());
            }
        }
    }

    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }

    public static Object parse(String s) throws IllegalArgumentException {
        return new Json(s).parseValue();
    }

    public static JsObject parseObject(String s) throws IllegalArgumentException {
        return new Json(s).parseObject();
    }
}
