package com.youthlin.example.stream;

/**
 * @author youthlin.chen
 * @date 2019-07-19 09:51
 */
public interface TerminalAction<E_IN, OUT> extends Stage<E_IN> {

    <IN> OUT finish(Visitor<IN> in);

}
