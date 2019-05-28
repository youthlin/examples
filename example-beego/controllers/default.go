package controllers

import (
	"encoding/json"
	"fmt"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	"github.com/youthlin/examples/example-beego/models"
	"github.com/youthlin/examples/example-beego/service"
	"io/ioutil"
	"strings"
)

var icon = make(map[string]string)
var version string

func init() {
	files, e := ioutil.ReadDir("static/img/icon")
	if e != nil {
		logs.Error("Read icon dir error: %+v", e)
	}
	for _, png := range files {
		name := png.Name()
		icon[strings.TrimSuffix(name, ".png")] = "/static/img/" + name
	}
	version = beego.AppConfig.String("version")
}

type MainController struct {
	beego.Controller
}

func (this *MainController) setTitle(t string) {
	this.Data["Title"] = t
}
func (this *MainController) Prepare() {
	this.Layout = "layout.html"
	this.Data["v"] = version
}

func (this *MainController) Render() (e error) {
	title, _ := this.Data["Title"].(string)
	if title == "" {
		this.setTitle("灵天气")
	} else {
		this.setTitle(title + " - 灵天气")
	}
	// super method
	e = this.Controller.Render()
	return
}

// @router /
func (this *MainController) Home() {
	cities, err := service.ListCityByCount()
	if err == nil {
		this.Data["hot"] = cities
	}
	this.Data["defaultHot"] = service.DefaultHot
	this.TplName = "index.html"
	this.setTitle("首页")
}

// @router /cityList
func (this *MainController) CityList() {
	cityMap, e := service.ListAllCity()
	if e != nil {
		this.toError(e)
		return
	}
	this.Data["CityMap"] = cityMap
	this.Data["Provinces"] = service.GroupCities(&cityMap)
	this.TplName = "cityList.html"
	this.setTitle("城市列表")
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
	ext := this.Ctx.Input.Param(":ext")
	weather, err := service.SearchWeather(code, ext == "update")
	if err != nil {
		this.toError(err)
		return
	}
	service.CityCountIncrement(code)
	logs.Info("code=%s weather=%v", code, weather)
	this.Data["weather"] = weather
	this.TplName = "weather.html"
	this.setTitle(weather.CityInfo.Parent + "-" + weather.CityInfo.City + "天气预报")
}

// @router /about
func (this *MainController) About() {
	detail := this.GetString("detail")
	this.Data["ShowDetail"] = false
	if detail != "" {
		this.Data["ShowDetail"] = true
		bytes, _ := json.Marshal(service.UpdateDetail)
		this.Data["UpdateDetail"] = string(bytes)
	}
	this.TplName = "about.html"
	this.setTitle("关于")
}

func (this *MainController) toError(err error) {
	logs.Error("Error Page: %+v", err)
	this.Data["e"] = err
	this.TplName = "error.html"
	this.setTitle("出错了")
}
