package net.artux.pda.ios.net;

import com.android.org.conscrypt.OpenSSLSocketImpl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Turns on SNI for every TLS socket, which RoboVM's (Android 4-era) Conscrypt only sends once
 * setHostname() is called on the socket. Android's own OkHttp platform (AndroidPlatform) makes
 * that call, but OkHttp doesn't recognise RoboVM as Android and falls back to a platform that
 * never does - so the ClientHello went out without SNI, and artux.net's Cloudflare front end
 * rejects that with a handshake_failure alert, surfaced as "SSLProtocolException: SSL handshake
 * aborted ... Failure in SSL library" (reproduced in the simulator; `openssl s_client` without
 * -servername gets the same alert 40).
 */
public final class SniSSLSocketFactory extends SSLSocketFactory {

    private final SSLSocketFactory delegate;
    private final X509TrustManager trustManager;

    private SniSSLSocketFactory(SSLSocketFactory delegate, X509TrustManager trustManager) {
        this.delegate = delegate;
        this.trustManager = trustManager;
    }

    /** Wraps the platform's default TLS socket factory, using its default trust store. */
    public static SniSSLSocketFactory createDefault() {
        try {
            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init((KeyStore) null);
            X509TrustManager trustManager = null;
            for (TrustManager tm : factory.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    trustManager = (X509TrustManager) tm;
                    break;
                }
            }
            if (trustManager == null) {
                throw new IllegalStateException("No X509TrustManager in the default TrustManagerFactory");
            }
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{trustManager}, null);
            return new SniSSLSocketFactory(context.getSocketFactory(), trustManager);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Can't set up TLS", e);
        }
    }

    /** The trust manager behind this factory - OkHttp needs both together. */
    public X509TrustManager trustManager() {
        return trustManager;
    }

    private static Socket withSni(Socket socket, String host) {
        if (host != null && socket instanceof OpenSSLSocketImpl) {
            OpenSSLSocketImpl sslSocket = (OpenSSLSocketImpl) socket;
            sslSocket.setHostname(host);
            sslSocket.setUseSessionTickets(true);
        }
        return socket;
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return withSni(delegate.createSocket(socket, host, port, autoClose), host);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return withSni(delegate.createSocket(host, port), host);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        return withSni(delegate.createSocket(host, port, localHost, localPort), host);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return delegate.createSocket(host, port);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return delegate.createSocket(address, port, localAddress, localPort);
    }

    @Override
    public Socket createSocket() throws IOException {
        return delegate.createSocket();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }
}
