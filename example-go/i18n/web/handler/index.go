package handler

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/youthlin/examples/example-go/i18n/web/handler/middleware"
	"github.com/youthlin/t"
	"github.com/youthlin/t/locale"
	"github.com/youthlin/z"
	"golang.org/x/text/language"
	"golang.org/x/text/language/display"
)

type Lang struct {
	Code    string
	Self    string
	Display string
	Used    bool
}

// Index 主页
func Index(c *gin.Context) {
	userLang := c.Query("lang")
	if userLang != "" {
		userLang = locale.Normalize(userLang)
		z.Info("user set lang to: %v", userLang)
		c.Set(middleware.GinCtxKeyTranslation, t.L(userLang))
		c.SetCookie(middleware.CookieKeyLang, userLang, 0, "", "", true, true)
	}
	RenderHTML(c, http.StatusOK, gin.H{
		"langs": getAllLangs(c),
	}, "home", "index")
}

// getAllLangs 返回所有支持的翻译
func getAllLangs(c *gin.Context) []*Lang {
	ts := middleware.GetTs(c)
	usedCode := ts.UsedLocale()
	z.Info("实际使用的语言=%v", usedCode)

	usedTag := language.Make(usedCode)
	var langs = []*Lang{}
	for _, lang := range ts.Locales() {
		tag := language.Make(lang)
		langs = append(langs, &Lang{
			Code:    lang,
			Self:    display.Self.Name(tag),
			Display: display.Tags(usedTag).Name(tag),
			Used:    usedCode == lang,
		})
	}
	return langs
}
