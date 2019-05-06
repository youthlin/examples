package controller

import (
	"github.com/youthlin/examples/example-go-web/data"
	"github.com/youthlin/examples/example-go-web/service"
	"net/http"
)

func toError(writer http.ResponseWriter, request *http.Request, model map[string]interface{}, errMsg string, err error) {
	if errMsg == "" {
		model["Error"] = err.Error()
	} else {
		model["Error"] = errMsg
	}
	model["Err"] = err
	model["Title"] = "错误"
	generateHTML(request, writer, model, "layout", "error")
}

// GET
func SignUpPage(writer http.ResponseWriter, request *http.Request) {
	model := data.Model{"Title": "注册"}
	generateHTML(request, writer, model, "layout", "sign-up")
}

// POST
func SignUp(writer http.ResponseWriter, request *http.Request) {
	model := data.Model{}
	request.ParseForm()
	username := request.PostFormValue("username")
	email := request.PostFormValue("email")
	password := request.PostFormValue("password")
	user := data.User{
		Name:        username,
		DisplayName: username,
		Email:       email,
		Password:    password,
	}
	old, e := service.FindUserByName(username)
	if e != nil {
		toError(writer, request, model, "查询用户名出错", e)
		return
	}
	if old != nil {
		toError(writer, request, model, "该用户名已被使用:"+username, nil)
		return
	}
	old, e = service.FindUserByEmail(email)
	if e != nil {
		toError(writer, request, model, "查询电子邮件出错", e)
		return
	}
	if old != nil {
		toError(writer, request, model, "该电子邮件已被使用:"+email, e)
		return
	}
	e = service.SaveUser(&user)
	if e != nil {
		toError(writer, request, model, "保存用户失败", e)
		return
	}
	service.SetSession(writer, request, &user)
	http.Redirect(writer, request, "/", http.StatusTemporaryRedirect)
}

// POST
func Login(writer http.ResponseWriter, request *http.Request) {
	request.ParseForm()
	userName := request.PostFormValue("userName")
	password := request.PostFormValue("password")
	user, e := service.FindUserByNameAndPassword(userName, password)
	if e == nil {
		service.SetSession(writer, request, &user)
		http.Redirect(writer, request, "/", http.StatusTemporaryRedirect)
		return
	}
	toError(writer, request, data.Model{}, "用户名或密码错误", e)
}

// POST
func Logout(writer http.ResponseWriter, request *http.Request) {
	user := service.FindUserFromCookie(request)
	if user != nil {
		service.DeleteSession(writer, request)
	}
	http.Redirect(writer, request, "/", http.StatusTemporaryRedirect)
}
