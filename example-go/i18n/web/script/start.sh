#!/usr/bin/env bash
set -ex
pid=$$
echo "My PID=${pid}"
PREDIR=$(pwd)
cd $(dirname $0)
exec ./web-demo
