package fx.nio.codecs;

public interface CodecOutput<T> {
    void write(T object);
}
