package routers

import (
	"github.com/astaxie/beego"
	"github.com/youthlin/examples/example-beego/controllers"
)

func init() {
	beego.Include(&controllers.MainController{})
}
