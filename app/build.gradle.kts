plugins {
    java
    id("com.diffplug.spotless") version "7.0.2"
}

group = "hexlet.code"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    java {
        // don't need to set target, it is inferred from java

        // apply a specific flavor of google-java-format
        // googleJavaFormat('1.8').aosp().reflowLongStrings().skipJavadocFormatting()
        // fix formatting of type annotations
        importOrder()
        googleJavaFormat().aosp()
        formatAnnotations()
        removeUnusedImports()
        leadingTabsToSpaces(4)
        endWithNewline()
        // make sure every file has the following copyright header.
        // optionally, Spotless can set copyright years by digging
        // through git history (see "license" section below)
        // licenseHeader '/* (C)$YEAR */'
    }
}
