package models

import "github.com/astaxie/beego/orm"

type City struct {
	Id   int    `json:"id"`
	Pid  int    `json:"pid"`
	Code string `json:"city_code"`
	Name string `json:"city_name"`
}

func init() {
	orm.RegisterModel(new(City))
}
