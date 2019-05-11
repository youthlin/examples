package models

import (
	"time"
)

// http://t.weather.sojson.com/api/weather/city/101240706
// http://t.weather.itboy.net/api/weather/city/101240706

type Weather struct {
	UpdateTime time.Time   `json:"time"`
	CityInfo   City        `json:"cityInfo"`
	Date       string      `json:"date"`
	Message    string      `json:"message"`
	Status     int         `json:"status"`
	Data       WeatherData `json:"data"`
}

type WeatherData struct {
	Shidu     string        `json:"shidu"`
	Pm25      int           `json:"pm25"`
	Pm10      int           `json:"pm10"`
	Quality   string        `json:"quality"`
	Wendu     string        `json:"wendu"`
	Ganmao    string        `json:"ganmao"`
	Yesterday WeatherItem   `json:"yesterday"`
	Forecast  []WeatherItem `json:"forecast"`
}

type WeatherItem struct {
	Date    string `json:"date"`
	Sunrise string `json:"sunrise"`
	High    string `json:"high"`
	Low     string `json:"low"`
	Sunset  string `json:"sunset"`
	Aqi     int    `json:"aqi"`
	Ymd     string `json:"ymd"`
	Week    string `json:"week"`
	Fx      string `json:"fx"`
	Fl      string `json:"fl"`
	Type    string `json:"type"`
	Notice  string `json:"notice"`
}

func init() {
	// orm.RegisterModel(new(Weather))
}
