const nzd = require('node-zookeeper-dubbo');
const java = require('js-to-java');
const app = require('express')();

const opt = {
    application: {name: 'node-zookeeper-dubbo-example'},
    registry: '127.0.0.1:2181',
    dubboVer: '2.0.2',
    root: 'dubbo',
    dependencies: {
        HelloService: {
            interface: 'com.youthlin.example.rpc.api.service.HelloService',
            version: '0.0.1',
            timeout: 400,
            group: 'hello',
            // methodSignature: { // optional
            //     findById: (id) => [{'$class': 'java.lang.Long', '$': id}],
            //     findByName: (name) => [java.String(name)],
            // }
        }
    }
};
const Dubbo = new nzd(opt);

Dubbo.on("service:changed", (event) => console.log(event));

app.get('/hello/sayHello', (req, res) => {
    Dubbo.HelloService
        .sayHello(java.String('NZB'))
        .then(data => {
            console.log('ok: ' + data);
            res.send(data);
        })
        .catch(err => {
            console.log('fail: ' + err);
            res.send(err);
        })
});

app.listen(9090);
