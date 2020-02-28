package cos.json;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public interface Json3 {
    class Node {
        final Node prev;
        Object value;

        public Node(Node prev, Object value) {
            this.prev = prev;
            this.value = value;
        }

    }

    static Object parse(String cs) {
        Node node = null;
        String key = null;
        Object value = null;

        boolean expectedValue = true;
        int len = cs.length();
        for (int i = 0; i < len; i++) {
            char c = cs.charAt(i);

            switch (c) {
                case ':', ' ', '\r', '\n', '\t' -> {
                    continue;
                }
                case ',' -> {
                    expectedValue = false; // for arrays = true
                    continue;
                }
                case '}' -> {
                    if (node != null) {
                        value = node.value;
                        node = node.prev;
                    }
                }
                case '{' -> {
                    expectedValue = false;
                    node = new Node(node, null);
                    continue;
                }
                case '"' -> {
                    int endIdx = cs.indexOf('"', i + 1);
                    String v = cs.substring(i + 1, endIdx);
                    i = endIdx;

                    //value or key
                    if (expectedValue) {
                        value = v;
                    } else {
                        key = v;
                        expectedValue = true;
                        continue;
                    }
                }
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
//                    default -> value = parseNumber();
                default -> value = null;
            }

            if (node != null) {
                if (key != null) {
                    if (node.value == null) {
                        node.value = new JsObject();
                    }
                    ((JsObject) node.value).values.put(key, value);
                    expectedValue = false;
                    key = null;
                }
            }
        }

        return value;


    }

    Object NOTHING = new Object();

    static boolean isWhitespace(char c) {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }
}
