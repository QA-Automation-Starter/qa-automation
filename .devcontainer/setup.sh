#!/usr/bin/env bash
set -e

sudo chown -R vscode:vscode /workspaces/qa-automation
sudo chown -R vscode:vscode /home/vscode/.m2
cd /workspaces/qa-automation && ./mvnw install -Pmode-build-nosign,mode-aspectj-skip
