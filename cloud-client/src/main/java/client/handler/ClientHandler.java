package client.handler;

import client.utils.AuthRegExitOfClient;
import client.utils.ProcessCommandFromServer;
import domain.FileUpload;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class ClientHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) {
        if (o.getClass() == String.class) {
            String resultCommand = ( String ) o;
            AuthRegExitOfClient.processCommandAuthClient(resultCommand);
        }
        if (o.getClass() == FileUpload.class) {
            FileUpload fileUpload = ( FileUpload ) o;
            ProcessCommandFromServer.updateWorkClientWindowOnServer(fileUpload);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

}
