package controller

import (
	"github.com/youthlin/examples/example-go-web/data"
	"github.com/youthlin/examples/example-go-web/service"
	"github.com/youthlin/examples/example-go-web/util"
	"log"
	"net/http"
	"strconv"
	"time"
)

// get
func CreateThreadPage(writer http.ResponseWriter, request *http.Request) {
	generateHTML(request, writer, data.Model{"Title": "发表新话题"}, "layout", "thread-new")
}

// post
func CreateThread(writer http.ResponseWriter, request *http.Request) {
	request.ParseForm()
	user := service.FindUserFromCookie(request)
	topic := request.PostFormValue("topic")
	content := request.PostFormValue("content")
	tag := request.PostFormValue("tag")
	thread := data.Thread{
		Topic:    topic,
		Content:  content,
		Tag:      tag,
		User:     user,
		CreateAt: time.Now(),
	}
	err := service.SaveThread(&thread)
	if err != nil {
		toError(writer, request, data.Model{}, "保存失败", err)
		return
	}
	http.Redirect(writer, request, "/thread/view?id="+strconv.FormatInt(thread.Id, 10), http.StatusTemporaryRedirect)
}

// get
func ViewThread(writer http.ResponseWriter, request *http.Request) {
	model := data.Model{}
	ids := request.FormValue("id")
	id, err := strconv.ParseInt(ids, 10, 64)
	if err != nil {
		http.Redirect(writer, request, "/", http.StatusTemporaryRedirect)
		return
	}
	thread, err := service.FindThreadById(id)
	if err != nil {
		toError(writer, request, model, "", err)
		return
	}
	model["Thread"] = thread
	model["Title"] = thread.Topic + "|查看帖子"
	preThread, err := service.FindPreThread(id)
	if err != nil {
		log.Printf("查询id=%d的上一篇出错:%+v", id, err)
	} else {
		model["PreThread"] = preThread
	}
	nextThread, err := service.FindNextThread(id)
	if err != nil {
		log.Printf("查询id=%d的下一篇出错:%+v", id, err)
	} else {
		model["NextThread"] = nextThread
	}
	size := util.ToIntWithCheck(request.FormValue("s"), util.DEFAULT_REPLY_LIST_PAGE_SIZE, util.MAX_REPLY_LIST_PAGE_SIZE, 1)
	totalPage := util.CountPage(thread.ReplyCount, size)
	page := util.ToIntWithCheck(request.FormValue("p"), util.DEFAULT_PAGE, totalPage, 1)
	posts, err := service.FindPostsByThreadId(thread.Id, page, size)
	if err != nil {
		toError(writer, request, model, "获取帖子("+string(thread.Id)+":"+thread.Topic+")回复列表失败", err)
		return
	}
	model["Posts"] = posts
	if totalPage > 1 {
		model["PostTotalPage"] = totalPage
		model["Page"] = page
		model["Size"] = size
	}
	generateHTML(request, writer, model, "layout", "thread-detail")
}
