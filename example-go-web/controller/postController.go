package controller

import (
	"github.com/youthlin/examples/example-go-web/data"
	"github.com/youthlin/examples/example-go-web/service"
	"net/http"
	"strconv"
	"time"
)

// post
func ReplyThread(writer http.ResponseWriter, request *http.Request) {
	user := service.FindUserFromCookie(request)
	model := make(map[string]interface{})
	if user == nil {
		toError(writer, request, model, "请先登录", nil)
	}
	request.ParseForm()
	tid := request.PostFormValue("tid")
	pid := request.PostFormValue("pid")
	threadId, err := strconv.ParseInt(tid, 10, 64)
	if err != nil {
		toError(writer, request, model, "找不到要回复的帖子", err)
		return
	}
	parentId, err := strconv.ParseInt(pid, 10, 64)
	if err != nil {
		toError(writer, request, model, "找不到要回复的帖子", err)
		return
	}
	content := request.PostFormValue("reply")
	post := data.Post{
		Content:  content,
		ThreadId: threadId,
		ParentId: parentId,
		CreateAt: time.Now(),
		User:     user,
	}
	err = service.SavePost(&post)
	if err != nil {
		toError(writer, request, model, "发表回复失败", err)
		return
	}
	http.Redirect(writer, request, "/thread/view?id="+tid+"#p-"+strconv.FormatInt(post.Id, 10), http.StatusTemporaryRedirect)
}
