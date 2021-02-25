package com.afei.simple.discard;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;

public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;// 获取客户端传来的Msg
        String recieved = getMessage(buf);
        System.out.println("客户端:"+recieved );
        if("你最近怎么样".equals(recieved)){
            ctx.writeAndFlush(getSendByteBuf("我挺好的啊"));
        }else if("吃饭了吗".equals(recieved)){
            ctx.writeAndFlush(getSendByteBuf("没吃呢"));
        }else if("你爱我吗".equals(recieved)){
            ctx.writeAndFlush(getSendByteBuf("我爱你"));
        } else if ("你好".equals(recieved)){
            ctx.writeAndFlush(getSendByteBuf("你好2"));
        } else {
            ctx.writeAndFlush(getSendByteBuf("听不懂你什么意思"));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /*
     * 将字节UTF-8编码返回字符串
     */
    private String getMessage(ByteBuf buf) {
        byte[] con = new byte[buf.readableBytes()];
        buf.readBytes(con);
        try {
            return new String(con, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * 将Sting转化为UTF-8编码的字节
     */
    private ByteBuf getSendByteBuf(String message) throws UnsupportedEncodingException {
        byte[] req = message.getBytes("UTF-8");
        ByteBuf pingMessage = Unpooled.buffer();
        pingMessage.writeBytes(req);
        return pingMessage;
    }
}
