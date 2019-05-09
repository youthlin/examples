package service

import (
	"database/sql"
	"log"
)

var Db *sql.DB

func init() {
	d, e := sql.Open("mysql", "root:root@tcp(localhost:3306)/go_bbs?charset=utf8mb4&parseTime=True&loc=Asia%2FShanghai")
	if e != nil {
		log.Fatal(e)
	} else {
		Db = d
	}
}
