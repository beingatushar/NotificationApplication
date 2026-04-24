#!/bin/bash

echo "🧹 1. Cleaning up storage and killing Port 8080..."
rm -rf ~/.gradle
export GRADLE_USER_HOME=/tmp/.gradle

# This command kills any process sitting on port 8080
fuser -k 8080/tcp || true

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
