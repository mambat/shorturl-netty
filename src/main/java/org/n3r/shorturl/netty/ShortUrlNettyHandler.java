package org.n3r.shorturl.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.util.Map;

import org.n3r.shorturl.base62.Base62Encoder;
import org.n3r.shorturl.redis.RedisClient;

public class ShortUrlNettyHandler implements NettyHttpServerHandler {
    public static final RedisClient redis = new RedisClient("132.35.81.197:6379");

    @Override
    public void handle(ChannelHandlerContext ctx, HttpRequest request, String urlPattern, Map<String, String> parameters) {
        String rspCnt = "/favicon.ico".equalsIgnoreCase(urlPattern) ? "" : "/generate".equalsIgnoreCase(urlPattern) ?
                generate(ctx, request, parameters) : query(ctx, request, parameters);

        FullHttpResponse response = createResponse(request, rspCnt);

        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        if (keepAlive) {
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        ctx.nextOutboundMessageBuffer().add(response);

        if (!keepAlive) {
            ctx.flush().addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * http://localhost:8080/query?short=1I
     * @param ctx
     * @param request
     * @param parameters
     * @return
     */
    private String query(ChannelHandlerContext ctx, HttpRequest request, Map<String, String> parameters) {
        String base62 = parameters.get("short");
        return redis.get(base62);
    }

    /**
     * http://localhost:8080/generate?source=www.yhd.com
     * @param ctx
     * @param request
     * @param parameters
     * @return
     */
    private String generate(ChannelHandlerContext ctx, HttpRequest request, Map<String, String> parameters) {
        Long sequence = redis.incr("sequence");
        String base62 = Base62Encoder.encode(sequence);
        redis.set(base62, parameters.get("source"));
        return base62;
    }

    private FullHttpResponse createResponse(HttpRequest request, String content) {
        FullHttpResponse response = new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK,
                Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        return response;
    }

}
