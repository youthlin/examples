package middleware

import (
	"github.com/gin-gonic/gin"
	"github.com/youthlin/t"
	"github.com/youthlin/t/locale"
	"github.com/youthlin/z"
	"golang.org/x/text/language"
)

const (
	CookieKeyLang         = "lang"
	HeaderAcceptLanguage  = "Accept-Language" // 浏览器语言
	GinCtxKeyTranslations = "$Translations"
)

// T 中间件，为每个请求设置翻译
func T(c *gin.Context) {
	// 1 cookie 中设置了语言
	lang, err := c.Cookie(CookieKeyLang)
	if err == nil {
		lang = locale.Normalize(lang)
		z.Info("从 cookie 中获取到用户之前设置的语言: %v", lang)
		SetTs(c, lang)
		c.Next()
		return
	}

	// 2 浏览器标头获取语言
	accept := c.GetHeader(HeaderAcceptLanguage)
	if accept == "" {
		lang = t.SourceCodeLocale()
		z.Info("无法获取用户语言偏好，使用源代码中语言: %v", lang)
		SetTs(c, lang)
		c.Next()
		return
	}
	supported := t.Locales()
	var supportedTags []language.Tag
	for _, lang := range supported {
		supportedTags = append(supportedTags, language.Make(lang))
	}
	matcher := language.NewMatcher(supportedTags)
	userPref, q, err := language.ParseAcceptLanguage(accept)
	z.Info("浏览器标头=%v|解析结果:userPref=%v, q=%v, err=%v", accept, userPref, q, err)
	tag, index, conf := matcher.Match(userPref...)
	lang = supported[index]
	z.Info("使用浏览器标头匹配结果|supported=%v|got=%v|index=%v|conf=%v|最终使用=%v",
		supportedTags, tag, index, conf, lang)
	SetTs(c, lang)
	c.Next()
}

func SetTs(c *gin.Context, lang string) {
	c.Set(GinCtxKeyTranslations, t.L(lang))
}

// GetTs 获取请求关联的翻译
func GetTs(c *gin.Context) *t.Translations {
	if tr, ok := c.Get(GinCtxKeyTranslations); ok {
		return tr.(*t.Translations)
	}
	return t.L(t.SourceCodeLocale())
}
