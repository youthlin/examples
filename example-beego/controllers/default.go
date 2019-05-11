package controllers

import (
	"fmt"
	"github.com/astaxie/beego"
	"github.com/youthlin/examples/example-beego/models"
	"github.com/youthlin/examples/example-beego/service"
)

type MainController struct {
	beego.Controller
}

func (this *MainController) Prepare() {
	this.Layout = "layout.html"
}

// @router /
func (this *MainController) Home() {
	this.TplName = "index.html"
}

// @router /cityList
func (this *MainController) CityList() {
	cityMap, e := service.ListAllCity()
	if e != nil {
		this.Data["e"] = e
		this.TplName = "error.html"
		return
	}
	this.Data["CityMap"] = cityMap
	this.TplName = "cityList.html"
}

// @router /citySearch
func (this *MainController) CitySearch() {
	query := this.GetString("q")
	if query == "" {
		this.Data["json"] = models.MakeFail(1, "No query words: parameter q required")
	} else {
		maps, e := service.SearchCity(query)
		if e != nil {
			this.Data["json"] = models.MakeFail(2, fmt.Sprintf("query db error.%+v", e))
		} else {
			this.Data["json"] = models.MakeSuccess(maps)
		}
	}
	this.ServeJSON()
}

// @router /weather/:id
func (this *MainController) Weather() {

	this.TplName = "weather.html"
}

// @router /about
func (this *MainController) About() {
	this.TplName = "about.html"
}
