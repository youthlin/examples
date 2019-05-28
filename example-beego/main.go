package main

import (
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	_ "github.com/youthlin/examples/example-beego/routers"
)

func init() {
	// https://beego.me/docs/module/logs.md
	logs.SetLogger(logs.AdapterConsole, `{"level":1,"color":true}`)
	// 		"separate":["emergency", "alert", "critical", "error", "warning", "notice", "info", "debug"]
	config := `{
		"filename":"logs/weather.log",
		"perm":"0775",
		"separate":["error", "info", "debug"]
	}`
	logs.SetLogger(logs.AdapterMultiFile, config)
	logs.EnableFuncCallDepth(true)
	logs.Async()

}
func main() {
	beego.Run()
}
