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

func init() {
	db, err := gorm.Open("mysql", beego.AppConfig.String("data.source.url"))
	if err != nil {
		panic(err)
	}
	gDB = db
	if e := gDB.Set("gorm:table_options", "CHARSET=utf8mb4").AutoMigrate(models.CityInstance, models.WeatherInstance).Error; e != nil {
		logs.Emergency("建表失败 %+v", e)
	}
	// weatherCron()
}
func weatherCron() {
	util.StartTimer2(updateAllWeather, func(doneTime time.Time) time.Time {
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
		return time.Date(doneTime.Year(), doneTime.Month(), doneTime.Day()+dayPlus, nextHour, 0, 0, 0, doneTime.Location())
	}, func(err error) bool {
		logs.Error("定时任务出现异常:%+v", err)
		return false
	})
}
