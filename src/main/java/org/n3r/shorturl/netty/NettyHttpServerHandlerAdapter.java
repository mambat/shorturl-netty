package org.n3r.shorturl.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NettyHttpServerHandlerAdapter extends ChannelInboundMessageHandlerAdapter<Object> {

    private HttpRequest request;

    private String urlPattern;

    private Map<String, String> parameters;

    NettyHttpServerHandler handler;

    public NettyHttpServerHandlerAdapter(NettyHttpServerHandler handler) {
        this.handler = handler;
        parameters = new HashMap<String, String>();
    }

    @Override
    public void endMessageReceived(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;

            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx, request);
            }

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());

            urlPattern = queryStringDecoder.path();

            Map<String, List<String>> params = queryStringDecoder.parameters();
            if (!params.isEmpty()) {
                for (Entry<String, List<String>> p: params.entrySet()) {
                    parameters.put(p.getKey(), p.getValue().get(0));
                }
            }
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                String[] params = content.toString(CharsetUtil.UTF_8).split("&");
                for (String param : params) {
                    String[] kv = param.split("=");
                    if (kv.length == 1) continue;
                    parameters.put(QueryStringDecoder.decodeComponent(kv[0], CharsetUtil.UTF_8),
                            QueryStringDecoder.decodeComponent(kv[1], CharsetUtil.UTF_8));
                }
            }

            if (msg instanceof LastHttpContent) {
                // LastHttpContent trailer = (LastHttpContent) msg;
                handler.handle(ctx, request, urlPattern, parameters);
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx, HttpRequest request) {
        HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.CONTINUE);
        ctx.nextOutboundMessageBuffer().add(response);
    }

}
