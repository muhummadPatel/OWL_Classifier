##OWL Classifier

**Authors** :
* Aashiq Parker
* Brian Mc George
* Muhummad Patel

**Contact Us** :
Please feel free to direct any and all comments, queries, and/or suggestions regarding this project to muhummad.patel@gmail.com.

###Description:
OWL Classifier is a Java app that loads an ontology (as specified by the user) and provides a quick report of the features being used in that ontology. Furthermore, the OWL Classifier analyses the features of OWL that are being used in the loaded ontology and indicates which of the OWL sub-languages that ontology can be classified as. The reports also list the axioms that prevent the ontology from being classified as the other sub-languages. The OWL Classifier also reports on the expressivity of the OWL file and shows how each axiom added to the expressivity of the ontology and explains the derivation of the final expressivity class.

###Download:
Should you wish to use OWL Classifier, simply download the [latest release](https://github.com/muhummadPatel/OWL_Classifier/releases/latest) (as a .jar file) and run it. Should you wish to build OWL Classifier, see the sections below.

###Setting Up This Project:
OWL Classifier is a Java project built and managed using Maven. The build process has been encapsulated in a makefile for easy building and running (see next section for further details on using the makefile). The following is a quick guide to getting the OWL Classifier project up and running on your machine:
* Ensure that you have the following tools installed on your machine:
    * Java 8 ***(recommended, although Java 7 will suffice)***
    * Maven
    * Make
    * git ***(recommended)***
* Clone this repo to your machine using `git clone https://github.com/muhummadPatel/OWL_Classifier.git`. Alternatively, you could download a zipped copy of this repo (using the 'Download ZIP' button on this GitHub page) and unzip it
* Navigate to the newly created project folder (probably OWL_Classifier) in a terminal
* run `make` to clean, build, and run the project. Please note that a working internet connection is required when building the project for the first time as the dependencies will need to be downloaded

###Using the Makefile:
The makefile included in this project encapsulates the build process for this project. The following commands are supported by the OWL Classifier Makefile:
* `make` - runs the default target. This will clean, build, and run the project. (***very useful***)
* `make clean` - deletes all build artifacts from the project folder
* `make build` - compiles and packages the project
* `make run` - executes the project (project must have been built successfully for this command to work)
* `make deploy` - builds and packages an executable, standalone .jar file including all dependencies that will run OWL Classifier. The .jar file is located in the 'target' directory and can be run by simply double clicking it. This jar file can be freely distributed and run on any computer with an appropriate JVM/JRE
