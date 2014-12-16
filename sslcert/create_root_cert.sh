#!/bin/sh

rm -f certindex*
rm -f serial*
rm -f *.pem

if [ ! -d private ]; then
    mkdir private
else
    rm -f private/*
fi
if [ ! -d certs ]; then
    mkdir certs
else
    rm -f certs/*
fi

touch certindex.txt
echo "100001" >serial

openssl req -new -x509 -extensions v3_ca -keyout private/cakey.pem -out cacert.pem -days 365 -config ./openssl.cnf
