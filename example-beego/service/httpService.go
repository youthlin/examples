package service

import (
	"encoding/json"
	"github.com/astaxie/beego/logs"
	"github.com/pkg/errors"
	"io/ioutil"
	"net/http"
)

func GetRemoteJson(url string, v interface{}) error {
	resp, err := http.Get(url)
	if err != nil {
		return errors.Wrap(err, "http get error")
	}
	if resp != nil {
		defer resp.Body.Close()
	}
	bytes, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return errors.Wrap(err, "read response body error")
	}
	logs.Info("HTTP RESPONSE: %s", string(bytes))
	return errors.Wrap(json.Unmarshal(bytes, v), "json unmarshal error")
}
