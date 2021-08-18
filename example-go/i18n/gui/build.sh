#!/usr/bin/env bash
go mod tidy
gofmt -w .

# 打包
go get fyne.io/fyne/v2/cmd/fyne
fyne package -name gui-demo -release -appID com.youthlin.go.i18ndemo.gui
# 把翻译文件放进
cp -r lang gui-demo.app/Contents/MacOS/

# 移动到 output
rm -rf output
mkdir output
mv gui-demo.app output/
