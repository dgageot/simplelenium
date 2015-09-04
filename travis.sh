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

CHROME)
  echo "Testing CHROME"
  curl -Lo chrome.zip https://download-chromium.appspot.com/dl/Linux_x64 && unzip chrome.zip
	ls chrome-linux
	pwd
  startXvfb
  mvn -Dbrowser=chrome verify
  ;;

esac
