package server.handler;

import domain.FileUpload;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import server.factory.Factory;
import server.service.impl.NettyServerService;


public class ServerNettyHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg.getClass() == String.class) {
            String s = ( String ) msg;
            if (s.startsWith("/")) {
                ctx.writeAndFlush(Factory.getCommandDirectoryService().processCommand(s));
            } else {
                ctx.writeAndFlush(Factory.getCommandFileProcessable().commandProcessFile(s));
            }
        } else {
            FileUpload fileUpload = ( FileUpload ) msg;
            if (SQLHandler.addFiles(fileUpload.getPath().split("\\\\")[0],
                    fileUpload.getNameFile(), fileUpload.getPath(), fileUpload.getBytes())) {
                NettyServerService.getLogger().info("Файл добавлен в хранилище");
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.channel();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }
}


