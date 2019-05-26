package service

import (
	"fmt"
	"github.com/astaxie/beego/logs"
	"github.com/jinzhu/gorm"
	"github.com/pkg/errors"
	"github.com/youthlin/examples/example-beego/models"
	"time"
)

func SearchWeather(code string, forceUpdate bool) (weather models.Weather, e error) {
	if forceUpdate {
		logs.Info("Force Update. code=%s", code)
		e = updateWeather(code)
		if e != nil {
			return
		}
	}
	e = searchWeather0(code, &weather)
	if e != nil {
		if e == gorm.ErrRecordNotFound && !forceUpdate {
			e = updateWeather(code)
			if e == nil {
				e = searchWeather0(code, &weather)
			}
		}
		e = errors.Wrap(e, "Search Weather Error")
	}
	return
}

func searchWeather0(code string, weather *models.Weather) error {
	now := time.Now()
	date := now.Format("20060102")
	return gDB.Where("date = ? AND city_code = ?", date, code).Order("updated_at").Limit(1).Find(weather).Error
}

func updateAllWeather() error {
	maps, e := ListAllCity()
	if e != nil {
		return e
	}
	for _, v := range maps {
		if v.Code != "" {
			e := updateWeather(v.Code)
			if e != nil {
				logs.Error("[update all]更新天气出现异常 跳过该城市 并休眠 5s 再继续. %v", v)
				time.Sleep(time.Second * 5)
				// return e
			}
			time.Sleep(time.Second * 2)
		}
	}
	return nil
}

func updateWeather(code string) error {
	var weather models.Weather
	e := GetRemoteJson("http://t.weather.itboy.net/api/weather/city/"+code, &weather)
	if e != nil {
		logs.Error("Update Weather Error: %+v", e)
		return e
	}
	if weather.Status != 200 {
		return errors.New(fmt.Sprintf("[%d]%s", weather.Status, weather.Message))
	}
	weather.CityCode = weather.CityInfo.CityId
	if e = gDB.Save(&weather).Error; e != nil {
		return errors.Wrap(e, "Save Error")
	}
	return nil
}
