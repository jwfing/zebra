VA=$JAVA_HOME/bin/java
JAVA=java
JAVA_HEAP_MAX=-Xmx768m 
CLASSPATH=${CLASSPATH}:./crawler-1.0-SNAPSHOT.jar
for f in ./dependency/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

CLASS='org.zebra.search.crawler.ServiceApp'
exec "$JAVA" $JAVA_HEAP_MAX -classpath "$CLASSPATH" $CLASS

