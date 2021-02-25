package com.afei.simple.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;


public class SimpleClient {
    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();

        try {
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new DiscardClientHandler());
                        }
                    });

            ChannelFuture future = b.connect(host, port).sync();
            if(future.isSuccess()){
                System.out.println("连接服务器成功");
            }


            Scanner input = new Scanner(System.in);
            String sendMessage=null;
            do{
                System.out.println("请输入聊天内容：");
                sendMessage=input.nextLine();

                future.channel().writeAndFlush(getSendByteBuf(sendMessage));

            }while(sendMessage!=null&&!sendMessage.equals("quit"));

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }
    }

    /*
     * 将Sting转化为UTF-8编码的字节
     */
    public static ByteBuf getSendByteBuf(String message) throws UnsupportedEncodingException {
        byte[] req = message.getBytes("UTF-8");
        ByteBuf pingMessage = Unpooled.buffer();
        pingMessage.writeBytes(req);
        return pingMessage;
    }

}
