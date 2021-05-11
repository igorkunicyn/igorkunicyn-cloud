package client.service.impl;

import client.handler.ClientHandler;
import client.service.NetworkService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.*;


public class NettyNetworkService implements NetworkService {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8189;
    private static NettyNetworkService instance;
    private static SocketChannel channel;
    private final static int MAX_SIZE_OBJECT = 1024 * 1024 * 1024;

    public static NettyNetworkService getInstance() {
        if (instance == null) {
            instance = new NettyNetworkService();
            initializeSocket();
        }
        return instance;
    }

    private static void initializeSocket() {
        Thread t = new Thread(() -> {
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                channel = socketChannel;
                                socketChannel.pipeline().addLast(new ObjectDecoder(MAX_SIZE_OBJECT,
                                        ClassResolvers.cacheDisabled(null)), new ObjectEncoder(), new ClientHandler());
                            }
                        });
                ChannelFuture future = b.connect(SERVER_HOST, SERVER_PORT).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        t.start();
    }

    @Override
    public void closeConnection() {
        channel.close();
    }

    @Override
    public void sendCommand(Object o) {
        channel.writeAndFlush(o);
    }

}


