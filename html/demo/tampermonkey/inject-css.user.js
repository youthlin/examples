// ==UserScript==
// @name         Inject CSS
// @name:zh      CSS 样式注入
// @name:zh-CN   CSS 样式注入
// @namespace    http://youthlin.com/
// @version      0.1
// @description  Inject custom css to site.
// @author       Youth．霖
// @match        *
// @include      *
// @grant        GM_getValue
// @grant        GM_setValue
// @grant        GM_addStyle
// @grant        GM_registerMenuCommand
// ==/UserScript==
(async function () {
    'use strict';
    const SETTING_KEY = 'cssMap'
    const debug = function (...args) {
        console.log(...args)
    }
    const on = function (eventName, selector, handler) {
        // http://youmightnotneedjquery.com/
        document.addEventListener(eventName, function (e) {
            // loop parent nodes from the target to the delegation node
            for (var target = e.target; target && target !== this; target = target.parentNode) {
                if (target.matches(selector)) {
                    handler.call(target, e);
                    break;
                }
            }
        }, false);
    }
    debug('Hello, Inject CSS.')

    async function start() {
        const cssMap = await cssValue()
        const rand = (Math.random() * 100000).toFixed(0)
        const html = document.getElementsByTagName('html')[0]
        // http://www.ruanyifeng.com/blog/2015/07/flex-grammar.html
        html.insertAdjacentHTML('beforeend', `<div id="inject-css-${rand}" 
style="display: none;position: absolute;left: 0;top: 0;right: 0;bottom: 0;z-index: 999; background-color: rgba(255,255,255,.9);justify-content: center;align-items: center;"></div>`)
        const wrapper = document.getElementById('inject-css-' + rand)
        GM_registerMenuCommand('Settings|设置', function (e) {
            debug('Settings', e)
            wrapper.style.display = 'flex'
        }, 's')
        await render(cssMap, wrapper)
        on('click', '.inject-css-delete', async function (e) {
            const deleteKey = e.target.dataset.key
            cssMap.delete(deleteKey)
            await save(cssMap)
            await render(cssMap, wrapper)
        })
        on('click', '.inject-css-add', async function (e) {
            e.preventDefault()
            const k = document.querySelector('#inject-css-url').value
            const v = document.querySelector('#inject-css-value').value
            if (k !== '') {
                cssMap.set(k, v)
                await save(cssMap)
                await render(cssMap, wrapper)
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

    async function cssValue() {
        const settingValue = await GM_getValue(SETTING_KEY, '{}');
        debug('setting value:', settingValue)
        let cssMap = JSON.parse(settingValue) // url regex -> css value
        cssMap = new Map(Object.entries(cssMap)) // to Map
        return cssMap
    }

    async function save(cssMap) {
        const s = JSON.stringify(Object.fromEntries(cssMap));
        await GM_setValue(SETTING_KEY, s)
        debug('saved', s, 'get:', await cssValue())
    }

    async function render(cssMap, wrapper) {
        const url = window.location.href
        debug('cssMap:', cssMap, 'url:', url)
        let injectCss = `#inject-css-setting table{
            border-collapse: collapse;
        }
        #inject-css-setting th, #inject-css-setting td{
            border: 1px solid #ccc;
            padding: .5em;
        }
        #inject-css-setting tr:nth-child(even){
            background-color: #f2f2f2
        }
        #inject-css-setting tr:hover{
            background-color: #ddd
        }
        #inject-css-setting .inject-css-delete,#inject-css-setting .inject-css-add{
            width: 100%;
            cursor: pointer;
        }
        `
        let tableBody = ''
        for (const entry of cssMap) {
            try {
                if (new RegExp(entry[0]).test(url)) {
                    injectCss += entry[1] + "\n"
                }
                tableBody += `<tr><td>${entry[0]}</td><td><pre>${entry[1]}</pre></td><td><button data-key="${entry[0]}" class="inject-css-delete">-</button></td></tr>`
            } catch (e) {
                debug('regexp error', e)
                cssMap.delete(entry[0])
            }
        }
        wrapper.innerHTML = `<style>${injectCss}</style><div id="inject-css-setting"><button class="inject-css-hide">关闭(Esc)</button>
<table><tbody><tr><th>URL 正则</th><th>注入 CSS</th><th>操作</th></tr>${tableBody}
<tr><td><input type="text" id="inject-css-url"></td><td><textarea id="inject-css-value" cols="30" rows="3"></textarea></td><td><button class="inject-css-add">+</button></td></tr>
</tbody></table></div>`
    }

    setTimeout(start, 2000)

})();
