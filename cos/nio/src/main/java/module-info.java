module fx.nio {
    requires java.base;
    requires java.net.http;
    requires org.jetbrains.annotations;
    requires microjson;
    requires cos.logging;

    exports fx.nio;
    exports fx.nio.codecs;
}
