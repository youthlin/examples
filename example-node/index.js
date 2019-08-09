'use strict';
let http = require('http'),
    fs = require('fs'),
    url = require('url'),
    path = require('path');

// 从命令行参数获取root目录，默认是当前目录:
const root = path.resolve(process.argv[2] || '.');
console.log('Static root dir: ' + root);

let server = http.createServer((request, response) => {
    try {
        // 获得URL的path，类似 '/css/bootstrap.css':
        const pathname = decodeURI(url.parse(request.url).pathname);
        // 获得对应的本地文件路径，类似 '/srv/www/css/bootstrap.css':
        const filepath = path.join(root, pathname);
        // 获取文件状态:
        fs.stat(filepath, (err, stats) => {
            if (err) {
                notFound(request, response, err);
                return;
            }
            if (stats.isFile()) {
                console.log('200 ' + request.url);
                if (filepath.endsWith('.html')) {
                    response.setHeader('Content-Type', 'text/html;charset=UTF-8');
                }
                if (filepath.endsWith('.js')) {
                    response.setHeader('Content-Type', 'text/javascript;charset=UTF-8');
                }
                if (filepath.endsWith('.css')) {
                    response.setHeader('Content-Type', 'text/css;charset=UTF-8');
                }
                if (filepath.endsWith('.json')) {
                    response.setHeader('Content-Type', 'application/json;charset=UTF-8');
                }
                response.writeHead(200);
                // 将文件流导向response:
                fs.createReadStream(filepath).pipe(response);
                return;
            }
            if (stats.isDirectory()) {
                fs.readdir(filepath, (err, files) => {
                    if (err) {
                        notFound(request, response, err);
                        return;
                    }
                    console.log('200 ' + request.url);
                    const list = files.map(file => {
                            let href;
                            if (pathname.endsWith('/')) {
                                href = pathname + file;
                            } else {
                                href = pathname + '/' + file;
                            }
                            if (isDir(path.join(filepath, file))) {
                                href += '/';
                                file += '/';
                            }
                            return '<li><a href="' + href + '">'
                                + file + '</a></li>';
                        }
                    ).join('\r\n');
                    response.writeHead(200);
                    response.write('<!DOCTYPE html>' +
                        '<html lang="zh-CN">' +
                        '<head><meta charset="UTF-8"><title>Index of ' + pathname + '</title></head>' +
                        '<body>' +
                        '<h2>' + filepath + '</h2>' +
                        '<ul>' +
                        '<li><a href=".">刷新</a></li>\r\n' +
                        '<li><a href="..">上级</a></li>\r\n' +
                        '<li><a href="/">根目录</a></li>\r\n' +
                        list + '</ul></body>' +
                        '</html>');
                    response.end('\r\n');
                });
            }
        });
    } catch (e) {
        console.error(e);
        response.writeHead(500);
        response.end('Server Error');
    }
});

server.listen(8099, '0.0.0.0');
console.log('Server is running at http://0.0.0.0:8099/');

function isDir(file) {
    try {
        let stat = fs.statSync(file);
        return stat.isDirectory();
    } catch (e) {
        return false;
    }
}

function notFound(request, response, err) {
    console.log('404 ' + request.url + 'err:' + err);
    // 发送404响应:
    response.writeHead(404);
    const pathname = decodeURI(url.parse(request.url).pathname);
    const filepath = path.join(root, pathname);
    response.write('<!DOCTYPE html>' +
        '<html lang="zh-CN">' +
        '<head><meta charset="UTF-8"><title>Index of ' + pathname + '</title></head>' +
        '<body>' +
        '<h2>' + filepath + '</h2>' +
        '<ul>' +
        '<li><a href=".">刷新</a></li>\r\n' +
        '<li><a href="..">上级</a></li>\r\n' +
        '<li><a href="/">根目录</a></li>\r\n' +
        '</ul>' +
        '<strong>404 Not Found: ' + err + '</strong>' +
        '</body>' +
        '</html>');
    response.end('\r\n');
}
