package me.tsinyong;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Properties;

public class TcpClientStarter {
    private final static Properties properties = new Properties();


    public static void start() {
        EventLoopGroup client = new NioEventLoopGroup();
        new Bootstrap()
                .group(client)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true).handler(new ChannelInitializer<NioSocketChannel>(){
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast("encode", new StringEncoder());
                        nioSocketChannel.pipeline().addLast("decode", new StringDecoder());
                        nioSocketChannel.pipeline().addLast(new SimpleChannelInboundHandler<>() {
                            @Override
                            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("收到");
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
                                System.out.println("收到");
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ctx.channel().close();
                            }
                        });
                    }
                });
    }
}
