package service

import (
	"github.com/pkg/errors"
	"github.com/youthlin/examples/example-go-web/data"
)

func SavePost(post *data.Post) error {
	stmt, e := Db.Prepare("INSERT INTO post( content, user_id, thread_id, pid, create_at) VALUE (?,?,?,?,?)")
	if e != nil {
		return errors.Wrap(e, "[prepare]保存回复出错")
	}
	result, e := stmt.Exec(post.Content, post.User.Id, post.ThreadId, post.ParentId, post.CreateAt)
	if e != nil {
		return errors.Wrap(e, "[stmt exec]保存回复失败")
	}
	insertId, e := result.LastInsertId()
	if e != nil {
		return errors.Wrap(e, "[get insert id]保存回复出错")
	}
	post.Id = insertId
	return nil
}

func FindPostsByThreadId(tid int64, page int, size int) (posts []data.Post, err error) {
	stmt, err := Db.Prepare("SELECT p.id, p.content, p.thread_id, p.pid, p.create_at, " +
		" u.id user_id,u.name,u.display_name,u.email,u.password,u.create_at " +
		" FROM    (SELECT id FROM post WHERE thread_id=? LIMIT ?,?) tmp " +
		"    LEFT JOIN(post p JOIN user u ON p.user_id=u.id)" +
		"    ON tmp.id=p.id ")
	if err != nil {
		return posts, errors.Wrap(err, "[prepare]查询回复列表出错")
	}
	start := (page - 1) * size
	rows, err := stmt.Query(tid, start, size)
	if err != nil {
		return posts, errors.Wrap(err, "[stmt query]查询回复列表出错")
	}
	for rows.Next() {
		user := data.User{}
		post := data.Post{
			User: &user,
		}
		err = rows.Scan(&post.Id, &post.Content, &post.ThreadId, &post.ParentId, &post.CreateAt,
			&user.Id, &user.Name, &user.DisplayName, &user.Email, &user.Password, &user.CreateAt)
		if err != nil {
			return posts, errors.Wrap(err, "[scan]查询回复失败")
		}
		posts = append(posts, post)
	}
	return posts, nil
}
