import {argumentMap, JavaString} from 'interpret-util';
import {TDubboCallResult, Dubbo} from 'dubbo2.js';

export interface IHelloService {
  sayHello(String0: JavaString): TDubboCallResult<string>;
}

export const HelloServiceWrapper = {sayHello: argumentMap};

export function HelloService(dubbo: Dubbo): IHelloService {
  return dubbo.proxyService<IHelloService>({
    dubboInterface: 'com.youthlin.example.rpc.api.service.HelloService',
    methods: HelloServiceWrapper,
  });
}

//generate by interpret-cli dubbo2.js
