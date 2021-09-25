package fx.nio.codecs;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import almazko.microjson.JsObject;
import almazko.microjson.Json;
import org.jetbrains.annotations.NotNull;
import static java.lang.System.currentTimeMillis;
import static java.nio.charset.StandardCharsets.UTF_8;

import cos.logging.Logger;
import fx.nio.RawChannel;

public class JsonCodec implements Codec<HttpRequest<JsObject>, HttpResponse<JsObject, Object>> {

    private final static ByteBuffer PARSE_ERROR = ByteBuffer.wrap(
            """
                    HTTP/1.1 400 Bad Request
                    Content-Type: application/json; charset=utf-8
                    Connection: close

                    {"error": "Parse error"}"""
                    .stripIndent().getBytes(UTF_8)
    );

    private final static ByteBuffer INTERNAL_ERROR = ByteBuffer.wrap(
            """
                    HTTP/1.1 500 Internal error
                    Content-Type: application/json; charset=utf-8
                    Connection: close

                    {"error": "Internal error"}"""
                    .stripIndent().getBytes(UTF_8)
    );

    private final Logger logger = Logger.get(JsonCodec.class).atErrors();
    private final RawChannel ch;
    private CodecInput<HttpRequest<JsObject>> controller;

    public JsonCodec(RawChannel ch) {
        this.ch = ch;
    }

    @Override
    public void handler(@NotNull CodecInput<HttpRequest<JsObject>> consumer) {
        controller = consumer;
    }

    @Override
    public void close() {
        if (controller instanceof AutoCloseable) {
            try {
                ((AutoCloseable) controller).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void read(ByteBuffer in) {
        var httpBody = new String(in.array(), 0, in.position());
        onData(httpBody);
        in.flip();
    }

    //todo throw erron on " Transfer-Encoding: chunked
    private void onData(String data) {
        HttpRequest<JsObject> request;
        try {
            request = parseRequest(data);
        } catch (Exception e) {
            closeByError(PARSE_ERROR.duplicate(), "Wrong request",e);
            return;
        }

        logger.info("" + request);


        try {
            if ("OPTIONS".equals(request.method())) {
                ch.write(ByteBuffer.wrap("HTTP/1.1 204".getBytes(UTF_8)));
                if (!request.isKeepAlive()) {
                    ch.close();
                    return;
                }
            }

            controller.onData(request);

        } catch (Exception e) {
            write(new HttpResponse<>(request, null, e));
        }
    }

    @Override
    public void write(HttpResponse<JsObject, Object> response) {
        var request = response.request();
//        .thenApplyAsync(JSON::stringify)
        var sb = new StringBuilder();
        String content;
        if (response.error() != null) {
            if (response.error() instanceof HttpException) {
                sb.append("HTTP/1.1 ");
                sb.append(((HttpException) response.error()).code);
                sb.append('\n');
            } else {
                sb.append("HTTP/1.1 400 Bad Request\n");
            }
            content = "{\"error\": \"" + response.error().getMessage() + "\"}";
        } else {
            sb.append("HTTP/1.1 200 OK\n");
            content = response.result().toString();
        }
        if (request.isKeepAlive()) {
            sb.append("Connection: Keep-Alive\n");
        }
        if (request.uri().getPath().contains(".yaml")) {
            sb.append("Content-Type: text/yaml; charset=utf-8\n");
        } else {
            sb.append("Content-Type: application/json; charset=utf-8\n");
        }
        sb.append("Content-Length: ").append(content.getBytes().length).append('\n');
        sb.append('\n');
        sb.append(content);

        try {
            ch.write(ByteBuffer.wrap(sb.toString().getBytes(UTF_8)));
            if (!request.isKeepAlive()) {
                ch.close();
            }

        } catch (IOException e) {
            closeByError(INTERNAL_ERROR.duplicate(), "Internal error", e);
        }
    }

    private HttpRequest<JsObject> parseRequest(String data) {
        var idx = data.indexOf(' ');
        var method = data.substring(0, idx);
        var path = data.substring(idx + 1, data.indexOf(' ', idx + 1));

        var uri = URI.create(path);
        var dataIndex = data.indexOf("\r\n\r\n");
        var headers = parseHeaders(data, dataIndex);

        if (dataIndex == -1) {
            throw new InvalidRequestException("Wrong HTTP request");
        }
        var raw = data.substring(dataIndex + 4);
        var body = raw.isBlank() ? null : Json.parseObject(raw);
        return new HttpRequest<>(method, uri, headers, body, currentTimeMillis());
    }

    private Map<String, String> parseHeaders(String data, int dataIndex) {
        var headers = data.substring(0, dataIndex).split("\r\n");
        var result = new HashMap<String, String>(headers.length);
        for (int i = 1, headersLength = headers.length; i < headersLength; i++) {
            String row = headers[i];
            var parsed = row.split(": ", 2);
            result.put(parsed[0].toLowerCase(), parsed[1].trim().toLowerCase());
        }

        return result;
    }

    private void closeByError(ByteBuffer data, String comment, Throwable t) {
        logger.warn(comment, t);
        try {
            ch.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ch.close();
    }
}
