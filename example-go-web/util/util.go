package util

import (
	"crypto/md5"
	"fmt"
	"log"
	"math/rand"
	"strconv"
	"time"
)

const (
	INT_MAX                       = int(^uint(0) >> 1)
	INT_MIN                       = ^INT_MAX
	DEFAULT_PAGE                  = 1
	DEFAULT_THREAD_LIST_PAGE_SIZE = 5
	MAX_THREAD_LIST_PAGE_SIZE     = 100
	DEFAULT_REPLY_LIST_PAGE_SIZE  = 5
	MAX_REPLY_LIST_PAGE_SIZE      = 100
)

// create a random UUID with from RFC 4122
// adapted from http://github.com/nu7hatch/gouuid
func CreateUUID() (uuid string) {
	rand.Seed(time.Now().Unix())
	u := new([16]byte)
	_, err := rand.Read(u[:])
	if err != nil {
		log.Fatalln("Cannot generate UUID", err)
	}

	// 0x40 is reserved variant from RFC 4122
	u[8] = (u[8] | 0x40) & 0x7F
	// Set the four most significant bits (bits 12 through 15) of the
	// time_hi_and_version field to the 4-bit version number.
	u[6] = (u[6] & 0xF) | (0x4 << 4)
	uuid = fmt.Sprintf("%x-%x-%x-%x-%x", u[0:4], u[4:6], u[6:8], u[8:10], u[10:])
	return
}

func Md5(x string) string {
	hash := md5.Sum([]byte(x))
	return fmt.Sprintf("%x", hash)
}
func ToInt(str string, dft int) int {
	if str == "" {
		return dft
	}
	i, e := strconv.Atoi(str)
	if e != nil {
		return dft
	}
	return i
}
func ToIntWithCheck(str string, dft int, max int, min int) int {
	i := ToInt(str, dft)
	if i > max {
		return dft
	}
	if i < min {
		return dft
	}
	return i
}
func CountPage(total int, size int) int {
	return (total-1)/size + 1
}
