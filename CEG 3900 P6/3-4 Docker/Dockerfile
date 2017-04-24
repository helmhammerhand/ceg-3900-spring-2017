FROM ubuntu:16.04 

RUN apt-get -y update && apt-get install -y gradle && apt-get install -y wget && apt-get install -y curl && apt-get install -y default-jdk && apt-get install -y git && echo "deb http://www.apache.org/dist/cassandra/debian 310x main" | tee -a /etc/apt/sources.list.d/cassandra.sources.list && curl https://www.apache.org/dist/cassandra/KEYS | apt-key add - && apt-get update && apt-get -y install cassandra
RUN git clone http://github.com/mcandre/painbow.git && cd painbow && gradle build && cd build/libs && wget http://central.maven.org/maven2/com/offbytwo/docopt/0.6.0.20150202/docopt-0.6.0.20150202.jar && wget http://central.maven.org/maven2/commons-codec/commons-codec/1.10/commons-codec-1.10.jar 
CMD cd painbow/build/libs; echo 'alias painbow="java -cp painbow.jar:commons-codec-1.10.jar:docopt-0.6.0.20150202.jar:/usr/share/cassandra/lib/* us.yellosoft.painbow.Painbow $1"' >> ~root/.bashrc; service cassandra start; /bin/bash
