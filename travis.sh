#!/bin/bash

set -euo pipefail

function startXvfb() {
  export DISPLAY=:99.0
  /sbin/start-stop-daemon --start --quiet --pidfile /tmp/custom_xvfb_99.pid --make-pidfile --background --exec /usr/bin/Xvfb -- :99 -ac -screen 0 1280x1024x16
}

case "$BROWSER" in

PHANTOM_JS)
  echo "Testing PhantomJS"
  mvn verify
  ;;

FIREFOX)
  echo "Testing Firefox"
  startXvfb
  mvn -Dbrowser=firefox verify
  ;;

esac
