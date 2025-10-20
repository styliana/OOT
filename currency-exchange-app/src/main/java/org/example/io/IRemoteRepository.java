package org.example.io;

public interface IRemoteRepository {
    byte[] fetch(String url) throws Exception;
}
