// ==UserScript==
// @name         Inject CSS
// @name:zh      CSS 样式注入
// @namespace    https://youthlin.com/
// @version      0.1
// @description  Inject custom css to site.
// @description:zh  插入自定义 CSS 样式到任意网址
// @author       Youth．霖
// @match        *
// @include      *
// @grant        GM_getValue
// @grant        GM_setValue
// @grant        GM_addStyle
// @grant        GM_registerMenuCommand
// ==/UserScript==
(function () {
    'use strict';
    const SETTING_KEY = 'cssMap'
    const RAND = (Math.random() * 100000).toFixed(0)
    const debug = function (...args) {
        if (GM_getValue('debug', 'false') === 'true') {
            console.log(...args)
        }
    }
    const on = function (eventName, selector, handler) {
        // http://youmightnotneedjquery.com/
        document.addEventListener(eventName, function (e) {
            // loop parent nodes from the target to the delegation node
            for (let target = e.target; target && target !== this; target = target.parentNode) {
                if (target.matches(selector)) {
                    handler.call(target, e);
                    break;
                }
            }
        }, false);
    }
    debug('Hello, Inject CSS.')

    function start() {
        const cssMap = cssValue()
        const html = document.getElementsByTagName('html')[0]
        html.insertAdjacentHTML('beforeend', `<div id="inject-css-${RAND}"></div>`)
        const wrapper = document.getElementById(`inject-css-${RAND}`)
        GM_registerMenuCommand('Settings|设置', function (e) {
            debug('Settings', e)
            wrapper.style.display = 'flex'
        }, 's')
        render(cssMap, wrapper)
        on('click', '.inject-css-delete', function (e) {
            const deleteKey = e.target.dataset.key
            cssMap.delete(deleteKey)
            save(cssMap)
            render(cssMap, wrapper)
        })
        on('click', '.inject-css-add', function (e) {
            e.preventDefault()
            const k = document.querySelector('#inject-css-url').value.trim()
            const v = document.querySelector('#inject-css-value').value.trim()
            if (k !== '' && v !== '') {
                try {
                    new RegExp(k)
                } catch (e) {
                    alert('`' + k + '`: 不是有效的正则表达式. error=' + e)
                    return
                }
                cssMap.set(k, v)
                save(cssMap)
                render(cssMap, wrapper)
            }
        })
        on('click', '.inject-css-hide', function () {
            wrapper.style.display = 'none'
        })
        on('keyup', 'html', function (e) {
                if (e.code === 'Escape') {
                    wrapper.style.display = 'none'
                }
            }
        )
    }

    function cssValue() {
        const settingValue = GM_getValue(SETTING_KEY, '{}');
        debug('setting value:', settingValue)
        let cssMap = JSON.parse(settingValue) // url regex -> css value
        cssMap = new Map(Object.entries(cssMap)) // to Map
        return cssMap
    }

    function save(cssMap) {
        const s = JSON.stringify(Object.fromEntries(cssMap));// Map 需要先转为 Object 才能序列化为 JSON
        GM_setValue(SETTING_KEY, s)
        debug('saved', s, 'get:', cssValue())
    }

    function render(cssMap, wrapper) {
        const url = window.location.href
        debug('cssMap:', cssMap, 'url:', url)
        // http://www.ruanyifeng.com/blog/2015/07/flex-grammar.html
        let injectCss = `#inject-css-${RAND} {
            display: none;
            position: fixed;
            left: 0;
            top: 0;
            right: 0;
            bottom: 0;
            z-index: 999; 
            background-color: rgba(0,0,0,.8);
            justify-content: center;
            align-items: center;
            font-size: 16px;
        }
        #inject-css-setting {
            max-height: 100%;
            overflow: auto;
            color: #000;
        }
        #inject-css-setting button {
            border: 1px solid #ccc;
            background-color: #fff;
            cursor: pointer;
            padding: .5em;
            border-top-left-radius: 5px;
            border-top-right-radius: 5px;
        }
        #inject-css-setting table {
            border-collapse: collapse;
            background-color: #fff;
        }
        #inject-css-setting th, #inject-css-setting td {
            border: 1px solid #ccc;
            padding: .5em;
        }
        #inject-css-setting tr.active {
            font-weight: bold; 
        }
        #inject-css-setting td.active:after {
            content: ' ⭐️'
        }
        #inject-css-setting tr:nth-child(even) {
            background-color: #f2f2f2
        }
        #inject-css-setting tr:hover {
            background-color: #ddd
        }
        #inject-css-setting .inject-css-delete,#inject-css-setting .inject-css-add {
            width: 100%;
            border-radius: 5px;
        }
        `
        let tableBody = ''
        for (const entry of cssMap) {
            try {
                let active = ''
                if (new RegExp(entry[0]).test(url)) {
                    injectCss += entry[1] + "\n"
                    active = 'active'
                }
                tableBody += `<tr class="${active}"><td class="${active}"><code>${entry[0]}</code></td><td><pre>${entry[1]}</pre></td><td><button data-key="${entry[0]}" class="inject-css-delete">-</button></td></tr>`
            } catch (e) {
                debug('regexp error', e)
                cssMap.delete(entry[0])
            }
        }
        wrapper.innerHTML = `<style>${injectCss}</style><div id="inject-css-setting"><button class="inject-css-hide">关闭(Esc)</button>
<table><tbody><tr><th>URL 正则</th><th>注入 CSS</th><th>操作</th></tr>
${tableBody}
<tr><td><label><input type="text" id="inject-css-url" placeholder="点号斜线记得转义"></label></td>
<td><textarea id="inject-css-value" cols="30" rows="3" placeholder="html { background-color: #ccc; }"></textarea></td>
<td><button class="inject-css-add">+</button></td></tr>
</tbody></table></div>`
    }

    setTimeout(start, 2000)

})();
