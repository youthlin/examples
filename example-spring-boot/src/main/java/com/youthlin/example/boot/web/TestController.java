package com.youthlin.example.boot.web;

import com.youthlin.example.boot.bean.ConfigBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author youthlin.chen
 * @date 2019-10-09 18:07
 */
@Controller
@RequestMapping("/test")
public class TestController {

    /**
     * bool 类型的可以传入 true/false 1/0 1=true 0=false
     * see org.springframework.core.convert.support.StringToBooleanConverter
     */
    @ResponseBody
    @RequestMapping("/add")
    public Object add(ConfigBean configBean) {
        return configBean;
    }

}
