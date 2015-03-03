package net.haroeris.core.http;

import net.haroeris.core.logging.Marker;
import net.haroeris.core.settings.SettingEntry;
import net.haroeris.core.settings.SettingMgr;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by stw on 28.02.2015.
 */
@Service
@Scope(value = "singleton")
public class HttpConnectionService {
    private static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionService.class);
    // TODO Spring
    private PoolingHttpClientConnectionManager connMrg;
    @Autowired
    private SettingMgr settingMgr;

    private static TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
        }
    };

    {

        SSLContext sslContext = null;
        PlainConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        HttpClientContext clientContext = HttpClientContext.create();
        //Socket socket = plainsf.createSocket(clientContext);
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainsf)
                .register("https", sslsf)
                .build();

        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // The ConnectionManager Settings
        connMrg= new PoolingHttpClientConnectionManager(registry);
        // Increase max total connection to 200
        connMrg.setMaxTotal(200);
        // Increase default max connection per route to 20
        connMrg.setDefaultMaxPerRoute(20);
    }

    private String internalPost(
            String url, final List<NameValuePair> parameters
    ) throws IOException, ExecutionException, InterruptedException {
/*
        URL targetURL = new URL(url);
        HttpHost targetHost = new HttpHost( targetURL.getHost(), targetURL.getPort());

        HttpRoute route = new HttpRoute(targetHost);

        // Increase max connections for localhost:80 to 50
        //HttpHost localhost = new HttpHost("locahost", 80);
        connMrg.setMaxPerRoute(route, 50);

        // Request new connection. This can be a long process
        ConnectionRequest connRequest = connMrg.requestConnection(route, null);
        // Wait for connection up to 10 sec
        HttpClientConnection conn = connRequest.get(10, TimeUnit.SECONDS);
*/
        /*
        try {
            // If not open
            if (!conn.isOpen()) {
                HttpClientContext context = HttpClientContext.create();
                // establish connection based on its route info
                connMrg.connect(conn, route, 1000, context);
                // and mark it as route complete
                connMrg.routeComplete(conn, route, context);
            }
        */

        CloseableHttpClient httpclient;

        HttpHost proxy = null;
        String proxyHost = settingMgr.getConfig(SettingEntry.SYSTEM_PROXY_HOST);
        String proxyPort = settingMgr.getConfig(SettingEntry.SYSTEM_PROXY_PORT);
        if(StringUtils.isNotBlank(proxyHost)) {
            proxy = new HttpHost(proxyHost, Integer.valueOf(proxyPort));
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            httpclient = HttpClients.custom().setConnectionManager(connMrg).setRoutePlanner(routePlanner).build();
        } else {
            httpclient = HttpClients.custom().setConnectionManager(connMrg).build();
        }

        // Do useful things with the connection.

        try {
            HttpPost post = new HttpPost(url);

            if ((null != parameters) && (parameters.size() > 0)) {
                //System.out.println("PARAMS "+ parameters);
                post.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
            }
            post.addHeader("content-type", "application/x-www-form-urlencoded");

            String result = httpclient.execute(post, new BasicResponseHandler());
            LOGGER.info(Marker.BACKENDCALL, "Call to \"{}\" results = {}", post.getRequestLine(), result);
            return result;
        } finally {
            //httpclient.close();
        }
        /*} finally {
            connMrg.releaseConnection(conn, null, 1, TimeUnit.MINUTES);
        }
        */

    }

    public String post(String url, List<NameValuePair> nvps) {
        try {
            String result = internalPost(url, nvps);
            return result;
        } catch (IOException | ExecutionException | InterruptedException e) {
            LOGGER.error("[post]", e);
        }

        return null;
    }
}
