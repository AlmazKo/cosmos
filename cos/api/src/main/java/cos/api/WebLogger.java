/*
 * Copyright (c) PrimeXbt
 */

package cos.api;


import cos.logging.Logger;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;


public class WebLogger implements Handler<RoutingContext> {
    private final Logger logger = Logger.get(WebLogger.class);

    private void log(RoutingContext ctx, long startLts, HttpMethod method, String uri) {
        var request = ctx.request();
        var length = request.response().bytesWritten();
        var status = request.response().getStatusCode();
        var execTime = System.currentTimeMillis() - startLts;
        var message = String.format("%s %s %d %db - %dms", method, uri, status, length, execTime);
        doLog(status, message);
    }

    private void doLog(int status, String message) {
        if (status >= 400) {
            logger.warn(message);
        } else {
            logger.debug(message);
        }
    }

    @Override
    public void handle(RoutingContext ctx) {
        var timestamp = System.currentTimeMillis();
        var method = ctx.request().method();
        var uri = ctx.request().uri();
        ctx.addBodyEndHandler(v -> log(ctx, timestamp, method, uri));
        ctx.next();
    }

}
