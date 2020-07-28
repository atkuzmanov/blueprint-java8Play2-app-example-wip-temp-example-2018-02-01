#!/usr/bin/env bash
###!/bin/sh
###
### default-example-application-name service
###
### chkconfig:   35 99 99
### description: default-example-application description

### Source function library.
. /etc/rc.d/init.d/functions

exec="java -jar -Dhttp.port=8080 /usr/lib/default-example-application-name/default-example-application-name.jar"
prog="default-example-application-name"
pidfile="/var/run/default-example-application-name/default-example-application-name.pid"
output_logfile=/var/log/default-example-application-name/output.log

[ -e /etc/sysconfig/$prog ] && . /etc/sysconfig/$prog

lockfile=/var/lock/subsys/$prog

start() {
    echo -n $"Initialising $prog: "
    daemon --pidfile=${pidfile} $exec
    retval=$?
    echo
    [ $retval -eq 0 ] && touch $lockfile
    return $retval
}

stop() {
    echo -n $"Shutting down $prog: "
    killproc -p $pidfile
    retval=$?
    echo
    [ $retval -eq 0 ] && rm -f $lockfile
    echo "Application shut down." >> $output_logfile
    return $retval
}

reload() {
    restart
}

restart() {
    stop
    start
}

rh_status_q() {
    rh_status >/dev/null 2>&1
}

force_reload() {
    restart
}

rh_status() {
    # Check to see if the service is running or use generic status instead.
    status -p $pidfile $prog
}

case "$1" in
    start)
        rh_status_q && exit 0
        $1
        ;;
    stop)
        rh_status_q || exit 0
        $1
        ;;
    restart)
        $1
        ;;
    reload)
        rh_status_q || exit 7
        $1
        ;;
    force-reload)
        force_reload
        ;;
    status)
        rh_status
        ;;
    conditionalrestart|try-restart)
        rh_status_q || exit 0
        restart
        ;;
    *)
        echo $"How to use: $0 {stop|start|restart|status|force-reload|try-restart|conditionalrestart|reload}"
        exit 2
esac
exit $?


