package service

import (
	"fmt"
	"github.com/astaxie/beego/logs"
	"github.com/jinzhu/gorm"
	"github.com/pkg/errors"
	"github.com/youthlin/examples/example-beego/models"
	"sort"
	"time"
)

var UpdateTaskRunning bool
var UpdateTaskStartedAt time.Time
var UpdateTaskLastDoneAt time.Time
var UpdatedCity = []models.CityView{}

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
	return gDB.Where("date = ? AND city_code = ?", date, code).Order("time desc").Limit(1).Find(weather).Error
}

func updateAllWeather() error {
	UpdateTaskRunning = true
	UpdateTaskStartedAt = time.Now()
	defer func() {
		UpdateTaskRunning = false
		UpdateTaskLastDoneAt = time.Now()
		UpdatedCity = nil
	}()
	maps, e := ListAllCity()
	if e != nil {
		return e
	}
	ids := []int{}
	for k, _ := range maps {
		ids = append(ids, k)
	}
	sort.Ints(ids)
	for _, id := range ids {
		v := maps[id]
		if v.Code != "" {
			// 300次每分钟 = 5qps
			e := updateWeather(v.Code)
			if e != nil {
				logs.Error("[update one error]更新天气出现异常 跳过该城市 city=%v error=%+v", v, e)
			}
			UpdatedCity = append(UpdatedCity, models.CityView{
				Id:   v.Id,
				Code: v.Code,
				Name: v.Name,
			})
			// 1s=1000ms 休眠 500ms~2qps 250ms~4qps
			time.Sleep(time.Millisecond * 300)
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
