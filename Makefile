clean-build-run:
	mvn clean
	mkdir target &&	touch ./target/.gitkeep
	mvn install:install-file -Dfile=lib/api.jar -DgroupId=owlapi-api -DartifactId=owlapi -Dversion=1.4.3 -Dpackaging=jar
	mvn install:install-file -Dfile=lib/validation.jar -DgroupId=owlapi-validation -DartifactId=owlapi -Dversion=1.4.3 -Dpackaging=jar
	mvn install:install-file -Dfile=lib/io.jar -DgroupId=owlapi-io -DartifactId=owlapi -Dversion=1.4.3 -Dpackaging=jar
	mvn install:install-file -Dfile=lib/rdfapi.jar -DgroupId=owlapi-rdfapi -DartifactId=owlapi -Dversion=1.4.3 -Dpackaging=jar
	mvn install:install-file -Dfile=lib/rdfparser.jar -DgroupId=owlapi-rdfparser -DartifactId=owlapi -Dversion=1.4.3 -Dpackaging=jar
	mvn install:install-file -Dfile=lib/impl.jar -DgroupId=owlapi-impl -DartifactId=owlapi -Dversion=1.4.3 -Dpackaging=jar
	mvn install:install-file -Dfile=lib/antlr.jar -DgroupId=owlapi-antlr -DartifactId=owlapi -Dversion=1.4.3 -Dpackaging=jar
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
