package com.youthlin.demo.test;

import com.youthlin.demo.service.HelloService;
import org.aspectj.weaver.reflect.InternalUseOnlyPointcutParser;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.ShadowMatch;

import java.lang.reflect.Method;

/**
 * 创建: youthlin.chen
 * 时间: 2018-04-26 15:16
 */
public class Main {
    public static void main(String[] args) {

        PointcutParser pointcutParser = new InternalUseOnlyPointcutParser(Main.class.getClassLoader());
        PointcutExpression expression = pointcutParser.parsePointcutExpression("execution(*  com.youthlin.demo.service.*.*(..))");
        System.out.println(expression);
        System.out.println(expression.couldMatchJoinPointsInType(HelloService.class));
        System.out.println(expression.couldMatchJoinPointsInType(Main.class));
        for (Method method : HelloService.class.getDeclaredMethods()) {
            ShadowMatch shadowMatch = expression.matchesMethodExecution(method);
            if (shadowMatch.matchesJoinPoint(null, null, null).matches()) {
                System.out.println("matches " + method);
            }
        }
    }
}
