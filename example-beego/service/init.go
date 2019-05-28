package service

import (
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	"github.com/jinzhu/gorm"
	_ "github.com/jinzhu/gorm/dialects/mysql"
	"github.com/youthlin/examples/example-beego/models"
	"github.com/youthlin/examples/example-beego/util"
	"time"
)

var gDB *gorm.DB
var DefaultHot = make(map[string]string)
var defaultHotCode []string

func init() {
	db, err := gorm.Open("mysql", beego.AppConfig.String("data.source.url"))
	if err != nil {
		panic(err)
	}
	gDB = db
	if e := gDB.Set("gorm:table_options", "CHARSET=utf8mb4").AutoMigrate(models.CityInstance, models.WeatherInstance).Error; e != nil {
		logs.Emergency("建表失败 %+v", e)
	}
	DefaultHot["101010100"] = "北京"
	DefaultHot["101020100"] = "上海"
	DefaultHot["101280101"] = "广州"
	DefaultHot["101280601"] = "深圳"
	DefaultHot["101060101"] = "长春"
	DefaultHot["101230201"] = "厦门"
	DefaultHot["101240706"] = "信丰"
	DefaultHot["101120201"] = "青岛"
	for k, _ := range DefaultHot {
		defaultHotCode = append(defaultHotCode, k)
	}
	// 触发 load from json
	_, _ = ListAllCity()

	weatherCron()
}
func weatherCron() {
	util.StartTimer(updateAllWeather, func(doneTime time.Time) time.Time {
		hour := doneTime.Hour()
		// 4 9 14 20
		var nextHour int
		dayPlus := 0
		if hour < 4 {
			nextHour = 4
		} else if hour < 9 {
			nextHour = 9
		} else if hour < 14 {
			nextHour = 14
		} else if hour < 20 {
			nextHour = 20
		} else {
			dayPlus = 1
			nextHour = 4
		}
		nextExecuteTime := time.Date(doneTime.Year(), doneTime.Month(), doneTime.Day()+dayPlus, nextHour, 0, 0, 0, doneTime.Location())
		UpdateTaskNextStartedAt = nextExecuteTime
		return UpdateTaskNextStartedAt
	}, func(err error) bool {
		logs.Error("定时任务出现异常:%+v", err)
		return false
	})
}
