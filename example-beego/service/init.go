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
	// 触发 load from json
	allCities, _ = ListAllCity()
	// 北京 上海 广州 深圳 信丰 长春 青岛 厦门
	defaultHotId := []int{1, 24, 75, 76, 1985, 210, 283, 59}
	for _, id := range defaultHotId {
		city := allCities[id]
		defaultHotCities = append(defaultHotCities, city)
		defaultHotCode = append(defaultHotCode, city.Code)
	}

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
		UpdateDetail.NextStartAt = nextExecuteTime
		return nextExecuteTime
	}, func(err error) bool {
		logs.Error("定时任务出现异常:%+v", err)
		return false
	})
}
