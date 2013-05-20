# Two-way authenticated SSL communication

This demo project shows how to create and set up two-way authenticated SSL
communication over raw sockets using plain Java (and OpenSSL).

The idea for this is based on the following blog posting: 
http://thoughtcrime.org/blog/authenticity-is-broken-in-ssl-but-your-app-ha/
(option 1) which basically explains the following set up:

1. A private (self-signed) CA is used to create a 4096-bit signing certificate;
2. this signing certificate is used to create two signed certificates, one for
   the server, and one for the client;
3. both the client and server get/include a copy of the signing certificate to 
   verify the identify of its peer.

## Usage

Note that this project is intended for demo purposes, showing the abilities of
two-way authenticated SSL communication. As such, it should *not* be used in
production situations!

### Basic steps to set up the certificates:

1. `cd sslcert`;
2. run `./create_root_cert.sh` and answer the questions. For common name, use
   something like "Certificate Authority" or anything you like;
3. run `./export_root_cert_to_keytool.sh` to create the Java keystore with the
   signing certificate (which is the certificate trusted by both client and
   server);
4. run `./create_signing_request.sh server` to create a signing request for the
   server certificate, and answer all questions. For common name, use the FQDN
   of the server (which is not verified at runtime, but helps you keep the
   certificates apart);
5. run `./sign_request.sh server` to sign and create the actual certificate for
   the server;
6. run `./export_cert_key_to_keytool.sh server` to export the server
   certificate *and* its private key to a Java keystore;
7. repeat steps 4 through 6 for the client certificate (use `client` as name);
8. copy the keystores to their respective locations, by running
   `copy_keystores.sh`.

### Building the demo server and client

1. `cd ssl.socket`
2. run `ant clean build` to build the demo JAR. Note that you need Java7 to
   compile the code and create a JAR file in the `generated` directory.

### Steps to run the server

1. run `java -cp generated/ssl.socket.jar nl.lxtreme.ssl.socket.server.SslServer 9000`
   to start the server at port 9000 (replace 9000 with any other port if you
   like).

### Steps to run the client

1. run `java -cp generated/ssl.socket.jar nl.lxtreme.ssl.socket.client.SslClient localhost 9000`
   to start the client and let it communicate to the server running at
   localhost on port 9000 (again, change the hostname and port number to your
   likings).

The result will be a few lines that are written to the console(s) of both the
client and server, for example:

    Server started. Awaiting client...
    Client (client.localhost) connected. Awaiting ping...
    Ping received. Sending pong...
    Pong written. Ending server...

and

    Connected to server (server.localhost). Writing ping...
    Ping written, awaiting pong...
    Pong obtained! Ending client...

Both the client and server terminate after this.

## License

This code is licensed under Apache-2.0 License.

## Author

This code is written by Jan Willem Janssen, j.w.janssen@lxtreme.nl.


