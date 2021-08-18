package main

import (
	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/theme"
	"github.com/youthlin/examples/example-go/i18n/gui/fonts"
)

func newDefaultTheme() *myTheme {
	return newThemeWithFont(fyne.NewStaticResource("霞鹜文楷等宽", fonts.MonoRegular))
}

func newThemeWithFont(font fyne.Resource) *myTheme {
	return &myTheme{
		Theme: theme.DefaultTheme(),
		font:  font,
	}
}

type myTheme struct {
	fyne.Theme
	font fyne.Resource
}

var _ fyne.Theme = (*myTheme)(nil)

func (t *myTheme) Font(style fyne.TextStyle) fyne.Resource {
	if t.font != nil {
		return t.font
	}
	return t.Theme.Font(style)
}
