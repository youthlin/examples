package controllers

import (
	"github.com/astaxie/beego"
	"github.com/youthlin/examples/example-beego/models"
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
	this.TplName = "cityList.html"
}

// @router /citySearch
func (this *MainController) CitySearch() {
	this.Data["json"] = models.City{
		Code: "101240706",
		Name: "信丰",
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
