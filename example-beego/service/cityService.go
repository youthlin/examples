package service

import (
	"encoding/json"
	"github.com/astaxie/beego/logs"
	_ "github.com/go-sql-driver/mysql"
	"github.com/pkg/errors"
	"github.com/youthlin/examples/example-beego/models"
	"io/ioutil"
	"sort"
)

func ListAllCity() (maps map[int]models.City, e error) {
	var cities []*models.City
	gDB.Find(&cities)
	maps = cityListToMap(cities)
	if len(maps) == 0 {
		logs.Info("load from city.json file")
		bytes, e := ioutil.ReadFile("conf/city.json")
		if e != nil {
			logs.Error("error read city.json. %+v", e)
		}
		e = json.Unmarshal(bytes, &cities)
		if e != nil {
			return maps, errors.Wrap(e, "Read City Json File Error")
		}
		for _, c := range cities {
			gDB.Save(c)
		}
		maps = cityListToMap(cities)
	}
	return maps, nil
}

func cityListToMap(cities []*models.City) map[int]models.City {
	maps := make(map[int]models.City)
	for _, c := range cities {
		maps[c.Id] = *c
	}
	return maps
}

func GroupCities(maps *map[int]models.City) models.Provinces {
	var provinces models.Provinces
	for _, v := range *maps {
		if v.Pid == 0 {
			cities := getCities(maps, v.Id)
			country := getCountry(maps, cities)
			province := models.Province{
				Province: v,
				Cities:   cities,
				Country:  country,
			}
			provinces = append(provinces, province)
		}
	}
	sort.Sort(provinces)
	return provinces
}

func getCities(maps *map[int]models.City, pid int) models.Cities {
	var cities models.Cities
	for _, v := range *maps {
		if v.Pid == pid {
			cities = append(cities, v)
		}
	}
	sort.Sort(cities)
	return cities
}
func getCountry(maps *map[int]models.City, cities models.Cities) map[models.City]models.Cities {
	country := make(map[models.City]models.Cities)
	for _, city := range cities {
		country[city] = getCities(maps, city.Id)
	}
	return country
}

func SearchCity(words string) (maps map[int]models.City, e error) {
	var cities []*models.City
	gDB.Find(&cities, "name LIKE ?", words+"%")
	maps = cityListToMap(cities)
	return
}

func CityCountIncrement(code string) {
	var city models.City
	if e := gDB.Model(&models.CityInstance).Where("code = ?", code).First(&city).Error; e == nil {
		city.SearchCount++
		gDB.Model(&models.CityInstance).Update(&city)
	} else {
		logs.Error("add count error: %+v", e)
	}
}
func ListCityByCount() (cities []models.City, err error) {
	err = gDB.Model(&models.CityInstance).Not("code", defaultHotCode).Order("search_count desc").Limit(12).Find(&cities).Error
	return
}
