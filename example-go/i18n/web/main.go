package main

import (
	"context"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/cockroachdb/errors"
	"github.com/gin-gonic/gin"
	"github.com/youthlin/examples/example-go/i18n/web/handler"
	"github.com/youthlin/examples/example-go/i18n/web/handler/middleware"
	"github.com/youthlin/t"
	"github.com/youthlin/z"
)

const Addr = ":8099"

func Init() {
	z.SetGlobalLogger(z.NewLogger(z.DefaultConfig()))
	wd, err := os.Getwd()
	z.Info("pid=%d work dir=%s err=%+v", os.Getpid(), wd, err)

	t.Load("./lang")
	z.Info("supported langs: %v", t.Locales())
}

func main() {
	Init()
	r := gin.Default()
	router(r)
	start(r)
}

func router(r *gin.Engine) {
	r.Use(middleware.T)
	r.GET("/ping", handler.Ping)
	r.GET("/index", handler.Index)
}

func start(r *gin.Engine) {
	srv := &http.Server{
		Addr:    Addr,
		Handler: r,
	}

	// start
	go func() {
		if err := srv.ListenAndServe(); err != nil && errors.Is(err, http.ErrServerClosed) {
			z.Warn("listen: %+v", err)
		}
	}()

	graceffulyShutdown(srv)
}

// graceffulyShutdown 优雅停机
// https://polarisxu.studygolang.com/posts/go/signal-notifycontext/
func graceffulyShutdown(server *http.Server) {
	ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	<-ctx.Done() // 监听中断信号
	stop()       // 重置 os.Interrupt 的默认行为, 再次按下 ^C 会直接退出
	z.Info("shutting down graceffuly, press Ctrl+C again to force exist immediately")

	// 最多等待 5 秒
	timeOutCtx, cancal := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancal()
	if err := server.Shutdown(timeOutCtx); err != nil {
		z.Warn("Shutdown server error|%+v", err)
	} else {
		z.Info("Shutdown ok")
	}
}
