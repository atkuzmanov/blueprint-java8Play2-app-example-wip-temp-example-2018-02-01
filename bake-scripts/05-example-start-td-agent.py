#! /usr/bin/env python
import sys
import json
import subprocess

# the configuration.json file is passed in
# as a first argument to all bake-scripts
config_file = sys.argv[1]

# readNumberOfSQSMsgs the config file and load it into
# into a python dictionary
config = json.load(open(config_file))

# extracting just the secure configuration part
environment = config.get("environment")

print "environment is " + environment

if environment == "live":
    print "Attempting to install td-agent"
    subprocess.call(["/bin/chmod", "a+x", "/etc/init.d/td-agent"])
    subprocess.call(["chkconfig", "--add", "td-agent"])
    subprocess.call(["chkconfig", "td-agent", "on"])
