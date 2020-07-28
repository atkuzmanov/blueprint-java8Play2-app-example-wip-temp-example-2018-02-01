#!/bin/bash
### The JSON configuration filename is only readable at bake-time and it's passed to this script. So this will dump it in the dir below on the machine it runs.
cat $1 > /etc/[EXAMPLE-APPLICATION-NAME]/[EXAMPLE-APPLICATION-NAME]-config.json
