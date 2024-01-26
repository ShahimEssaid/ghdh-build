#!/usr/bin/env bash
#set -x
set -e
set -u
set -o pipefail
set -o noclobber
shopt -s nullglob

# stack overflow #59895
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do
  DIR="$(cd -P "$(dirname "$SOURCE")" && pwd)"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE"
done
DIR="$(cd -P "$(dirname "$SOURCE")" && pwd)"

cd "$DIR/.."
cd maven-deploy-runtime
bin/maven-deploy.sh

cd "$DIR/.."
cd maven-deploy-plugins
bin/maven-deploy.sh

cd "$DIR/.."
cd projects/maven-artifacts-example
bin/maven-deploy.sh

cd "$DIR/.."
cd projects/fat-jar
bin/fat-jar.sh
bin/example-1.sh




