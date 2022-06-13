package me.tsinyong;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Properties;

public class TcpServerStarter {
    private final static Properties properties = new Properties();

    public static void port() {
        properties.put("port", 9999);
    }

    public static void start() {
        EventLoopGroup master = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        new ServerBootstrap().group(master, worker).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast("decode", new StringDecoder());
                        socketChannel.pipeline().addLast("encode", new StringEncoder());
                        socketChannel.pipeline().addLast(new SimpleChannelInboundHandler() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                String data = (String) msg;
                                ctx.writeAndFlush("收到消息" + data);
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
                                channelHandlerContext.flush();
                                channelHandlerContext.channel().close();
                            }
                        });
                    }
                });
    }
}
