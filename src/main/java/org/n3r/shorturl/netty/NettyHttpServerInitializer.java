package org.n3r.shorturl.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class NettyHttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private NettyHttpServerHandler handler;

    public NettyHttpServerInitializer(NettyHttpServerHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        p.addLast("decoder", new HttpRequestDecoder());

        p.addLast("encoder", new HttpResponseEncoder());

        p.addLast("handler", new NettyHttpServerHandlerAdapter(handler));
    }

}
