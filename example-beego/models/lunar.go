package models

// https://www.sojson.com/open/api/lunar/json.shtml?date=2019-05-12

type Lunar struct {
	Status  int       `json:"status"`
	Message string    `json:"message"`
	Data    LunarData `json:"data"`
}
type LunarData struct {
	Year            int            `json:"year"`
	Month           int            `json:"month"`
	Day             int            `json:"day"`
	LunarYear       int            `json:"lunarYear"`
	LunarMonth      int            `json:"lunarMonth"`
	LunarDay        int            `json:"lunarDay"`
	CnYear          string         `json:"cnyear"`
	CnMonth         string         `json:"cnmonth"`
	CnDay           string         `json:"cnday"`
	HYear           string         `json:"hyear"`
	CyclicalYear    string         `json:"cyclicalYear"`
	CyclicalMonth   string         `json:"cyclicalMonth"`
	CyclicalDay     string         `json:"cyclicalDay"`
	Suit            string         `json:"suit"`
	Taboo           string         `json:"taboo"`
	Animal          string         `json:"animal"`
	Week            string         `json:"week"`
	FestivalList    []string       `json:"festivalList"`
	Jieqi           map[int]string `json:"jieqi"`
	MaxDayInMonth   int            `json:"maxDayInMonth"`
	Leap            bool           `json:"leap"`
	LunarYearString string         `json:"lunarYearString"`
	BigMonth        bool           `json:"bigMonth"`
}

func init() {
	// orm.RegisterModel(new(Lunar))
}
