#!/usr/bin/env bash
rm -rf output
mkdir output
cp -r lang output/
cp -r templates output/
cp script/* output/

go build -o output/web-demo
