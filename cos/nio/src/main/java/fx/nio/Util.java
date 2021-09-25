package fx.nio;

import java.nio.channels.SelectionKey;

interface Util {

    static String toStr(SelectionKey key) {
        return String.valueOf(new char[]{
                key.isConnectable() ? 'C' : '-',
                key.isReadable() ? 'R' : '-',
                key.isWritable() ? 'W' : '-',
                key.isAcceptable() ? 'A' : '-'}
        );
    }
}
