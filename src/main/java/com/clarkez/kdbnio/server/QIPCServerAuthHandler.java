package com.clarkez.kdbnio.server;

public interface QIPCServerAuthHandler {
    boolean authenticate(String user, String password);
}
