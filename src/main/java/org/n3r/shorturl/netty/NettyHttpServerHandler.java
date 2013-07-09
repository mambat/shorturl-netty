package org.n3r.shorturl.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Map;

public interface NettyHttpServerHandler {

    public void handle(ChannelHandlerContext ctx, HttpRequest request, String urlPattern, Map<String, String> parameters);

}
