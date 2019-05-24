package routers

import (
	"github.com/astaxie/beego"
	"github.com/youthlin/examples/example-beego/controllers"
)

func init() {
	beego.Router("/", &controllers.MainController{}, "*:Home")
	beego.Router("/cityList", &controllers.MainController{}, "get,post:CityList")
	beego.Router("/citySearch", &controllers.MainController{}, "get,post:CitySearch")
	beego.Router("/weather/*.*", &controllers.MainController{}, "get:Weather")
	beego.Router("/about", &controllers.MainController{}, "get:About")
}
