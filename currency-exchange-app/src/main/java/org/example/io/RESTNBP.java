package org.example.io;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RESTNBP implements IRemoteRepository {
    @Override
    public byte[] fetch(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(10_000);
        conn.setRequestMethod("GET");
        conn.setInstanceFollowRedirects(true);

        int code = conn.getResponseCode();
        if (code != 200) throw new Exception("HTTP code: " + code);

        try (InputStream in = conn.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer)) != -1) baos.write(buffer, 0, n);
            return baos.toByteArray();
        }
    }
}
