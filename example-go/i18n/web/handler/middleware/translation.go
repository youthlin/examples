package middleware

import (
	"github.com/gin-gonic/gin"
	"github.com/youthlin/t"
	"github.com/youthlin/t/locale"
	"github.com/youthlin/z"
	"golang.org/x/text/language"
)

const (
	CookieKeyLang        = "lang"
	HeaderAcceptLanguage = "Accept-Language" // 浏览器语言
	GinCtxKeyTranslation = "T"
)

// T 中间件，为每个请求设置翻译
func T(c *gin.Context) {
	// 1 cookie 中设置了语言
	lang, err := c.Cookie(CookieKeyLang)
	if err == nil {
		lang = locale.Normalize(lang)
		c.Set(GinCtxKeyTranslation, t.L(lang))
		z.Info("use lang from cookie: %v", lang)
		c.Next()
		return
	}

	// 2 浏览器标头获取语言
	supported := t.Locales()
	var supportedTags []language.Tag
	for _, lang := range supported {
		supportedTags = append(supportedTags, language.Make(lang))
	}
	matcher := language.NewMatcher(supportedTags)
	userPref, _, _ := language.ParseAcceptLanguage(c.GetHeader(HeaderAcceptLanguage))
	_, index, _ := matcher.Match(userPref...)
	lang = supported[index]
	c.Set(GinCtxKeyTranslation, t.L(lang))
	z.Info("use lang from header: %v", lang)
	c.Next()
}

// GetTs 获取请求关联的翻译
func GetTs(c *gin.Context) *t.Translations {
	if tr, ok := c.Get(GinCtxKeyTranslation); ok {
		return tr.(*t.Translations)
	}
	return t.Global()
}
