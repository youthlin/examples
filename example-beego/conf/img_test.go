package conf

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"os"
	"strings"
	"testing"
)

type Img struct {
	Hover string `json:"hover"`
	Image string `json:"image"`
}

func TestName(t *testing.T) {
	bytes, _ := ioutil.ReadFile("weather-images.json")
	maps := make(map[string]*Img)
	json.Unmarshal(bytes, &maps)
	for k, v := range maps {
		split := strings.Split(k, "|")
		hover, e := get(v.Hover)
		if e != nil {
			panic(e)
		}
		img, _ := get(v.Image)
		for _, name := range split {
			fmt.Printf("%s:%s\n", name, *v)
			save("../static/img/icon/"+name+".png", hover)
			save("../static/img/icon/"+name+"2.png", img)
		}
		save("../static/img/icon/"+getPngName(v.Hover), hover)
		save("../static/img/icon/"+getPngName(v.Image), img)
	}
}
func get(url string) (b []byte, e error) {
	fmt.Printf("get url: %s\n", url)
	resp, e := http.Get(url)
	if e != nil {
		panic(e)
	}
	b, e = ioutil.ReadAll(resp.Body)
	return
}
func save(name string, b []byte) {
	file, _ := os.Create(name)
	n, err := file.Write(b)
	if err != nil {
		fmt.Printf("Error write file %+v\n", err)
	} else {
		fmt.Printf("Saved %s[%d]\n", file.Name(), n)
	}
}
func getPngName(url string) string {
	arr := strings.Split(url, "/")
	return arr[len(arr)-1]

}
