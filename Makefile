ifeq ($(OS),Windows_NT)
GRADLE := gradlew.bat
else
GRADLE := ./gradlew
endif

.PHONY: test lint

test:
	cd app && $(GRADLE) test

lint:
	cd app && $(GRADLE) spotlessCheck compileJava compileTestJava
