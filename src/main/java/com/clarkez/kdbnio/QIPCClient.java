package com.clarkez.kdbnio;

import com.clarkez.kdbnio.client.AbstractQIPCMessageListener;
import com.clarkez.kdbnio.client.QIPCIncomingListener;
import com.clarkez.kdbnio.client.QIPCMessageListener;
import com.clarkez.kdbnio.qpic.QIPCMessage;
import com.clarkez.kdbnio.server.QIPCServerAuthHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kx.QConst;

import com.clarkez.kdbnio.client.QIPCResponseListener;

/**
 * Discards any incoming data.
 */
public class QIPCClient extends AbstractQIPCService {

	private String host;
	private String userpass;
    private int port;

    public QIPCClient(String host,int port,String userpass) {
    	this.host = host;
        this.port = port;
        this.userpass = userpass;
    }

    public Channel connectClient(final QIPCIncomingListener listener) throws Exception {

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
        	Bootstrap  b = new Bootstrap ();
            b.group(workerGroup)
             .channel(NioSocketChannel.class) 
             .handler(new ChannelInitializer<SocketChannel>() { 
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                	ChannelPipeline p = ch.pipeline();
                    p.addLast("logger", new LoggingHandler(LogLevel.TRACE));
             		p.addLast("password", new QIPCPasswordHandlerForClient(userpass));
             		p.addLast("decoder", new QIPCDecoder());
             		p.addLast("encoder", new QIPCEncoder());
             		p.addLast("handler", new QIPCClientHandler(listener));
                 }
             })
             //.option(ChannelOption.SO_BACKLOG, 128)
             .option(ChannelOption.SO_KEEPALIVE, true)
             .option(ChannelOption.TCP_NODELAY, true);                          
                         
             //bootstrap.setOption("receiveBufferSize", 1048576);
           
            ChannelFuture f = b.connect(host, port).sync(); 
            f.await();
            Channel ch = f.channel();
            return ch;
        } finally {
            //workerGroup.shutdownGracefully();
        }
    }

}