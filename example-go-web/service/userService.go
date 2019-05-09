package service

import (
	"github.com/pkg/errors"
	"github.com/youthlin/examples/example-go-web/data"
	"github.com/youthlin/examples/example-go-web/util"
	"net/http"
	"time"
)

var userColumn = " id,name,display_name,email,password,create_at "

// 会话保存在内存 应用重启所有用户需要重新登录
var sessionMap = make(map[string]*data.Session)

func SetSession(writer http.ResponseWriter, request *http.Request, user *data.User) {
	uuid := util.CreateUUID()
	cookie := http.Cookie{
		Name:   util.COOKIE_NAME,
		Value:  uuid,
		Path:   "/",
		MaxAge: 3600,
	}
	timezoneMinute := request.PostFormValue("timezoneMinute")
	timezoneName := request.PostFormValue("timezoneName")

	session := data.Session{
		Uuid:     uuid,
		CreateAt: time.Now(),
		Ext: &map[string]interface{}{
			"timezoneMinute": -util.ToInt(timezoneMinute, 0),
			"timezoneName":   timezoneName,
		},
		User: user,
	}
	sessionMap[uuid] = &session
	http.SetCookie(writer, &cookie)
}

func DeleteSession(writer http.ResponseWriter, request *http.Request) {
	cookie, e := request.Cookie(util.COOKIE_NAME)
	if e != nil {
		return
	}
	cookie.MaxAge = -1
	uuid := cookie.Value
	sessionMap[uuid] = nil
	http.SetCookie(writer, cookie)
}

func GetSession(request *http.Request) *data.Session {
	cookie, e := request.Cookie(util.COOKIE_NAME)
	if e == nil {
		uuid := cookie.Value
		session := sessionMap[uuid]
		if session == nil {
			return nil
		}
		return session
	}
	return nil
}

func FindUserFromCookie(r *http.Request) *data.User {
	session := GetSession(r)
	if session != nil {
		return session.User
	}
	return nil
}

func FindUserByName(name string) (*data.User, error) {
	rows, err := Db.Query("SELECT "+userColumn+" FROM user WHERE name=? LIMIT 1", name)
	if err != nil {
		return nil, errors.Wrap(err, "[query]查询用户失败")
	}
	if rows.Next() {
		user := &data.User{}
		err = rows.Scan(&user.Id, &user.Name, &user.DisplayName, &user.Email, &user.Password, &user.CreateAt)
		if err != nil {
			return nil, errors.Wrap(err, "[scan]查询用户失败")
		}
		return user, nil
	}
	return nil, nil
}

func FindUserByEmail(email string) (*data.User, error) {
	rows, e := Db.Query("SELECT "+userColumn+" FROM user WHERE email=? LIMIT 1", email)
	if e != nil {
		return nil, errors.Wrap(e, "[query]查询用户失败")
	}
	if rows.Next() {
		user := &data.User{}
		e = rows.Scan(&user.Id, &user.Name, &user.DisplayName, &user.Email, &user.Password, &user.CreateAt)
		if e != nil {
			return user, errors.Wrap(e, "[scan]查询用户失败")
		}
	}
	return nil, nil
}

func FindUserByNameAndPassword(name string, password string) (data.User, error) {
	user := data.User{}
	row := Db.QueryRow("SELECT "+userColumn+" FROM user WHERE name=? AND password=? LIMIT 1", name, password)
	e := row.Scan(&user.Id, &user.Name, &user.DisplayName, &user.Email, &user.Password, &user.CreateAt)
	return user, errors.Wrap(e, "[scan]查询用户失败")
}

func SaveUser(user *data.User) error {
	stmt, e := Db.Prepare("INSERT INTO user(name,display_name,email,password)value (?,?,?,?)")
	if e != nil {
		return errors.Wrap(e, "[prepare insert]保存用户失败")
	}
	result, e := stmt.Exec(user.Name, user.DisplayName, user.Email, user.Password)
	if e != nil {
		return errors.Wrap(e, "[stmt exec]保存用户失败")
	}
	id, e := result.LastInsertId()
	if e != nil {
		return errors.Wrap(e, "[get result id]保存用户出错")
	}
	user.Id = id
	return nil
}
