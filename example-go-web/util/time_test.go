package util

import (
	"fmt"
	"testing"
	"time"
)

func TestCreateUUID(t *testing.T) {
	now := time.Now()
	s(now)
	zone := time.FixedZone("Asia/Shanghai", -480*60)
	s(now.In(zone))
	zone = time.FixedZone("Asia/Shanghai", 480*60)
	s(now.In(zone))
}

func s(d time.Time) {
	fmt.Println(d.Format(time.RFC3339) + "   【" + d.Format(DEFAULT_TIME_FORMAT) + "】")
}
