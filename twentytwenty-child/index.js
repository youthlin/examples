// 为了不与其他文件命名冲突 所以用匿名函数包起来
(function () {
    // region util
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

    // endregion util

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
    const commentList = document.getElementById('comment-nav-list');//评论列表wrap
    const comments = document.getElementById('comments');//评论区域锚点
    const respond = document.getElementById('respond');//回复
    const loading = document.getElementById('comment-loading');//加载中
    const cancelReply = document.getElementById('cancel-comment-reply-link');//取消回复
    const commentNavWraps = document.querySelectorAll('.comment-nav-wrap');//评论分页
    const topNav = document.getElementById('comment-nav-wrap-top');//评论分页-上
    const bottomNav = document.getElementById('comment-nav-wrap-bottom');//评论分页-下
    if (commentList !== null && commentNavWraps !== null) {
        commentNavWraps.forEach(wrap => wrap.addEventListener('click', function (e) {
            const target = e.target;
            if (target.nodeName.toLowerCase() === 'a') {
                e.preventDefault();
                if (cancelReply.style.display !== 'none') {
                    cancelReply.click();
                }
                loading.style.display = 'flex';
                topNav.innerHTML = '';
                commentList.innerHTML = '';
                bottomNav.innerHTML = '';
                comments.scrollIntoView(true);
                const href = target.href;
                const data = new FormData();
                data.append('action', 'lin_list_comment');
                data.append('p', loading.dataset.post);
                data.append('c', href.match(/comment-page-([\d]*)/)[1]);
                ajax({url: lin_ajax.url, method: 'POST', body: data})
                    .then(response => {
                        const parsedHtml = parseHTML(response);
                        topNav.innerHTML = parsedHtml.getElementById('comment-nav-wrap-top').innerHTML;
                        bottomNav.innerHTML = parsedHtml.getElementById('comment-nav-wrap-bottom').innerHTML;
                        commentList.innerHTML = parsedHtml.getElementById('comment-nav-list').innerHTML;
                        loading.style.display = 'none';
                        window.history.pushState(null, parsedHtml.title, href);
                    })
                    .catch(err => {
                        console.log('error: ' + err);
                        window.location = href;
                    });
            }
        }));
    }

    document.oncopy = function () {
        let link = document.querySelector('link[rel=shortlink]');
        if (link === null) {
            link = document.location.href;
        } else {
            link = link.href;
        }
        let selection = window.getSelection();
        let text = "<br><br>//来源: <a href='" + link + "'>" + link + "</a>";
        text = selection + text;
        let tmp = document.createElement('div');
        tmp.style.left = '-99999px';
        tmp.style.position = 'absolute';
        const bodyElement = document.getElementsByTagName('body')[0];
        bodyElement.appendChild(tmp);
        tmp.innerHTML = text;
        selection.selectAllChildren(tmp);
        window.setTimeout(function () {
            bodyElement.removeChild(tmp);
        }, 0);
    };

    // region 上下滑动
    const goTop = document.getElementById('svg-go-top');
    const goBottom = document.getElementById('svg-go-bottom');
    const goComments = document.getElementById('svg-go-comments');
    const goReply = document.getElementById('svg-go-reply');
    let flag;

    function clearFlag() {
        if (flag) {
            clearInterval(flag);
            flag = 0;
        }
    }

    goTop.addEventListener('click', function () {
        clearFlag();
        window.scrollTo(0, 0);
    });
    goTop.addEventListener('mouseover', function () {
        if (!flag) {
            flag = setInterval(function () {
                // https://zh.javascript.info/size-and-scroll-window#window-scroll
                window.scrollBy(0, -10);
            }, 10);
        }
    });
    goTop.addEventListener('mouseout', function () {
        clearFlag();
    });
    goBottom.addEventListener('click', function () {
        clearFlag();
        document.getElementById('site-footer').scrollIntoView(false);
    });
    goBottom.addEventListener('mouseover', function () {
        if (!flag) {
            flag = setInterval(function () {
                window.scrollBy(0, 10);
            }, 10);
        }
    });
    goBottom.addEventListener('mouseout', function () {
        clearFlag();
    });
    if (goComments != null && comments != null) {
        goComments.addEventListener('click', function () {
            comments.scrollIntoView(true);
        });
    }
    if (goReply != null && respond != null) {
        goReply.addEventListener('click', function () {
            respond.scrollIntoView(true);
        });
    }
    // endregion 上下滑动

    // region ajax comment
    const commentForm = document.getElementById('commentform');//评论表单
    const comment = document.getElementById('comment');//输入框
    if (commentForm != null) {
        let lastCommentTime = Date.now();

        function tooFast() {
            return Date.now() - lastCommentTime < 2000;
        }

        commentForm.addEventListener('submit', function (e) {
            e.preventDefault();
            if (tooFast()) {
                return false;
            }
            lastCommentTime = Date.now();
            if (!commentForm.reportValidity()) {
                return false;
            }
            let formData = new FormData(commentForm);
            formData.append('action', 'lin_ajax_comment');
            ajax({
                url: lin_ajax.url,
                method: 'POST',
                body: formData
            }).then(response => {
                const parsed = parseHTML(response);
                const newComment = parsed.body.children[0];
                if (respond.parentElement.classList.contains('comment')) {
                    // 回复
                    respond.insertAdjacentElement('beforebegin', newComment);
                    cancelReply.click();
                } else {
                    // 不是回复
                    commentList.appendChild(newComment);
                }
                newComment.scrollIntoView(true);
                comment.value = '';
            }).catch(xhr => {
                alert('抱歉 出错了 请尝试刷新页面.\n' + parseHTML(xhr.responseText).documentElement.innerText);
            });
            return false;
        });
    }
    // endregion ajax comment

    baguetteBox.run('.entry-content', {
        captions: function (a) {
            const parent = a.parentElement;
            if (parent.tagName.toLocaleLowerCase() === 'figure') {
                const figCaption = parent.getElementsByTagName('figcaption')[0];
                if (figCaption) {
                    return figCaption.innerText;
                }
            }
            return true;
        },
        noScrollbars: true,
        async: true,
    });

})();
