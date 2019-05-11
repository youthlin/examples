package service

import (
	"encoding/json"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/orm"
	_ "github.com/go-sql-driver/mysql"
	"github.com/pkg/errors"
	"github.com/youthlin/examples/example-beego/models"
	"io/ioutil"
	"log"
)

var o orm.Ormer
var city = new(models.City)

func init() {
	// set default database
	e := orm.RegisterDataBase("default", "mysql", beego.AppConfig.String("data.source.url"))
	if e != nil {
		panic(errors.Wrap(e, "RegisterDataBase Error"))
	}

	// 不支持指定表的字符集 https://github.com/astaxie/beego/issues/2698
	// orm.RunSyncdb("default", false, true)

	o = orm.NewOrm()

}

func ListAllCity() (maps map[int]models.City, e error) {
	var cities []*models.City
	_, e = o.QueryTable(city).All(&cities)
	if e != nil {
		return maps, errors.Wrap(e, "Query All City error")
	}
	maps = cityListToMap(cities)
	if len(maps) == 0 {
		log.Println("load from city.json file")
		bytes, e := ioutil.ReadFile("conf/city.json")
		if e != nil {
			log.Printf("error read city.json. %+v", e)
		}
		e = json.Unmarshal(bytes, &cities)
		if e != nil {
			return maps, errors.Wrap(e, "Read City Json File Error")
		}
		for _, c := range cities {
			_, e := o.Insert(c)
			if e != nil {
				return maps, errors.Wrap(e, "Insert City Error")
			}
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

func SearchCity(words string) (maps map[int]models.City, e error) {
	var cities []*models.City
	_, e = o.QueryTable(city).Filter("name__startswith", words).All(&cities)
	if e != nil {
		return maps, errors.Wrap(e, "Search City error")
	}
	maps = cityListToMap(cities)
	return
}
