package util

import (
	"github.com/astaxie/beego/logs"
	"log"
	"os"
	"time"
)

/**
定时任务
@param taskFunc 要执行的任务
@param nextTimeCalculator 计算下次执行的时机 入参本次执行完成的时间 返回下次执行的时间
@param errorHandler 错误处理 返回 false 则停止定时任务
*/
func StartTimer(taskFunc func() error, nextTimeCalculator func(time.Time) time.Time, errorHandler func(err error) bool) {
	logs.Info("os.Args=%v", os.Args)
	immediately := true
	if len(os.Args) > 1 {
		immediately = os.Args[1] != "-next"
	}
	run := immediately
	go func() {
		for {
			if run {
				e := taskFunc()
				if e != nil {
					if !errorHandler(e) {
						log.Printf("Timer exit because error handler return false ")
						break
					}
				}
			} else {
				logs.Info("本次任务不执行 可能是由于命令行参数指定了 -next")
			}
			run = true
			now := time.Now()
			next := nextTimeCalculator(now)
			log.Printf("Task done at %s, next execute time: %s", now, next)
			timer := time.NewTimer(next.Sub(now))
			<-timer.C
		}
	}()
}
