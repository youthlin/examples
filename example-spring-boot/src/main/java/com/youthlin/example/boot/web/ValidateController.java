package com.youthlin.example.boot.web;

import com.youthlin.example.boot.bean.ConfigBean;
import com.youthlin.example.boot.bean.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author : youthlin.chen @ 2019-09-18 22:10
 */
@Controller
@Slf4j
public class ValidateController {

    @RequestMapping("add")
    @ResponseBody
    public Response add(@Validated ConfigBean configBean) {
        log.info("config bean={}", configBean);
        return new Response();
    }

}
