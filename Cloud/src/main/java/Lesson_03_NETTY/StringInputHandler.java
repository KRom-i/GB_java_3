package Lesson_03_NETTY;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class StringInputHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead (ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = String.valueOf(msg);
        System.out.println("Message: " + message.replace("111", "222"));
    }
}
