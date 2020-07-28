#! /usr/bin/env python
import os
import os.path
import sys
import base64
import json

if __name__ == "__main__":
    # the configuration.json file is passed in as a first argument
    config_file = sys.argv[1]

    cert_dir = os.path.dirname("/etc/pki/tls/private/")

    # read the config file and load it into a python dictionary
    configuration = json.load(open(config_file))

    # set the environment
    environment = configuration.get("environment")

    # load certificate from  configuration, live keys have '.live' as suffix
    if environment == 'production':
        print 'Loading LIVE keys from configuration.'
        example_cert1 = configuration["configuration"]["example.ssl.cert1.live"]
        example_trust_store1 = configuration["configuration"]["example.ssl.trustStore1.live"]
    else:
        print 'Loading DEVELOPMENT keys from secure configuration.'
        example_cert1 = configuration["configuration"]["example.ssl.cert1"]
        example_trust_store1 = configuration["configuration"]["example.ssl.trustStore1"]

    if example_cert1 != None:
    # decode it back from base64 to normal
           decoded_cert = base64.b64decode(example_cert1)
    # open a new file.p12 in the create_cert_in_dir
          cert_file =  os.path.join(cert_dir, "exampleCert1.p12")
    # write it
         with open(cert_file, "a") as f:
             f.write(decoded_cert)
    # check if file exist
          file_path = cert_dir + "/exampleCert1.p12"
          print 'exampleCert1.p12 is writtn to ' + file_path + " and file exist ",  os.path.exists(file_path)
    else:
       print 'example.ssl.cert1 key does not exist or is empty.'


    if example_trust_store1 != None:
        # decode it back from base64 to normal
       decoded_trust = base64.b64decode(example_trust_store1)
        # open a new file.p12 in the create_cert_in_dir
       trust_file =  os.path.join(cert_dir, "example_trust_store1")
        # write it
       with open(trust_file, "a") as f:
           f.write(decoded_trust)
    # check if file exist
          file_path = cert_dir + "/example_trust_store1"
         print 'example_trust_store1 is writtn to ' + file_path + " and file exist ", os.path.exists(file_path)
     else:
         print 'example.ssl.example_trust_store1 key does not exist or is empty.'



