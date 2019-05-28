# example-beego 
## Weather Forecast
演示 Demo [https://w.youthlin.com/](https://w.youthlin.com/)  

```
go get github.com/youthlin/examples/example-beego
cd $GOPATH/src/github.com/youthlin/examples/example-beego
vim conf/db.conf
```
修改 `db.conf` 中数据库用户名、密码和数据库
```
go clean
go build
./example-beego
```
或者 `go run main.go`  
打开浏览器 端口 8888 (`app.conf` 可修改端口)

免费天气 API: https://www.sojson.com/blog/305.html

Screen shot:  
首页  
![Home](screenshots/Home.png)  
城市列表  
![CityList](screenshots/CityList.png)  
天气预报 
![Weather](screenshots/Weather.png)  
关于  
![About](screenshots/About.png)  

<small>部分代码来自旧仓库 https://github.com/YouthLin/Weather</small>
