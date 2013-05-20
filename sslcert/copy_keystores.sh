#!/bin/sh

if [ ! -f cacert.jks ]; then 
    echo "No cacert.jks found!\n"
    exit 1;
fi
cp -f cacert.jks ..

if [ ! -f client.jks ]; then 
    echo "No client.jks found!\n"
    exit 1;
fi
cp -f client.jks ../ssl.socket

if [ ! -f server.jks ]; then 
    echo "No server.jks found!\n"
    exit 1;
fi
cp -f server.jks ../ssl.socket

