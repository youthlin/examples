package controller

import (
	"fmt"
	"github.com/youthlin/examples/example-go-web/service"
	"github.com/youthlin/examples/example-go-web/util"
	"html/template"
	"log"
	"net/http"
	"regexp"
	"strings"
)

func Index(writer http.ResponseWriter, request *http.Request) {
	model := newModel()
	pageInt := util.ToIntWithCheck(request.FormValue("page"), util.DEFAULT_PAGE, util.INT_MAX, 1)
	sizeInt := util.ToIntWithCheck(request.FormValue("size"), util.DEFAULT_THREAD_LIST_PAGE_SIZE, util.MAX_THREAD_LIST_PAGE_SIZE, 1)
	model["Title"] = "所有帖子"
	threads, err := service.ListThreadWithoutPost(pageInt, sizeInt)
	if err != nil {
		toError(writer, request, model, "", err)
		return
	}
	model["Threads"] = threads
	totalPage, err := service.CountThreadPage(sizeInt)
	if err != nil {
		toError(writer, request, model, "", err)
		return
	}
	if totalPage > 1 {
		model["TotalPage"] = totalPage
		model["Page"] = pageInt
		model["Size"] = sizeInt
	}
	generateHTML(request, writer, model, "layout", "thread-list")
}

func RequireLogin(h http.HandlerFunc) http.HandlerFunc {
	return func(writer http.ResponseWriter, request *http.Request) {
		user := service.FindUserFromCookie(request)
		if user == nil {
			toError(writer, request, newModel(), "请先登录", nil)
			return
		}
		h(writer, request)
	}
}

func newModel(kv ...interface{}) map[string]interface{} {
	model := make(map[string]interface{})
	length := len(kv)
	for i := 0; i < length; i += 2 {
		model[kv[i].(string)] = kv[i+1]
	}
	return model
}

func setUserToModel(request *http.Request, model map[string]interface{}) {
	user := service.FindUserFromCookie(request)
	if user != nil {
		model["User"] = user
	}
}

func fillModel(model map[string]interface{}) {
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
	setUserToModel(request, model)
	var files []string
	for _, file := range filenames {
		files = append(files, fmt.Sprintf("templates/%s.html", file))
	}
	t := template.New("layout").Funcs(funcMap())
	templates := template.Must(t.ParseFiles(files...))
	fillModel(model)
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
