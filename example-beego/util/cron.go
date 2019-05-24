package util

import (
	"log"
	"time"
)

/**
@param taskFunc 要执行的任务
@param nextTimeCalculator 计算下次执行的时机 入参本次执行完成的时间 返回下次执行的时间
@param errorHandler 错误处理 返回 false 则停止定时任务
*/
func StartTimer2(taskFunc func() error, nextTimeCalculator func(time.Time) time.Time, errorHandler func(err error) bool) {
	go func() {
		for {
			e := taskFunc()
			if e != nil {
				if !errorHandler(e) {
					log.Printf("Timer exit because error handler return false ")
					break
				}
			}
			now := time.Now()
			next := nextTimeCalculator(now)
			log.Printf("Task done at %s, next exe time: %s", now, next)
			timer := time.NewTimer(next.Sub(now))
			<-timer.C
		}
	}()
}
