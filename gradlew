#!/usr/bin/env sh
set -e
DIR="$(cd "$(dirname "$0")" && pwd)"
exec "${JAVA_HOME:-java}" -classpath "$DIR/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
