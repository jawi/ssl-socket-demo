#!/bin/sh

if [ -z $1 ]; then
    echo "Usage $0 <name>\n";
    exit 1;
fi

if [ ! -f $1.pem ]; then
    echo "No such file: $1.pem!\n\nUsage $0 <name>\n";
    exit 2;
fi

keytool -importcert -file $1.pem -keystore $1.jks -alias $1
