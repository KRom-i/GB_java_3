package Lesson_03_NETTY.Server;

import Lesson_03_NETTY.ByteBufInputHandler;
import Lesson_03_NETTY.ChatMessageHandler;
import Lesson_03_NETTY.OutputHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.logging.FileHandler;

public class NettyServer {

    public NettyServer () {
        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel (Channel channel) throws Exception {
                            channel.pipeline().addLast(
                            new ByteBufInputHandler(),
                            new OutputHandler(),
                            new ChatMessageHandler()
                            );
                        }
                    });
            ChannelFuture future = bootstrap.bind(6666).sync();
            System.out.println("Server started");
            future.channel().closeFuture().sync();
            System.out.println("Server finished");
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main (String[] args) {
        new NettyServer();
    }
}
