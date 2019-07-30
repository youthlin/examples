import {Dubbo, java, setting} from "dubbo2.js";
import service from "./service";

//setting
const dubboSetting = setting
    .match(
        [
            'com.youthlin.example.rpc.api.service.HelloService',
        ],
        {
            version: '0.0.0',
            //group: '',
            timeout: 500
        },
    );

// create dubbo object
const dubbo = new Dubbo<typeof service>({
    application: {name: 'node-dubbo'},
    // zookeeper address
    register: '127.0.0.1:2181',
    service,
    dubboSetting
});

//main
(async () => {
    let {res, err} = await dubbo.service.HelloService.sayHello(java.String('NodeDubboExample'));

    console.log(JSON.stringify({res, err}));

})();
