PROJECT_NAME := kvectordb
JAR_FILE := target/$(PROJECT_NAME)-1.0-SNAPSHOT-jar-with-dependencies.jar

build:
	mvn clean package

run:
	java -jar $(JAR_FILE)

debug:
	mvnDebug clean package

site:
	mvn site
