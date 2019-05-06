package data

import (
	"strings"
	"time"
)

// region user----------------------------------------------------------

type User struct {
	Id          int64
	Name        string
	DisplayName string
	Email       string
	Password    string
	CreateAt    time.Time
}

func (user *User) CreateAtDate() string {
	// month/day hour:minus:secondsPM year timeZone
	// 01/02 03:04:05 06 -700
	return user.CreateAt.Format("2006-01-02 15:04:05")
}

// endregion user----------------------------------------------------------

// region thread----------------------------------------------------------

type Thread struct {
	Id       int64
	Topic    string
	Content  string
	Tag      string
	CreateAt time.Time

	User       *User
	ReplyCount int
}

func (thread *Thread) Tags() []string {
	return strings.Split(thread.Tag, ",")
}

func (thread Thread) CreateAtDate() string {
	// month/day hour:minus:secondsPM year timeZone
	// 01/02 03:04:05 06 -700
	return thread.CreateAt.Format("2006-01-02 15:04:05")
}

// endregion thread----------------------------------------------------------

// region post----------------------------------------------------------

type Post struct {
	Id       int64
	Content  string
	ThreadId int64
	ParentId int64
	CreateAt time.Time

	User *User
}

func (post *Post) CreateAtDate() string {
	// month/day hour:minus:secondsPM year timeZone
	// 01/02 03:04:05 06 -700
	return post.CreateAt.Format("2006-01-02 15:04:05")
}

// endregion post----------------------------------------------------------

// region session----------------------------------------------------------

type Session struct {
	Uuid     string
	CreateAt time.Time
	User     *User
}

// endregion session----------------------------------------------------------
