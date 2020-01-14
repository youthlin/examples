// 代码高亮
document.querySelectorAll("pre").forEach((block) => {
    hljs.highlightBlock(block);
    hljs.lineNumbersBlock(block);
    if (block.dataset.height) {
        block.style.maxHeight = block.dataset.height;
    }
});
// 评论框表情
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
