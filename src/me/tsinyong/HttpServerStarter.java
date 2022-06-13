package me.tsinyong;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.util.Properties;
import java.util.ResourceBundle;

public class HttpServerStarter {
    private final static Properties PROPERTIES = new Properties();

    public static void start() {
        EventLoopGroup master = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            new ServerBootstrap().group(master, worker).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    ChannelPipeline channelPipeline = socketChannel.pipeline();
                    channelPipeline.addLast("httpServerCodec", new HttpServerCodec());
                    channelPipeline.addLast("httpAggregator", new HttpObjectAggregator(512 * 1024));
                    channelPipeline.addLast("httpServerHandler", new SimpleChannelInboundHandler<FullHttpRequest>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) throws Exception {
                            Channel channel = channelHandlerContext.channel();
                            URI uri = new URI(request.uri());
                            System.out.println(uri.getPath());
                            System.out.println(request.method().name());
                            System.out.println(request.content().toString());
                            ByteBuf content = Unpooled.copiedBuffer("收到请求", CharsetUtil.UTF_8);
                            FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_0,HttpResponseStatus.OK,content);
                            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
                            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
                            channelHandlerContext.writeAndFlush(response);
                            channelHandlerContext.channel().close();
                        }
                    });
                }
            }).bind((Integer) PROPERTIES.getOrDefault("port", 9090)).channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void port() {
        PROPERTIES.put("port", 9090);
    }
}
