package service

import (
	"database/sql"
	"github.com/pkg/errors"
	"github.com/youthlin/examples/example-go-web/data"
	"github.com/youthlin/examples/example-go-web/util"
	"log"
	"strings"
)

var Db *sql.DB

func init() {
	d, e := sql.Open("mysql", "root:root@tcp(localhost:3306)/go_bbs?charset=utf8mb4&parseTime=True")
	if e != nil {
		log.Fatal(e)
	} else {
		Db = d
	}
}

func SaveThread(thread *data.Thread) error {
	stmt, e := Db.Prepare("INSERT INTO thread( topic, content, tags, user_id, create_at) VALUE (?,?,?,?,?)")
	if e != nil {
		return errors.Wrap(e, "[prepare insert]保存帖子失败")
	}
	result, e := stmt.Exec(thread.Topic, thread.Content, strings.Join(thread.Tags(), ","), thread.User.Id, thread.CreateAt)
	if e != nil {
		return errors.Wrap(e, "[stmt exec]保存帖子失败")
	}
	id, e := result.LastInsertId()
	if e != nil {
		return errors.Wrap(e, "[get result id]保存帖子发生了错误")
	}
	thread.Id = id
	return nil
}

func ListThreadWithoutPost(page int, size int) (threads []data.Thread, err error) {
	start := (page - 1) * size
	rows, err := Db.Query("SELECT t.id,t.topic,t.content,t.tags,t.create_at,"+
		" u.id user_id,u.name,u.display_name,u.email,u.password,u.create_at"+
		"	FROM (SELECT id FROM thread LIMIT ?,?) tmp"+
		"	      JOIN (thread t LEFT OUTER JOIN user u ON t.user_id = u.id)"+
		"	      ON tmp.id = t.id", start, size+1)
	if err != nil {
		return threads, errors.Wrap(err, "[query]查询帖子列表出错")
	}
	for rows.Next() {
		user := data.User{}
		thread := data.Thread{User: &user}
		err = rows.Scan(&thread.Id, &thread.Topic, &thread.Content, &thread.Tag, &thread.CreateAt,
			&user.Id, &user.Name, &user.DisplayName, &user.Email, &user.Password, &user.CreateAt)
		if err != nil {
			return threads, errors.Wrap(err, "[scan]查询帖子列表出错")
		}
		count, err := QueryReplyCountOfThread(thread.Id)
		if err != nil {
			return threads, errors.Wrap(err, "查询帖子回复数量出错")
		}
		thread.ReplyCount = count
		threads = append(threads, thread)
	}
	err = errors.Wrap(rows.Close(), "[close row]数据库错误")
	return
}
func QueryReplyCountOfThread(threadId int64) (count int, err error) {
	row := Db.QueryRow("SELECT COUNT(*)FROM post WHERE thread_id=?", threadId)
	err = row.Scan(&count)
	return
}

func FindThreadById(threadId int64) (data.Thread, error) {
	row := Db.QueryRow("SELECT t.id,t.topic,t.content,t.tags,t.create_at,"+
		" u.id user_id,u.name,u.display_name,u.email,u.password,u.create_at"+
		" FROM (SELECT * FROM thread  WHERE id=?) t LEFT JOIN user u ON t.user_id=u.id ", threadId)
	thread, e := getThreadFromRow(row)
	if e != nil {
		return thread, e
	}
	count, err := QueryReplyCountOfThread(thread.Id)
	if err != nil {
		return thread, errors.Wrap(err, "查询帖子回复量出错")
	}
	thread.ReplyCount = count
	return thread, e
}

func FindPreThread(threadId int64) (data.Thread, error) {
	row := Db.QueryRow("SELECT t.id,t.topic,t.content,t.tags,t.create_at,"+
		" u.id user_id,u.name,u.display_name,u.email,u.password,u.create_at"+
		" FROM (SELECT * FROM thread  WHERE id<? ORDER BY id DESC LIMIT 1) t LEFT JOIN user u ON t.user_id=u.id ", threadId)
	return getThreadFromRow(row)
}
func FindNextThread(threadId int64) (data.Thread, error) {
	row := Db.QueryRow("SELECT t.id,t.topic,t.content,t.tags,t.create_at,"+
		" u.id user_id,u.name,u.display_name,u.email,u.password,u.create_at"+
		" FROM (SELECT * FROM thread  WHERE id>? LIMIT 1) t LEFT JOIN user u ON t.user_id=u.id ", threadId)
	return getThreadFromRow(row)
}
func getThreadFromRow(row *sql.Row) (data.Thread, error) {
	user := data.User{}
	thread := data.Thread{User: &user}
	err := row.Scan(&thread.Id, &thread.Topic, &thread.Content, &thread.Tag, &thread.CreateAt,
		&user.Id, &user.Name, &user.DisplayName, &user.Email, &user.Password, &user.CreateAt)
	if err != nil {
		return thread, errors.Wrap(err, "[scan]查询帖子出错")
	}
	return thread, nil
}

func CountThreadPage(pageSize int) (int, error) {
	var count int
	row := Db.QueryRow("SELECT COUNT(*)FROM thread")
	err := row.Scan(&count)
	if err != nil {
		return count, errors.Wrap(err, "查询帖子数量错误")
	}
	return util.CountPage(count, pageSize), nil
}
