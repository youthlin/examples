package main

import (
	"io"
	"os"
	"path/filepath"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/dialog"
	"fyne.io/fyne/v2/storage"
	"fyne.io/fyne/v2/widget"
	"github.com/youthlin/t"
	"github.com/youthlin/z"
)

func Init() {
	z.SetGlobalLogger(z.NewLogger(z.DefaultConfig()))
	z.Info("app start")

	path := os.Getenv("LANG_PATH")
	if path == "" {
		abs, err := filepath.Abs(filepath.Dir(os.Args[0]))
		if err == nil { // 获取可执行文件所在目录的绝对路径
			path = filepath.Join(abs, "lang")
			if _, err := os.Stat(path); os.IsNotExist(err) {
				z.Info("目录不存在: %v", path) // go run 时
				path = ""
			} else {
				z.Info("从路径搜索到了 lang 目录: abs=%v | err=%+v", abs, err)
			}
		}
	}
	if path == "" {
		path = "./lang"
	}
	z.Info("LANG_PATH=%v", path)
	t.BindDefaultDomain(path)
	t.SetLocale("")
}

func main() {
	Init()

	a := app.New()
	setTheme(a, newDefaultTheme()) // 设置了字体以显示中文

	w := a.NewWindow(t.T("Hello"))
	w.SetMainMenu(fyne.NewMainMenu(fyne.NewMenu(
		t.T("File"),
		fyne.NewMenuItem(
			t.T("Font"),
			func() {
				dl := dialog.NewFileOpen(func(uc fyne.URIReadCloser, e error) {
					z.Info("file chose|uc=%v|err=%+v", uc, e)
					if e != nil || uc == nil || uc.URI() == nil {
						return
					}
					bytes, err := io.ReadAll(uc)
					if err != nil {
						z.Warn("read file err=%+v", err)
						return
					}
					th := newThemeWithFont(fyne.NewStaticResource(uc.URI().Name(), bytes))
					setTheme(a, th)
				}, w)
				dl.SetFilter(storage.NewExtensionFileFilter([]string{".ttf"}))
				dl.Show()
			},
		),
	)))

	var (
		helloTxt      = []string{t.T("Hello Fyne!"), t.T("Welcome :)")}
		helloTxtIndex = 0
		labelHello    = widget.NewLabel(helloTxt[helloTxtIndex])
	)
	w.SetContent(container.NewCenter(container.NewVBox(
		labelHello,
		widget.NewButton(t.T("Hi!"), func() {
			helloTxtIndex++
			labelHello.SetText(helloTxt[helloTxtIndex%len(helloTxt)])
		}),
	)))

	w.SetOnClosed(func() {
		z.Info("closed.")
	})

	w.Resize(fyne.NewSize(600, 400))
	w.ShowAndRun()
}

func setTheme(app fyne.App, theme fyne.Theme) {
	app.Settings().SetTheme(theme)
}
