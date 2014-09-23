#!/bin/sh
JAVA_HOME=/usr/jdk/instances/jdk1.6.0_11
WORK_DIR=/BQS/ccs/service
LIBRARY_DIR=/BQS/ccs/lib
CLASSPATH=CCS_20120503.jar:$CLASSPATH
APP_PATH=com.crm.thread.ServiceManager
CLASSPATH=$LIBRARY_DIR/common/c3p0.jar:$LIBRARY_DIR/common/commons-discovery.jar:$LIBRARY_DIR/common/commons-logging.jar:$LIBRARY_DIR/common/commons-net-1.4.1.jar:$LIBRARY_DIR/common/commons-pool.jar:$LIBRARY_DIR/common/log4j.jar:$LIBRARY_DIR/common/ojdbc14.jar:$LIBRARY_DIR/fss/FSSServerLib.jar:$LIBRARY_DIR/fss/ThreadMonitor.jar:$LIBRARY_DIR/glassfish/$LIBRARY_DIR/gf-client.jar:$LIBRARY_DIR/jasper/iText-2.1.7.jar:$LIBRARY_DIR/jasper/jasperreports-4.0.0.jar:$LIBRARY_DIR/jasper/jasperreports-fonts-4.0.1.jar:$LIBRARY_DIR/telcos/axis.jar:$LIBRARY_DIR/telcos/ccws.jar:$LIBRARY_DIR/telcos/comverse-in.jar:$LIBRARY_DIR/telcos/eji.jar:$LIBRARY_DIR/telcos/jaxrpc.jar:$LIBRARY_DIR/telcos/wsdl4j-1.5.1.jar:$LIBRARY_DIR/telcos/wss4j-1.5.3.jar:$LIBRARY_DIR/telcos/xercesImpl.jar:$LIBRARY_DIR/telcos/xmlsec-1.4.1.jar:$LIBRARY_DIR/ccws-client.jar:$LIBRARY_DIR/commons-thread.jar:$LIBRARY_DIR/csvjdbc.jar:$LIBRARY_DIR/service.jarpid_file=service.pid:$CLASSPATH
pid_file=service.pid

RUNNING=0
if [ -f $pid_file ]; then
	pid=`cat $pid_file`
	if [ "x$pid" != "x" ] && kill -0 $pid 2>/dev/null; then
		RUNNING=1
	fi
fi

RESULT=0

if [ "$1" = "start" ] ; then
  cd $WORK_DIR
  if [ $RUNNING -eq 0 ]; then
    $JAVA_HOME/bin/java -cp $CLASSPATH -Xms128m -Xmx2048m $APP_PATH &
    echo $! > $pid_file
    sleep 1
    pid=`cat $pid_file`
    if [ "x$pid" != "x" ] && kill -0 $pid 2>/dev/null; then
      RESULT=0
    else
      RESULT=1
    fi
  fi
elif [ "$1" = "stop" ] ; then
  if [ $RUNNING -eq 1 ]; then
    kill -9 $pid
    sleep 1
    pid=`cat $pid_file`
    if [ "x$pid" != "x" ] && kill -0 $pid 2>/dev/null; then
      RESULT=1
    else
      RESULT=0
    fi
  fi
elif [ "$1" = "check" ] ; then
  if [ $RUNNING -eq 0 ]; then
    RESULT=1
  fi
else
  echo "Usage: $0 {  start | stop | check }"
  RESULT=1
fi

exit $RESULT

