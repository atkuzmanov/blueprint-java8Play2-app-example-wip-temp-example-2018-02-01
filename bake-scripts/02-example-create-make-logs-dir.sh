#!/bin/sh
# Make the /var/log directory executable
chmod -R a+X /var/log
mkdir /var/log/[NAME-OF-APP]
mkdir a+w /var/log/[NAME-OF-APP]
mkdir -p /var/run/[NAME-OF-APP]
mkdir -p /var/run/[NAME-OF-APP]/