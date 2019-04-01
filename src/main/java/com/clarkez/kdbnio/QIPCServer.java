package com.clarkez.kdbnio;

import java.net.InetAddress;

import com.clarkez.kdbnio.client.QIPCIncomingListener;
import com.clarkez.kdbnio.server.QIPCServerAuthHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;

/**
 * Discards any incoming data.
 */
public class QIPCServer extends AbstractQIPCService{

    private int port;
    private QIPCServerAuthHandler authHandler;
    private QIPCIncomingListener listener;
    private ChannelFuture f;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public QIPCServer(int port, QIPCIncomingListener listener,QIPCServerAuthHandler authHandler) {
        this.port = port;
        this.authHandler = authHandler;
        this.listener = listener;
        f = null;

    }

    public void runServer( ) throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("password",new QIPCPasswordHandler(authHandler));
                            ch.pipeline().addLast("decoder",new QIPCDecoder());
                            ch.pipeline().addLast("encoder",new QIPCEncoder());
                            ch.pipeline().addLast("handler",new QIPCClientHandler(listener));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            InetAddress all = InetAddress.getByAddress(new byte[] {0,0,0,0});
            f = b.bind(all,port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
//            f.channel().closeFuture().sync();

        } catch(Exception e) {
            log.error("Error initialising server",e);
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void shutDownServer( ) throws Exception {
        log.error("Shutting Down Server");
        Future<?> wf = workerGroup.shutdownGracefully();
        Future<?> bf= bossGroup.shutdownGracefully();
        wf.sync();
        bf.sync();
    }



    }