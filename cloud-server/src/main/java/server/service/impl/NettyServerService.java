package server.service.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import server.handler.SQLHandler;
import server.handler.ServerNettyHandler;
import server.service.AuthDBService;
import server.service.ServerService;

import java.util.logging.Logger;

public class NettyServerService implements ServerService {

    private static final int SERVER_PORT = 8189;
    private AuthDBService authDBService;
    private static NettyServerService instance;
    private static final Logger logger = Logger.getLogger(NettyServerService.class.getName());
    private static final int MAX_SIZE_OBJECT = 1024 * 1024 * 1024;

    public static Logger getLogger() {
        return logger;
    }

    public static NettyServerService getInstance() {
        if (instance == null) {
            instance = new NettyServerService();
        }

        return instance;
    }

    @Override
    public void startServer() {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel socketChannel) {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new ObjectEncoder());
                    pipeline.addLast(new ObjectDecoder(MAX_SIZE_OBJECT, ClassResolvers.cacheDisabled(null)));
                    pipeline.addLast(new ServerNettyHandler());
                }
            });
            ChannelFuture future = b.bind(SERVER_PORT).sync();
            logger.info("Сервер запущен на порту " + SERVER_PORT);
            if (!SQLHandler.connect()) {
                throw new RuntimeException("Не удалось подключиться к БД");
            } else {
                logger.info("База данных подключена");
                authDBService = new AuthDBServise();
            }
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.info("Сервер упал");
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            logger.info("Сервер завершил работу");
            SQLHandler.disconnect();
        }
    }

    public AuthDBService getAuthService() {
        return authDBService;
    }

}
