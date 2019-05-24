package models

import (
	"time"
)

var CityInstance = new(City)

type City struct {
	Id        int    `json:"id"`
	Pid       int    `json:"pid"`
	Code      string `json:"city_code"`
	Name      string `json:"city_name"`
	CreatedAt time.Time
	UpdatedAt time.Time
	DeletedAt *time.Time `sql:"index"`
}

type Cities []City

func (cities Cities) Len() int {
	return len(cities)
}
func (cities Cities) Less(i int, j int) bool {
	return cities[i].Id < cities[j].Id
}
func (cities Cities) Swap(i int, j int) {
	tmp := cities[i]
	cities[i] = cities[j]
	cities[j] = tmp
}

type Province struct {
	Province City
	Cities   Cities
	Country  map[City]Cities
}
type Provinces []Province

func (provinces Provinces) Len() int {
	return len(provinces)
}
func (provinces Provinces) Less(i int, j int) bool {
	return provinces[i].Province.Id < provinces[j].Province.Id
}
func (provinces Provinces) Swap(i int, j int) {
	tmp := provinces[i]
	provinces[i] = provinces[j]
	provinces[j] = tmp
}
