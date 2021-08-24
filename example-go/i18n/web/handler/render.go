package handler

import (
	"html/template"
	"path/filepath"
	"strings"
	"sync"

	"github.com/gin-gonic/gin"
	"github.com/gin-gonic/gin/render"
	"github.com/youthlin/examples/example-go/i18n/web/handler/middleware"
	"github.com/youthlin/t"
	"github.com/youthlin/z"
)

// RenderHTML 渲染页面
// @param layout: templates/layout/${layout}.tmpl
// @param page: templates/${layout}/${page}.tmpl
func RenderHTML(c *gin.Context, code int, data gin.H, layout string, page ...string) {
	var tmpl *template.Template
	if gin.Mode() == gin.DebugMode {
		tmpl = newTempl(layout, page...)
	} else {
		tmpl = getCachedTmpl(layout, page...)
	}
	c.Render(code, &render.HTML{
		Data:     withLocale(c, data),
		Template: tmpl,
	})
}

// newTempl 构建一个模板
func newTempl(layout string, page ...string) *template.Template {
	fileNames := []string{filepath.Join("templates", "layout", layout+".tmpl")}
	for _, v := range page {
		fileNames = append(fileNames, filepath.Join("templates", layout, v+".tmpl"))
	}
	z.Info("layout=%v, page=%v, files=%v", layout, page, fileNames)
	return template.Must(template.ParseFiles(fileNames...))
}

// views 当不是 debug 模式时，会缓存模板解析结果
var views sync.Map

// getCachedTmpl 获取缓存的模板实例，没有会新建并缓存
func getCachedTmpl(layout string, page ...string) *template.Template {
	key := viewKey(layout, page...)
	if t, ok := views.Load(key); ok {
		if tt, ok := t.(*template.Template); ok {
			return tt
		}
	}
	tt := newTempl(layout, page...)
	views.Store(key, tt)
	return tt
}

// viewKey 多个模版文件名组成一个视图, 用文件名作为视图的 key: layout/part1+part2
func viewKey(layout string, part ...string) string {
	var sb strings.Builder
	sb.WriteString(layout)
	for i, v := range part {
		if i == 0 {
			sb.WriteString("/")
		} else {
			sb.WriteString("+")
		}
		sb.WriteString(v)
	}
	return sb.String()
}

// withLocale 将翻译组件注入数据
func withLocale(c *gin.Context, data gin.H) gin.H {
	ts := t.Global()
	locale, ok := c.Get(middleware.GinCtxKeyTranslation)
	if ok {
		ts = locale.(*t.Translations)
		z.Info("got transtions: %v", ts.Locale())
	}
	if data == nil {
		data = make(gin.H)
	}
	data["t"] = ts
	data["T"] = ts.T
	data["N"] = ts.N
	data["N64"] = ts.N64
	data["X"] = ts.X
	data["XN64"] = ts.XN64
	return data
}
