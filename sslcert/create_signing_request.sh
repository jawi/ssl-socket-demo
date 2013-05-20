#!/bin/sh

if [ -z $1 ]; then
    echo "Usage $0 <name>\n";
    exit 1
fi

openssl req -new -nodes -out $1req.pem -keyout private/$1key.pem -config ./openssl.cnf
