#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p build/classes
find src test -name '*.java' > build/sources.txt
javac -encoding UTF-8 -d build/classes @build/sources.txt
java -cp build/classes pos.PosServiceTest
