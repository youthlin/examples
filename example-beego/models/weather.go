package models

import (
	"database/sql/driver"
	"encoding/json"
	"fmt"
	"time"
)

// region http://axiaoxin.com/article/241/

// JSONTime format json time field by myself
type JsonTime struct {
	time.Time
}

// MarshalJSON on JSONTime format Time field with %Y-%m-%d %H:%M:%S
func (t JsonTime) MarshalJSON() ([]byte, error) {
	formatted := fmt.Sprintf("\"%s\"", t.Format("2006-01-02 15:04:05"))
	return []byte(formatted), nil
}
func (this *JsonTime) UnmarshalJSON(data []byte) (err error) {
	now, err := time.ParseInLocation(`"2006-01-02 15:04:05"`, string(data), time.Local)
	*this = JsonTime{now}
	return
}

// Value insert timestamp into mysql need this function.
func (t JsonTime) Value() (driver.Value, error) {
	var zeroTime time.Time
	if t.Time.UnixNano() == zeroTime.UnixNano() {
		return nil, nil
	}
	return t.Time, nil
}

// Scan valueof time.Time
func (t *JsonTime) Scan(v interface{}) error {
	value, ok := v.(time.Time)
	if ok {
		*t = JsonTime{Time: value}
		return nil
	}
	return fmt.Errorf("can not convert %v to timestamp", v)
}

// endregion

// http://t.weather.sojson.com/api/weather/city/101240706
// http://t.weather.itboy.net/api/weather/city/101240706

var WeatherInstance = new(Weather)

type Weather struct {
	ID        int64
	CreatedAt JsonTime
	DeletedAt *JsonTime `sql:"index"`
	UpdatedAt JsonTime
	Time      JsonTime     `json:"time"`
	CityCode  string       `json:"city_code" sql:"index"`
	CityInfo  *CityInfo    `json:"cityInfo" gorm:"_"`
	Date      string       `json:"date"`
	Message   string       `json:"message"`
	Status    int          `json:"status"`
	Data      *WeatherData `json:"data" gorm:"type:text"`
}
type CityInfo struct {
	City       string `json:"city"`
	CityId     string `json:"cityId"`
	Parent     string `json:"parent"`
	UpdateTime string `json:"updateTime"`
}
type WeatherData struct {
	Shidu     string        `json:"shidu"`
	Pm25      float32       `json:"pm25"`
	Pm10      float32       `json:"pm10"`
	Quality   string        `json:"quality"`
	Wendu     string        `json:"wendu"`
	Ganmao    string        `json:"ganmao"`
	Yesterday WeatherItem   `json:"yesterday"`
	Forecast  []WeatherItem `json:"forecast"`
}
type WeatherItem struct {
	Date    string  `json:"date"`
	Sunrise string  `json:"sunrise"`
	High    string  `json:"high"`
	Low     string  `json:"low"`
	Sunset  string  `json:"sunset"`
	Aqi     float32 `json:"aqi"`
	Ymd     string  `json:"ymd"`
	Week    string  `json:"week"`
	Fx      string  `json:"fx"`
	Fl      string  `json:"fl"`
	Type    string  `json:"type"`
	Notice  string  `json:"notice"`
}

// https://stackoverflow.com/a/33182597
func (this *CityInfo) Scan(src interface{}) error {
	s, ok := src.([]uint8)
	if !ok {
		return fmt.Errorf("CityInfo field must be []uint8/[]byte, got %T instead", src)
	}
	return json.Unmarshal(s, this)
}

func (this CityInfo) Value() (driver.Value, error) {
	bytes, e := json.Marshal(this)
	if e == nil {
		return string(bytes), e
	}
	return "", e
}

func (this *WeatherData) Scan(src interface{}) error {
	s, ok := src.([]uint8)
	if !ok {
		return fmt.Errorf("WeatherData field must be []uint8/[]byte, got %T instead", src)
	}
	return json.Unmarshal(s, this)
}

func (this WeatherData) Value() (driver.Value, error) {
	bytes, e := json.Marshal(this)
	if e == nil {
		return string(bytes), e
	}
	return "", e
}
