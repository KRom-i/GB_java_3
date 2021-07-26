package Lesson_03_NETTY;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TestHandler extends SimpleChannelInboundHandler<Integer> {

    @Override
    protected void channelRead0 (ChannelHandlerContext channelHandlerContext, Integer integer) throws Exception {
        System.out.println("Integer out: " + integer);
    }
}
