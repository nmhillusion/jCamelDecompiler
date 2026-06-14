#!/usr/bin/env bash

export m_java="$java25_home"

"$m_java/bin/java" -version

JAVA_HOME="$m_java" ./gradlew --info clean distZip