clean-build-run:
	mvn clean
	mvn compile
	mvn package
	mvn exec:java

clean:
	mvn clean

build:
	mvn compile
	mvn package

run:
	mvn exec:java
