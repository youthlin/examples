# Mini-MVC Example
A Spring-MVC-like MVC Container.  

Demo: [https://mvc.youthlin.com](https://mvc.youthlin.com)  
Mini-MVC + H2 + MyBatis + Thymeleaf  

- Auto scan and injected
- Interceptors support
- MyBatis support 
- ResponseBody support / JsonBody
- Thymeleaf support

If you would like to use MyBatis, Jackson or Thymeleaf, 
you should import dependency them at pom.xml

```java
@Dao
public interface IUserDao{
    //... need MyBatis mapper xml file
    List<User> list();
}
```

```java
@Controller
public class UserController{
    @Resource
    private IUserDao userDao;
    
    public String index(Map<String,Object> map){
        map.put("userList", userDao.list());
        return "list";
    }
}
```

```html
<!doctype html>
<html  lang="zh-CN" xmlns:th="http://www.thymeleaf.org/">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="ie=edge">
<title>User List</title>
</head>
<body>
    <table>
        <tr  th:each="user : ${userList}">
            <td th:text="${user.id}">1</td>
            <td th:text="${user.name}">Lin</td>
        </tr>
    </table>
</body>
</html>
```