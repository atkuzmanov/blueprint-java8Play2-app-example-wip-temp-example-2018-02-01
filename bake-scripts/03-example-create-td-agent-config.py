#!/usr/bin/env python

import json
import sys

# Setup the TD-AGENT CONFIG
TD_AGENT_CONFIG_FILE = "/etc/bake-scripts/td-agent.conf"
TD_AGENT_CONFIG = """
<source>
  type tail
  path /var/log/[APPLICATION-NAME]/application.log
  refresh_interval 1
  read_from_head true
  pos_file /var/log/td-agent/tmp/[APPLICATION-NAME]_application_log.pos
  tag [APPLICATION-NAME]_application_log
  format none
</source>

<source>
  type tail
  path /var/log/[APPLICATION-NAME]/output.log
  refresh_interval 1
  read_from_head true
  pos_file /var/log/td-agent/tmp/[APPLICATION-NAME]_output_log.pos
  tag [APPLICATION-NAME]_output_log
  format none
</source>

<source>
  type tail
  path /var/log/[APPLICATION-NAME]/external-requests.log
  refresh_interval 1
  read_from_head true
  pos_file /var/log/td-agent/tmp/[APPLICATION-NAME]_external-requests_log.pos
  tag [APPLICATION-NAME]_external-requests_log
  format none
</source>

<source>
  type tail
  path /var/log/[APPLICATION-NAME]/status.log
  refresh_interval 1
  read_from_head true
  pos_file /var/log/td-agent/tmp/[APPLICATION-NAME]_status_log.pos
  tag [APPLICATION-NAME]_status_log
  format none
</source>

<source>
  type tail
  path /var/log/httpd/access_log
  refresh_interval 1
  read_from_head true
  pos_file /var/log/td-agent/tmp/access_log.pos
  tag http_access_log
  format none
</source>

<source>
  type tail
  path /var/log/httpd/error_log
  refresh_interval 1
  read_from_head true
  pos_file /var/log/td-agent/tmp/error_log.pos
  tag http_error_log
  format none
</source>

<source>
  type monitor_agent
  bind 127.0.0.1
  port 24225
</source>

<match http_access_log>
  log_level error
  type s3
  s3_bucket {0}
  path [APPLICATION-NAME]/{1}/http_access_log
  s3_region eu-west-1
  check_apikey_on_start true
  buffer_type memory
  time_slice_format %Y%m%d%H
  # use longer flush_interval to reduce CPU usage.
  # note that this is a trade-off against latency.
  flush_interval 10s
  buffer_chunk_limit 64m
  format single_value
  store_as text
  num_threads 1
</match>

<match http_error_log>
  log_level error
  type s3
  s3_bucket {0}
  path [APPLICATION-NAME]/{1}/http_error_log
  s3_region eu-west-1
  check_apikey_on_start true
  buffer_type memory
  time_slice_format %Y%m%d%H
  # use longer flush_interval to reduce CPU usage.
  # note that this is a trade-off against latency.
  flush_interval 10s
  buffer_chunk_limit 64m
  format single_value
  store_as text
  num_threads 1
</match>

<match [APPLICATION-NAME]_output_log>
  log_level error
  type s3
  s3_bucket {0}
  path [APPLICATION-NAME]/{1}/application_logs
  s3_region eu-west-1
  check_apikey_on_start true
  buffer_type memory
  time_slice_format %Y%m%d%H
  # use longer flush_interval to reduce CPU usage.
  # note that this is a trade-off against latency.
  flush_interval 10s
  buffer_chunk_limit 64m
  format single_value
  store_as text
  num_threads 1
</match>

<match [APPLICATION-NAME]_application_log>
  log_level error
  type s3
  s3_bucket {0}
  path [APPLICATION-NAME]/{1}/application_logs
  s3_region eu-west-1
  check_apikey_on_start true
  buffer_type memory
  time_slice_format %Y%m%d%H
  # use longer flush_interval to reduce CPU usage.
  # note that this is a trade-off against latency.
  flush_interval 10s
  buffer_chunk_limit 64m
  format single_value
  store_as text
  num_threads 1
</match>

<match [APPLICATION-NAME]_external-requests_log>
  log_level error
  type s3
  s3_bucket {0}
  path [APPLICATION-NAME]/{1}/application_logs
  s3_region eu-west-1
  check_apikey_on_start true
  buffer_type memory
  time_slice_format %Y%m%d%H
  # use longer flush_interval to reduce CPU usage.
  # note that this is a trade-off against latency.
  flush_interval 10s
  buffer_chunk_limit 64m
  format single_value
  store_as text
  num_threads 1
</match>

<match [APPLICATION-NAME]_status_log>
  log_level error
  type s3
  s3_bucket {0}
  path [APPLICATION-NAME]/{1}/application_logs
  s3_region eu-west-1
  check_apikey_on_start true
  buffer_type memory
  time_slice_format %Y%m%d%H
  # use longer flush_interval to reduce CPU usage.
  # note that this is a trade-off against latency.
  flush_interval 10s
  buffer_chunk_limit 64m
  format single_value
  store_as text
  num_threads 1
</match>
"""

def main(component_json_path):
    config = json.load(open(component_json_path))

    try:
        bucket = config["configuration"]["s3_bucket_name"]
        environment = config["environment"]
        print "bucket name: ", bucket
        print "environment: ", environment
    except KeyError:
        raise Exception("Key 'configuration/s3_bucket_name' and/or 'environment' are missing from the configuration.")

    with open(TD_AGENT_CONFIG_FILE, "a") as f:
        f.write(TD_AGENT_CONFIG.format(bucket, environment))

print "sys.arg", sys.argv[1]

if __name__ == "__main__":
    main(sys.argv[1])