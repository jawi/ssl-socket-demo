#!/bin/sh

if [ -z $1 ]; then
    echo "Usage $0 <name>\n";
    exit 1;
fi

if [ ! -f $1req.pem ]; then
    echo "No such file: $1req.pem!\n\nUsage $0 <name>\n";
    exit 2;
fi

openssl ca -extensions v3_req -out $1cert.pem -config ./openssl.cnf -infiles $1req.pem
