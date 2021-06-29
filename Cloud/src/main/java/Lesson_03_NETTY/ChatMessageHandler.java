package Lesson_03_NETTY;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatMessageHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0 (ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Message from client: " + msg);
        msg = msg.replaceAll("lol", "***");
        ctx.writeAndFlush(msg);
    }

}
