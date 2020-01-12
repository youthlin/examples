<?php
add_action('wp_enqueue_scripts', 'my_theme_enqueue_styles');
function my_theme_enqueue_styles() {
    $theme_version = wp_get_theme()->get('Version');
    $parent_style = 'twentytwenty-style';
    
    wp_enqueue_style($parent_style, get_template_directory_uri() . '/style.css' );
    wp_enqueue_style('twentytwenty-child-style',
        get_stylesheet_directory_uri() . '/style.css',
        array($parent_style),
        $theme_version
    );

    wp_enqueue_style('highlight-style',
        'https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@9.17.1/build/styles/default.min.css');
}   

add_action('wp_footer', 'my_theme_enqueue_script_footer');
function my_theme_enqueue_script_footer(){
    wp_enqueue_script('highlight-js-main',
        'https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@9.17.1/build/highlight.min.js');
    wp_enqueue_script('highlight-js-linenumber',
        'https://cdn.jsdelivr.net/npm/highlightjs-line-numbers.js@2.7.0/dist/highlightjs-line-numbers.min.js');

    wp_add_inline_script('highlight-js-linenumber','document.querySelectorAll("pre").forEach((block) => {
        hljs.highlightBlock(block);
        hljs.lineNumbersBlock(block);
        if (block.dataset.height) {
            block.style.maxHeight = block.dataset.height;
        }
    });'
    );
}
