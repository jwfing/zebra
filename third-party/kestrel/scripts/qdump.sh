#!/bin/sh
DIST_HOME="$(dirname $0)/.."
java -server -classpath ${DIST_HOME}/libs/mina-core-2.0.0-M6.jar:${DIST_HOME}/libs/configgy-1.6.4.jar:${DIST_HOME}/libs/xrayspecs-1.0.7.jar:${DIST_HOME}/libs/vscaladoc-1.1-md-3.jar:${DIST_HOME}/libs/json-1.1.3.jar:${DIST_HOME}/libs/specs-1.6.2.1.jar:${DIST_HOME}/libs/naggati_2.7.7-0.7.5.jar:${DIST_HOME}/libs/slf4j-api-1.5.2.jar:${DIST_HOME}/libs/scala-library.jar:${DIST_HOME}/libs/twitteractors-1.1.0.jar:${DIST_HOME}/libs/slf4j-jdk14-1.5.2.jar:${DIST_HOME}/libs/twitteractors_2.7.7-2.0.1.jar:${DIST_HOME}/kestrel-1.3.0.jar scala.tools.nsc.MainGenericRunner net.lag.kestrel.tools.QDumper "$@"
