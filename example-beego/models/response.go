package models

type Response struct {
	Code    int         `json:"code"`
	Message string      `json:"message"`
	Data    interface{} `json:"data"`
}

func MakeSuccess(data interface{}) Response {
	return Response{
		Code:    0,
		Message: "OK",
		Data:    data,
	}
}

func MakeFail(code int, msg string) Response {
	return Response{
		Code:    code,
		Message: msg,
	}
}
