package controller

import (
	"fmt"
	"github.com/youthlin/examples/example-go-web/data"
	"github.com/youthlin/examples/example-go-web/service"
	"github.com/youthlin/examples/example-go-web/util"
	"html/template"
	"log"
	"net/http"
	"regexp"
	"strings"
	"time"
)

func Index(writer http.ResponseWriter, request *http.Request) {
	model := data.Model{}
	sizeInt := util.ToIntWithCheck(request.FormValue("size"), util.DEFAULT_THREAD_LIST_PAGE_SIZE, util.MAX_THREAD_LIST_PAGE_SIZE, 1)
	totalPage, err := service.CountThreadPage(sizeInt)
	if err != nil {
		toError(writer, request, model, "", err)
		return
	}
	pageInt := util.ToIntWithCheck(request.FormValue("page"), util.DEFAULT_PAGE, totalPage, 1)
	model["Title"] = "所有帖子"
	threads, err := service.ListThread(pageInt, sizeInt)
	if err != nil {
		toError(writer, request, model, "", err)
		return
	}
	model["Threads"] = threads
	if totalPage > 1 {
		model["TotalPage"] = totalPage
		model["Page"] = pageInt
		model["Size"] = sizeInt
	}
	generateHTML(request, writer, model, "layout", "thread-list")
}

// auth filter
func RequireLogin(h http.HandlerFunc) http.HandlerFunc {
	return func(writer http.ResponseWriter, request *http.Request) {
		user := service.FindUserFromCookie(request)
		if user == nil {
			toError(writer, request, data.Model{}, "请先登录", nil)
			return
		}
		h(writer, request)
	}
}

func fillUser(request *http.Request, model map[string]interface{}) {
	session := service.GetSession(request)
	if session != nil {
		model["Session"] = session
	}
	user := service.FindUserFromCookie(request)
	if user != nil {
		model["User"] = user
	}
}

func fillTitle(model map[string]interface{}) {
	title := model["Title"]
	if title != nil {
		switch title.(type) {
		case string:
			old := title.(string)
			model["Title"] = old + "|GoBBS"
		default:
			model["Title"] = "GoBBS"
		}
	} else {
		model["Title"] = "GoBBS"
	}
}

func generateHTML(request *http.Request, writer http.ResponseWriter, model map[string]interface{}, filenames ...string) {
	fillUser(request, model)
	var files []string
	for _, file := range filenames {
		files = append(files, fmt.Sprintf("templates/%s.html", file))
	}
	t := template.New("layout").Funcs(funcMap())
	templates := template.Must(t.ParseFiles(files...))
	fillTitle(model)
	err := templates.ExecuteTemplate(writer, "layout", model)
	if err != nil {
		log.Println(err)
	}
}

func funcMap() template.FuncMap {
	return template.FuncMap{
		"short":         short,
		"removeHtmlTag": removeHtmlTag,
		"unescaped":     unescaped,
		"add":           add,
		"gravatar":      gravatar,
		"d":             fixTimeZone,
	}
}
func short(x string) string {
	length := len(x)
	if length > 200 {
		return x[0:200]
	}
	return x
}
func removeHtmlTag(x string) string {
	str := strings.TrimSpace(x) // 去空格
	// 将HTML标签全转换成小写
	re, _ := regexp.Compile("\\<[\\S\\s]+?\\>")
	str = re.ReplaceAllStringFunc(str, strings.ToLower)
	// 替换掉注释和一些标签
	reg := regexp.MustCompile(`<!--.*?-->|<[^>]+>`)
	str = reg.ReplaceAllString(str, "")
	return strings.ReplaceAll(str, "<.*?>", "")
}
func unescaped(x string) interface{} { return template.HTML(x) }
func add(i int, j int) int           { return i + j }
func gravatar(email string) string {
	return "https://www.gravatar.com/avatar/" + util.Md5(email) + ".jpg"
}
func fixTimeZone(d time.Time, session *data.Session) string {
	if session != nil {
		min := (*session.Ext)["timezoneMinute"]
		name := (*session.Ext)["timezoneName"]
		if min != nil && name != nil {
			zone := time.FixedZone(name.(string), 60*min.(int))
			return d.In(zone).Format(util.DEFAULT_TIME_FORMAT)
		}
	}
	return d.Format(time.RFC3339)
}
