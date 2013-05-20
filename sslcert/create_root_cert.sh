#!/bin/sh

rm -f certindex*
rm -f serial*
rm -f certs/*
rm -f private/*
rm -f *.pem

touch certindex.txt
echo "100001" >serial

openssl req -new -x509 -extensions v3_ca -keyout private/cakey.pem -out cacert.pem -days 365 -config ./openssl.cnf
