// 为了不与其他文件命名冲突 所以用匿名函数包起来
(function () {
    // region util
    const CLICK = 'click';
    const id = function (id, parent) {
        if (parent === undefined) {
            parent = document;
        }
        return parent.getElementById(id);
    };
    const $ = function (selector) {
        return document.querySelector(selector);
    };
    const $$ = function (selectors) {
        return document.querySelectorAll(selectors);
    };
    // https://juejin.im/post/5a77f5115188257a6426775c
    const ajax = function (options) {
        let {url, method = 'GET', body} = options;
        return new Promise((resolve, reject) => {
            const request = new XMLHttpRequest();
            request.open(method, url);
            request.send(body);
            request.onload = () => resolve(request.responseText);
            request.onerror = () => reject(request);
        });
    };
    // http://youmightnotneedjquery.com/#parse_html
    const parseHTML = function (html) {
        const doc = document.implementation.createHTMLDocument();
        doc.documentElement.innerHTML = html;
        return doc;
    };
    const on = function (element, type, callback, options) {
        if (element === null || element === undefined) {
            return;
        }
        if (element.addEventListener) {
            element.addEventListener(type, callback, options);
        } else if (element.attachEvent) {
            element.attachEvent('on' + type, callback);
        } else {
            element['on' + type] = callback;
        }
    };
    const off = function (element, type, callback, options) {
        if (element.removeEventListener) {
            element.removeEventListener(type, callback, options);
        } else if (element.detachEvent) {
            element.detachEvent('on' + type, callback);
        } else {
            element['on' + type] = null;
        }
    };
    const ready = function (fn) {
        if (document.readyState !== 'loading') {
            fn();
        } else {
            on(document, 'DOMContentLoaded', fn);
        }
    };
    const debug = function (msg) {
        console.log(msg);
    };
    // endregion util

    ready(() => {
        // 代码高亮
        $$("pre").forEach(block => {
            hljs.highlightBlock(block);
            hljs.lineNumbersBlock(block);
            if (block.dataset.height) {
                block.style.maxHeight = block.dataset.height;
            }
            const action = document.createElement('a');
            action.innerHTML = '宽屏模式';
            action.classList.add('hljs-action');
            on(action, CLICK, () => {
                if (block.style.maxWidth !== '100vw') {
                    block.style.maxWidth = '100vw';
                    block.style.width = '100vw';
                    block.style.position = 'relative';
                    block.style.left = '-' + block.getBoundingClientRect().x + 'px';
                    action.innerHTML = '还原';
                } else {
                    action.innerHTML = '宽屏模式';
                    block.style.maxWidth = null;
                    block.style.width = null;
                    block.style.position = null;
                    block.style.left = null;
                }
            });
            block.style.marginTop = '0';
            block.insertAdjacentElement('beforebegin', action);
        });

        // 点击评论框表情插入表情文字到评论框
        on($('.comment-form-smilies'), CLICK, e => {
            if (e.target.tagName.toLowerCase() === 'img') {
                const smiley = e.target;
                const input = id('comment');
                // 两边必须要有空格才会转换为表情
                const text = ' ' + smiley.alt + ' ';
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
            }
        });

        // 默认勾选记住我
        const rememberMe = id('wp-comment-cookies-consent');
        if (rememberMe != null) {
            rememberMe.checked = true;
        }

        // region 评论 ajax 翻页
        const commentList = id('comment-list');// 评论列表wrap
        const comments = id('comments');// 评论区域锚点
        const respond = id('respond');// 回复
        const loading = id('comment-loading');// 加载中
        const cancelReply = id('cancel-comment-reply-link');// 取消回复
        const commentNavWraps = $$('.comment-nav-wrap');// 评论分页
        const topNav = id('comment-nav-wrap-top');//评论分页-上
        const bottomNav = id('comment-nav-wrap-bottom');//评论分页-下
        if (commentList !== null && commentNavWraps !== null) {
            commentNavWraps.forEach(wrap => on(wrap, CLICK, e => {
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
                            topNav.innerHTML = id('comment-nav-wrap-top', parsedHtml).innerHTML;
                            bottomNav.innerHTML = id('comment-nav-wrap-bottom', parsedHtml).innerHTML;
                            commentList.innerHTML = id('comment-list', parsedHtml).innerHTML;
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
        // endregion 评论 ajax 翻页

        document.oncopy = function () {
            let link = $('link[rel=shortlink]');
            if (link === null) {
                link = document.location.href;
            } else {
                link = link.href;
            }
            const selection = window.getSelection();
            let text = "<br><br>//来源: <a href='" + link + "'>" + link + "</a>";
            text = selection + text;
            let tmp = document.createElement('div');
            tmp.style.left = '-99999px';
            tmp.style.position = 'absolute';
            const bodyElement = document.getElementsByTagName('body')[0];
            bodyElement.appendChild(tmp);
            tmp.innerHTML = text;
            selection.selectAllChildren(tmp);
            window.setTimeout(() => {
                bodyElement.removeChild(tmp);
            }, 0);
        };

        // region 上下滑动
        const goTop = id('svg-go-top');
        const goBottom = id('svg-go-bottom');
        const goComments = id('svg-go-comments');
        const goReply = id('svg-go-reply');
        let flag;

        function clearFlag() {
            if (flag) {
                clearInterval(flag);
                flag = 0;
            }
        }

        on(goTop, CLICK, () => {
            window.scrollTo(0, 0);
            clearFlag();
        });
        on(goTop, 'mouseover', () => {
            if (!flag) {
                flag = setInterval(() => {
                    // https://zh.javascript.info/size-and-scroll-window#window-scroll
                    window.scrollBy(0, -10);
                }, 10);
            }
        });
        on(goTop, 'mouseout', () => {
            clearFlag();
        });
        on(goBottom, CLICK, () => {
            clearFlag();
            setTimeout(() => {
                id('colophon').scrollIntoView(false);
            }, 10);
        });
        on(goBottom, 'mouseover', () => {
            if (!flag) {
                flag = setInterval(() => {
                    window.scrollBy(0, 10);
                }, 10);
            }
        });
        on(goBottom, 'mouseout', () => {
            clearFlag();
        });
        if (goComments != null) {
            on(goComments, CLICK, () => {
                if (comments != null) {
                    comments.scrollIntoView(true);
                } else if (respond != null) {
                    respond.scrollIntoView(true);
                }
            });
        }
        if (goReply != null) {
            on(goReply, CLICK, () => {
                if (respond != null) {
                    respond.scrollIntoView(true);
                } else if (comments != null) {
                    comments.scrollIntoView(true);
                }
            });
        }
        // endregion 上下滑动

        // region ajax comment
        const commentForm = id('commentform');//评论表单
        const comment = id('comment');//输入框
        const submit = id('submit');//提交按钮
        const commentSubmitLoading = id('comment-submit-loading');//提交中
        if (commentForm != null) {
            let lastCommentTime = Date.now();

            function tooFast() {
                return Date.now() - lastCommentTime < 2000;
            }

            on(commentForm, 'submit', e => {
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
                formData.append('lin_comment_nonce', lin_ajax.lin_comment_nonce);
                commentSubmitLoading.style.display = 'flex';
                comment.disabled = true;
                submit.disabled = true;
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
                }).finally(() => {
                    comment.disabled = false;
                    submit.disabled = false;
                    commentSubmitLoading.style.display = 'none';
                });
                return false;
            });
        }
        // endregion ajax comment

        // 图片灯箱 https://github.com/feimosi/baguetteBox.js
        baguetteBox.run('.entry-content', {
            captions: function (a) {
                const parent = a.parentElement;
                if (parent.tagName.toLocaleLowerCase() === 'figure') {
                    const figCaption = parent.getElementsByTagName('figcaption')[0];
                    if (figCaption) {
                        return figCaption.innerText;
                    }
                }
                const img = a.getElementsByTagName('img')[0];
                if (img) {
                    if (img.alt) {
                        return img.alt;
                    }
                    return decodeURIComponent(img.src.substr(img.src.lastIndexOf('/') + 1));
                }
                return '';
            },
            noScrollbars: true,
            async: true,
        });

        $$('a[rel~=external],a[rel~=friend],a[rel~=noreferrer]')
            .forEach(a => a.target = '_blank');

    });
})();
