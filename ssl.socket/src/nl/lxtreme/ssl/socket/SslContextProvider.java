package nl.lxtreme.ssl.socket;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

public interface SslContextProvider {
	TrustManager[] getTrustManagers() throws GeneralSecurityException, IOException;

	KeyManager[] getKeyManagers() throws GeneralSecurityException, IOException;

	String getProtocol();
}