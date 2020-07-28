#!/bin/sh

DEFAULT_EXAMPLE_RUNNING_PID=target/universal/stage/DEFAULT_EXAMPLE_RUNNING_PID
DEFAULT_CERTIFICATES_DIRECTORY=/etc/pki/tls/private

echo -e "\n ---[Starting checks before start...]--- \n"

echo -e ">. Ensureing PID file and associated processes are destroyed...."

### 1. Destroy the process.

if [ -f ${DEFAULT_EXAMPLE_RUNNING_PID} ]
then
        echo "+ >. Stopping DEFAULT_EXAMPLE_RUNNING_PID";
        kill `cat ${DEFAULT_EXAMPLE_RUNNING_PID}` && sleep 5;
fi

### 2. Check that the PID file is removed.

if [ -f ${DEFAULT_EXAMPLE_RUNNING_PID} ]
then
        echo "+ >. Deleting DEFAULT_EXAMPLE_RUNNING_PID file as it is still here...";
        rm ${DEFAULT_EXAMPLE_RUNNING_PID};
fi

### 3. Confirm the correct certificates are in the right place.

echo -e ">. Checking certs in place..."

ARE_CERTIFICATES_IN_CORRECT_PLACE=true

if [ ! -f "${DEFAULT_CERTIFICATES_DIRECTORY}/DefaultExampleCertificateName.p12" ]
then
        echo " >. WARN ${DEFAULT_CERTIFICATES_DIRECTORY}/DefaultExampleCertificateName.p12 is not present.";
        $ARE_CERTIFICATES_IN_CORRECT_PLACE = false;
fi

if [ "$ARE_CERTIFICATES_IN_CORRECT_PLACE" = false ]
then
    echo "  * Run python to create missing certificates using the relevant json config e.g. /etc/app-name/config-for-app-name.json.";
fi

echo -e "\n ---[Finished checks before starting.]--- "

