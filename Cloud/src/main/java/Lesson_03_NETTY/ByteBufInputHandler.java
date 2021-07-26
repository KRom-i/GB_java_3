package Lesson_03_NETTY;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

public class ByteBufInputHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive (ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected: " + ctx.channel());
    }

    @Override
    public void channelInactive (ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected: " + ctx.channel());
    }

    @Override
    public void channelRead (ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;

        StringBuilder builder = new StringBuilder();
        while (buf.isReadable()){
            builder.append((char) buf.readByte());
        }

        System.out.println("builder: " + builder.toString());
        try {
             ctx.fireChannelRead(222);
        } catch (Exception e){
            ctx.fireChannelRead(builder.toString());
            e.printStackTrace();
        }


    }

    @Override
    public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
