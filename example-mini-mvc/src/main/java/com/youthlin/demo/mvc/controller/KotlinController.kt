package com.youthlin.demo.mvc.controller

import com.youthlin.ioc.annotation.Controller
import com.youthlin.mvc.annotation.URL
import org.slf4j.LoggerFactory
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * 创建: youthlin.chen
 * 时间: 2018-03-15 19:36
 */
@Controller
@URL("kotlin")
class KotlinController {
    @Resource
    private var userController: UserController? = null

    @URL("hello")
    fun hello(map: MutableMap<String, Any>, request: HttpServletRequest?): String {
        LOGGER.info("map {}", map)
        LOGGER.info("request {}", request)
        return userController?.list(map) ?: "list"
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(KotlinController::class.java)
    }

}
