package controllers

import (
	"fmt"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	"github.com/youthlin/examples/example-beego/models"
	"github.com/youthlin/examples/example-beego/service"
	"io/ioutil"
	"strings"
)

var icon = make(map[string]string)

func init() {
	files, e := ioutil.ReadDir("static/img/icon")
	if e != nil {
		logs.Error("ReadDir error: %=v", e)
	}
	for _, png := range files {
		name := png.Name()
		icon[strings.TrimSuffix(name, ".png")] = "/static/img/" + name
	}
	logs.Info("icon: %s", icon)

}

type MainController struct {
	beego.Controller
}

func (this *MainController) Prepare() {
	this.Layout = "layout.html"
}

// @router /
func (this *MainController) Home() {
	cities, err := service.ListCityByCount()
	if err == nil {
		this.Data["hot"] = cities
	}
	this.Data["defaultHot"] = service.DefaultHot
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
	this.Data["Provinces"] = service.GroupCities(&cityMap)
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
			var result = make(map[string]string)
			for _, v := range maps {
				result[v.Code] = v.Name
			}
			this.Data["json"] = models.MakeSuccess(result)
		}
	}
	this.ServeJSON()
}

// :path, :ext
// @router /weather/*.*
func (this *MainController) Weather() {
	code := this.Ctx.Input.Param(":path")
	weather, err := service.SearchWeather(code)
	if err != nil {
		this.toError(err)
		return
	}
	service.CityCountIncrement(code)
	logs.Info("code=%s weather=%v", code, weather)
	this.Data["weather"] = weather
	this.TplName = "weather.html"
}

// @router /about
func (this *MainController) About() {
	this.TplName = "about.html"
}

func (this *MainController) toError(err error) {
	logs.Error("Error Page: %+v", err)
	this.Data["e"] = err
	this.TplName = "error.html"
}
