# node - dubbo

- 准备好 Dubbo 接口的 Java API `mvn clean && mvn package && mvn install dependency:copy-dependencies`
- 新建文件夹然后 `npm init` 初始化一个 node 项目
- 新建文件 dubbo.json
  ```json
  {
    "output": "./ts/providers",
    "entry": "com.youthlin.example.rpc.api.service",
    "entryJarPath": "../../../../example-dubbo-api/target/example-dubbo-api-1.0-SNAPSHOT.jar",
    "libDirPath": "../../../../example-dubbo-api/target/dependency",
    "providerSuffix": "Service"
  }
  ```
  - output: typescript 输出目录
  - entry: Dubbo 接口类全限定名前缀
  - entryJarPath: jar 路径
  - libDirPath: 依赖包文件夹 Maven项目通常是 target/dependency
  - providerSuffix: Dubbo 接口类名后缀
- `tsc --init`
   在 生成的 tsconfig.json 中 修改:
   ```json
    {
         "rootDir": "./ts",
         "outDir": "./js"  
    }
   ```
- `npm install interpret-dubbo2js -g` 安装 Java to Typescript 翻译师
- `npm install dubbo2.js interpret-util js-to-java @types/node @types/js-to-java` 添加依赖及其 ts 类型
- `interpret -c dubbo.json` 生成 Dubbo 接口的 typescript 文件
- 将 Dubbo 接口的 ts 类放在 service.ts 中，如
   ```typescript
    import {HelloService} from "./providers/com/youthlin/example/rpc/api/service/HelloService";

    export default {
        HelloService
    }
   ```
- 准备工作完成。在主文件中使用方式
  - dubbo settings
    ```typescript
    const dubboSetting = setting
        .match(
            [
                'com.youthlin.example.rpc.api.service.HelloService',
                // 有几个接口就写几个 如果 version, group, timeout 一致的话可以写在一起
                // 不一致的话 可以多写几个 match()
            ],
            {
                version: '0.0.0',
                //group: '',
                timeout: 500
            },
        );
    ```
  - dubbo 实例
    ```typescript
    const dubbo = new Dubbo<typeof service>({
        application: {name: 'node-dubbo'},
        // zookeeper address
        register: '127.0.0.1:2181',
        service,
        dubboSetting
    });
    ```
  - 接口调用
    ```typescript
    (async () => {
        let {res, err} = await dubbo.service.HelloService.sayHello(java.String('NodeDubboExample'));
    
        console.log(JSON.stringify({res, err}));
    
    })();

    ```
    JavaString 这些是 [js-to-java](https://github.com/node-modules/js-to-java) 的类
