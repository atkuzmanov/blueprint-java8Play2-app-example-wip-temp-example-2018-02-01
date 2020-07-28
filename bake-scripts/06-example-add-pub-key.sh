#!/bin/sh

keytool -noprompt -import -file /usr/lib/[EXAMPLE-APPLICATION-NAME]/conf/keys/example-public-key1.pub -alias example-alias -keystore /etc/pki/examplejks.jks -storepass [EXAMPLE-PASSWORD]


