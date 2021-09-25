package fx.nio.codecs;

public interface CodecInput<T> {
    void onData(T object);
}
