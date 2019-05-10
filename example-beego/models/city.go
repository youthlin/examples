package models

type City struct {
	Id   int    `json:"id"`
	Pid  int    `json:"pid"`
	Code string `json:"city_code"`
	Name string `json:"city_name"`
}
