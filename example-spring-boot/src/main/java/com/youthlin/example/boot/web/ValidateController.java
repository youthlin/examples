package com.youthlin.example.boot.web;

import com.youthlin.example.boot.bean.ConfigBean;
import com.youthlin.example.boot.bean.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author : youthlin.chen @ 2019-09-18 22:10
 */
@Controller
public class ValidateController {
    private static final Logger log = LoggerFactory.getLogger(ValidateController.class);

    @RequestMapping("add")
    @ResponseBody
    public Response add(@Validated ConfigBean configBean) {
        log.info("config bean={}", configBean);
        return new Response();
    }

}
