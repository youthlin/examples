package models

import "time"

type UpdateDetail struct {
	TaskRunning bool
	StartedAt   time.Time
	DoneAt      time.Time
	NextStartAt time.Time
	DoneCount   int
	TotalCount  int
	DoneCities  []CityView
}
