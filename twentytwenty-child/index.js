//region util
// https://juejin.im/post/5a77f5115188257a6426775c
let ajax = function (options) {
    let {url, method = 'GET', body} = options;
    return new Promise(function (resolve, reject) {
        let request = new XMLHttpRequest();
        request.open(method, url);
        request.send(body);
        request.onreadystatechange = () => {
            if (request.readyState === 4) {
                if (request.status >= 200 && request.status < 300) {
                    resolve.call(undefined, request.responseText)
                } else if (request.status >= 400) {
                    reject.call(undefined, request)
                }
            }
        }
    });
};
// http://youmightnotneedjquery.com/#parse_html
const parseHTML = function (html) {
    const doc = document.implementation.createHTMLDocument();
    doc.documentElement.innerHTML = html;
    return doc;
};

// https://github.com/nefe/You-Dont-Need-jQuery/blob/master/README.zh-CN.md#2.3
function getOffset(el) {
    const box = el.getBoundingClientRect();
    return {
        top: box.top + window.pageYOffset - document.documentElement.clientTop,
        left: box.left + window.pageXOffset - document.documentElement.clientLeft
    };
}

//endregion util

// 代码高亮
document.querySelectorAll("pre").forEach(block => {
    hljs.highlightBlock(block);
    hljs.lineNumbersBlock(block);
    if (block.dataset.height) {
        block.style.maxHeight = block.dataset.height;
    }
});
// 点击评论框表情插入表情文字到评论框
document.addEventListener('DOMContentLoaded', function () {
    let smiley = document.querySelectorAll('.my-smiley');
    smiley.forEach(element => element.addEventListener('click', function (event) {
        let input = document.getElementById('comment');
        // 两边必须要有空格才会转换为表情
        let text = ' ' + element.alt + ' ';
        if (document.selection) {
            input.focus();
            let sel = document.selection.createRange();
            sel.text = text;
            input.focus()
        } else if (input.selectionStart || input.selectionStart === 0) {
            let startPos = input.selectionStart;
            let endPos = input.selectionEnd;
            let cursorPos = endPos;
            input.value = input.value.substring(0, startPos) + text + input.value.substring(endPos, input.value.length);
            cursorPos += text.length;
            input.focus();
            input.selectionStart = cursorPos;
            input.selectionEnd = cursorPos
        } else {
            input.value += text;
            input.focus()
        }
    }));
});

// 评论 ajax 翻页
const list = document.getElementById('comment-nav-list');
const comments = document.getElementById('comments');
const loading = document.getElementById('comment-loading');
const cancelReply = document.getElementById('cancel-comment-reply-link');
list.addEventListener('click', function (e) {
    const target = e.target;
    if (target.nodeName.toLowerCase() === 'a' && target.parentElement.parentElement === list) {
        e.preventDefault();
        if (cancelReply.style.display !== 'none') {
            cancelReply.click();
        }
        loading.style.display = 'flex';
        list.innerHTML = '';
        window.scrollTo(getOffset(comments));
        const href = target.href;
        ajax({url: href})
            .then(response => {
                const parsedHtml = parseHTML(response);
                list.innerHTML = parsedHtml.getElementById('comment-nav-list').innerHTML;
                loading.style.display = 'none';
                window.history.pushState(null, parsedHtml.title, href);
            })
            .catch(err => {
                console.log('error: ' + err);
                window.location = href;
            });
    }
});
