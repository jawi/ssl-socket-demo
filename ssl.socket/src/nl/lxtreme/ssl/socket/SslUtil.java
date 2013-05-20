package nl.lxtreme.ssl.socket;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SslUtil {
    private static final String JKS = "JKS";

    public static KeyManager[] createKeyManagers(String keystore, char[] password) throws GeneralSecurityException, IOException {
        return createKeyManagers(keystore, password, password);
    }

    public static KeyManager[] createKeyManagers(String keystore, char[] storePassword, char[] keyPassword) throws GeneralSecurityException, IOException {
        String algorithm = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);

        KeyStore ks = KeyStore.getInstance(JKS);
        InputStream ksIs = new FileInputStream(keystore);
        try {
            ks.load(ksIs, storePassword);
        }
        finally {
            if (ksIs != null) {
                ksIs.close();
            }
        }
        kmf.init(ks, keyPassword);

        return kmf.getKeyManagers();
    }

    public static SSLContext createSSLContext(SslContextProvider provider) throws Exception {
        SSLContext context = SSLContext.getInstance(provider.getProtocol());
        context.init(provider.getKeyManagers(), provider.getTrustManagers(), new SecureRandom());
        return context;
    }

    public static SSLServerSocket createSSLServerSocket(int port, SslContextProvider provider) throws Exception {
        SSLContext context = createSSLContext(provider);
        SSLServerSocketFactory factory = context.getServerSocketFactory();
        SSLServerSocket socket = (SSLServerSocket) factory.createServerSocket(port);
        socket.setEnabledProtocols(new String[] { provider.getProtocol() });
        socket.setNeedClientAuth(false);
        return socket;
    }

    public static SSLSocket createSSLSocket(String host, int port, SslContextProvider provider) throws Exception {
        SSLContext context = createSSLContext(provider);
        SSLSocketFactory factory = context.getSocketFactory();
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        socket.setEnabledProtocols(new String[] { provider.getProtocol() });
        return socket;
    }

    public static TrustManager[] createTrustManagers(String keystore, char[] password) throws GeneralSecurityException, IOException {
        String algorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);

        KeyStore ks = KeyStore.getInstance(JKS);
        InputStream ksIs = new FileInputStream(keystore);
        try {
            ks.load(ksIs, password);
        }
        finally {
            if (ksIs != null) {
                ksIs.close();
            }
        }

        tmf.init(ks);

        return tmf.getTrustManagers();
    }

    public static String getPeerIdentity(Socket socket) {
        if (!(socket instanceof SSLSocket)) {
            return null;
        }

        SSLSession session = ((SSLSocket) socket).getSession();
        try {
            Principal principal = session.getPeerPrincipal();
            return getCommonName(principal);
        }
        catch (SSLPeerUnverifiedException e) {
            // Peer not verified, probably not using a certificate...
            return "unknown client";
        }
    }

    /**
     * Extract the name of the SSL server from the certificate.
     */
    public static String getServerName(X509Certificate certificate) {
        try {
            // compare to subjectAltNames if dnsName is present
            Collection<List<?>> subjAltNames = certificate.getSubjectAlternativeNames();
            if (subjAltNames != null) {
                for (Iterator<List<?>> itr = subjAltNames.iterator(); itr.hasNext();) {
                    List<?> next = itr.next();
                    if (((Integer) next.get(0)).intValue() == 2) {
                        return ((String) next.get(1));
                    }
                }
            }
        }
        catch (CertificateException e) {
            // Ignore...
        }

        // else check against common name in the subject field
        Principal subject = certificate.getSubjectX500Principal();

        return getCommonName(subject);
    }

    private static String getCommonName(Principal subject) {
        try {
            LdapName name = new LdapName(subject.getName());
            for (Rdn rdn : name.getRdns()) {
                if ("cn".equalsIgnoreCase(rdn.getType())) {
                    return (String) rdn.getValue();
                }
            }
        }
        catch (InvalidNameException e) {
            // Ignore...
        }
        return null;
    }
}
