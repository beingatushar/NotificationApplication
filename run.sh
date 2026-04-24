#!/bin/bash

echo "🧹 1. Cleaning up old Gradle cache to prevent 'No space left on device' errors..."
rm -rf ~/.gradle
export GRADLE_USER_HOME=/tmp/.gradle

echo "☕ 2. Setting Java 21 environment..."
export JAVA_HOME=/usr/lib/jvm/java-21-amazon-corretto.x86_64
export PATH=$JAVA_HOME/bin:$PATH

echo "🔄 3. Pulling latest code from GitHub..."
git fetch --all
git reset --hard origin/main

echo "🔐 4. Loading Environment Variables from .bashrc..."
source ~/.bashrc

echo "🚀 5. Building and Starting the Application..."
chmod +x gradlew
./gradlew clean bootRun