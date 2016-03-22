clean-build-run:
	mvn clean
	mkdir target &&	touch ./target/.gitkeep

	mvn compile
	mvn package

	mvn exec:java

clean:
	mvn clean
	mkdir target &&	touch ./target/.gitkeep

build:
	mvn compile
	mvn package

run:
	mvn exec:java
