.DEFAULT_GOAL := build-run

setup:
	gradle wrapper --gradle-version 7.4

clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew clean installDist

run:
	./gradlew run

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew checkstyleMain checkstyleTest

gen-migrations:
	./gradlew generateMigrations

start:
	APP_ENV=dev ./gradlew run

start-dist:
	APP_ENV=prod ./build/install/app/bin/app

build-run: build run

check-updates:
	./gradlew dependencyUpdates

.PHONY: build
