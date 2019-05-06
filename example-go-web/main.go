package main

import (
	_ "github.com/go-sql-driver/mysql"
	"github.com/youthlin/examples/example-go-web/controller"
	"net/http"
)

func main() {
	mux := http.NewServeMux()
	mux.Handle("/static/", http.StripPrefix("/static/", http.FileServer(http.Dir("static"))))

	mux.HandleFunc("/", controller.Index)

	// user
	mux.HandleFunc("/sign", controller.SignUpPage)
	mux.HandleFunc("/signUp", controller.SignUp)
	mux.HandleFunc("/login", controller.Login)
	mux.HandleFunc("/logout", controller.Logout)

	// thread
	mux.HandleFunc("/thread/new", controller.RequireLogin(controller.CreateThreadPage))
	mux.HandleFunc("/thread/save", controller.RequireLogin(controller.CreateThread))
	mux.HandleFunc("/thread/view", controller.ViewThread)

	// post
	mux.HandleFunc("/thread/reply", controller.RequireLogin(controller.ReplyThread))

	server := &http.Server{
		Addr:    "0.0.0.0:8088",
		Handler: mux,
	}
	server.ListenAndServe()

}
