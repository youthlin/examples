package com.youthlin.demo.mvc.controller;

import com.youthlin.demo.mvc.model.User;
import com.youthlin.demo.mvc.service.UserService;
import com.youthlin.ioc.annotation.Controller;
import com.youthlin.mvc.annotation.ConvertWith;
import com.youthlin.mvc.annotation.HttpMethod;
import com.youthlin.mvc.annotation.RequestBody;
import com.youthlin.mvc.annotation.URL;
import com.youthlin.mvc.support.converter.Converter;
import com.youthlin.mvc.view.jackson.JsonBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-13 13:39.
 */
@Controller
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;

    @URL(value = { "/", "/index" }, method = { HttpMethod.GET, HttpMethod.POST })
    public String list(Map<String, Object> map) {
        map.put("userList", userService.listUsers());
        return "list";
    }

    @URL("home")
    public String home() {
        return "forward:/";
    }

    @URL(value = "/add", method = HttpMethod.GET)
    public String addPage() {
        return "add";
    }

    @URL(value = "/add", method = HttpMethod.POST)
    public String addUser(User user, Map<String, Object> map) {
        if (user == null) {
            map.put("error", "参数错误");
            return "add";
        }
        if (user.getName() == null || user.getEmail() == null) {
            map.put("error", "用户名及电子邮件是必填项");
            return "add";
        }
        userService.saveUser(user);
        return "redirect:/";
    }

    //region  折叠
    @URL(value = "/edit", method = HttpMethod.GET)
    public String editPage(Long id, Map<String, Object> map) {
        User user = userService.findById(id);
        if (user == null) {
            map.put("error", "用户不存在");
            return "edit";
        }
        map.put("user", user);
        return "edit";
    }

    @URL(value = "/edit", method = HttpMethod.POST)
    public String editUser(Long id, Map<String, String> map) {
        User user = userService.findById(id);
        if (user == null) {
            map.put("error", "用户不存在");
            return "edit";
        }
        String name = map.get("name");
        String email = map.get("email");
        String note = map.get("note");
        if (name == null || email == null) {
            map.put("error", "用户名及电子邮件是必填项");
            return "add";
        }
        user.setName(name).setEmail(email).setNote(note);
        userService.editUser(user);
        return "redirect:/";
    }

    @URL("delete")
    public String delete(Long id) {
        if (userService.deleteById(id)) {
            LOGGER.debug("删除成功 用户ID: {}", id);
        } else {
            LOGGER.debug("删除失败 用户ID:{}", id);
        }
        return "redirect:/";
    }
    //endregion

    @URL("user")
    @JsonBody
    public Object test(@RequestBody User user, @ConvertWith(UserConverter.class) User user1) {
        return new Object[] { user, user1 };
    }

    public static class UserConverter implements Converter<User> {
        @Override
        public User convert(String from) {
            return new User().setEmail("xxx");
        }
    }
}
