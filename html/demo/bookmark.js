class BookMarkItem extends HTMLElement {
    constructor() {
        super();
        const shadow = this.attachShadow({ mode: 'open' });
        shadow.innerHTML = `
        <style></style>
        <div></div>
        `
    }
}

if (!customElements.get('bookmark-item')) {
    // 必须先定义才能在这里声明 不能提前 define
    customElements.define('bookmark-item', BookMarkItem)
}
