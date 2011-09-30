VA=$JAVA_HOME/bin/java
JAVA=java
JAVA_HEAP_MAX=-Xmx512m 
CLASSPATH=${CLASSPATH}:./
for f in ../lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

CLASS='org.zebra.search.crawler.ServiceApp'
exec "$JAVA" $JAVA_HEAP_MAX -classpath "$CLASSPATH" $CLASS

