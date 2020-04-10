 mvn compile exec:java    \
   -Dexec.mainClass=com.test.bq.pubsubtobq2 \
   -Dexec.args="--runner=DataflowRunner"
