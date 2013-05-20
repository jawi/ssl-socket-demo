#!/bin/sh

if [ -z $1 ]; then
    echo "Usage $0 <name>\n";
    exit 1;
fi

if [ ! -f $1cert.pem ]; then
    echo "No such file: $1cert.pem!\n\nUsage $0 <name>\n";
    exit 2;
fi

openssl pkcs12 -export -in $1cert.pem -inkey private/$1key.pem > $1.p12

keytool -importkeystore -srckeystore $1.p12 -srcstoretype pkcs12 -destkeystore $1.jks

rm -f $1.p12
