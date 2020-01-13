<?php
add_action('wp_enqueue_scripts', 'my_theme_enqueue_styles');
function my_theme_enqueue_styles() {
    $theme_version = wp_get_theme()->get('Version');
    $parent_style = 'twentytwenty-style';

    wp_enqueue_style($parent_style, get_template_directory_uri() . '/style.css');
    wp_enqueue_style('twentytwenty-child-style',
        get_stylesheet_directory_uri() . '/style.css',
        array($parent_style),
        $theme_version
    );

    wp_enqueue_style('highlight-style',
        'https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@9.17.1/build/styles/default.min.css');
}

add_action('wp_footer', 'my_theme_enqueue_script_footer');
function my_theme_enqueue_script_footer() {
    wp_enqueue_script('highlight-js-main',
        'https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@9.17.1/build/highlight.min.js');
    wp_enqueue_script('highlight-js-linenumber',
        'https://cdn.jsdelivr.net/npm/highlightjs-line-numbers.js@2.7.0/dist/highlightjs-line-numbers.min.js');

    wp_add_inline_script('highlight-js-linenumber', 'document.querySelectorAll("pre").forEach((block) => {
        hljs.highlightBlock(block);
        hljs.lineNumbersBlock(block);
        if (block.dataset.height) {
            block.style.maxHeight = block.dataset.height;
        }
    });'
    );
}

class My_Widget_Recent_Comments extends WP_Widget_Recent_Comments {
    public function __construct() {
        $widget_ops = array(
            'classname' => 'my_widget_recent_comments',
            'description' => __('近期评论(带头像)'),
        );
        $this->WP_Widget('my-recent-comments', __('近期评论(带头像)'), $widget_ops);
    }

    /**
     * 输出近期评论
     *
     * @param array $args Display arguments including 'before_title', 'after_title',
     *                        'before_widget', and 'after_widget'.
     * @param array $instance 当前挂件的设置数据
     * @since 2.8.0
     *
     */
    public function widget($args, $instance) {
        if (!isset($args['widget_id'])) {
            $args['widget_id'] = $this->id;
        }

        $output = '';

        $title = (!empty($instance['title'])) ? $instance['title'] : __('Recent Comments');

        /** This filter is documented in wp-includes/widgets/class-wp-widget-pages.php */
        $title = apply_filters('widget_title', $title, $instance, $this->id_base);

        $number = (!empty($instance['number'])) ? absint($instance['number']) : 5;
        if (!$number) {
            $number = 5;
        }

        /**
         * Filters the arguments for the Recent Comments widget.
         *
         * @param array $comment_args An array of arguments used to retrieve the recent comments.
         * @param array $instance Array of settings for the current widget.
         * @see WP_Comment_Query::query() for information on accepted arguments.
         *
         * @since 3.4.0
         * @since 4.9.0 Added the `$instance` parameter.
         *
         */
        $comments = get_comments(
            apply_filters(
                'widget_comments_args',
                array(
                    'number' => $number,
                    'status' => 'approve',
                    'post_status' => 'publish',
                    'type' => 'comment',    // 仅显示评论 不包括 ping-back
                    'user_id' => 0,         // 不显示作者评论
                ),
                $instance
            )
        );

        $output .= $args['before_widget'];
        if ($title) {
            $output .= $args['before_title'] . $title . $args['after_title'];
        }

        $output .= '<ul id="my-recent-comments">';
        if (is_array($comments) && $comments) {
            // Prime cache for associated posts. (Prime post term cache if we need it for permalinks.)
            $post_ids = array_unique(wp_list_pluck($comments, 'comment_post_ID'));
            _prime_post_caches($post_ids, strpos(get_option('permalink_structure'), '%category%'), false);

            foreach ((array)$comments as $comment) {
                $avatar = get_avatar($comment, 56, '', '头像');
                $output .= '<li class="recentcomments">';
                $output .=
                    '<div class="comment-avatar">' . $avatar . '</div>'
                    . '<div class="comment-content">'
                    . '  <div class="line line-1" title="' . htmlentities($comment->comment_content) . '">'
                    . mb_strimwidth(strip_tags($comment->comment_content), 0, 50, '...') . '</div>'
                    . '  <div class="line line-2">' . sprintf(
                    /* translators: Comments widget. 1: Comment author, 2: Post link. */
                        _x('%1$s on %2$s', 'widgets'),
                        get_comment_author_link($comment),
                        '<a href="' . esc_url(get_comment_link($comment)) . '">' . get_the_title($comment->comment_post_ID) . '</a>')
                    . '</div></div>';
                $output .= '</li>';
            }
        }
        $output .= '</ul>';
        $output .= $args['after_widget'];

        echo $output;
    }

}

register_widget('My_Widget_Recent_Comments');
