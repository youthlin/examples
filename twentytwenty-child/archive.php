<?php get_header(); ?>
    <main id="site-content" role="main">
        <?php
        $archive_title = get_the_archive_title();
        $archive_subtitle = get_the_archive_description();
        ?>
        <header class="archive-header has-text-align-center header-footer-group">
            <div class="archive-header-inner section-inner medium">
                <?php if ($archive_title) { ?>
                    <h1 class="archive-title"><?php echo wp_kses_post($archive_title); ?></h1>
                <?php } ?>
                <?php if ($archive_subtitle) { ?>
                    <div class="archive-subtitle section-inner thin max-percentage intro-text">
                        <?php echo wp_kses_post(wpautop($archive_subtitle)); ?>
                    </div>
                <?php } ?>
            </div>
        </header>
        <article <?php post_class(); ?> id="post-<?php the_ID(); ?>">
            <div class="post-inner">
                <div class="entry-content archive-wrap">
                    <?php
                    add_filter('the_title', 'title_with_date', 10, 2);
                    function title_with_date($title, $id) {
                        global $post;
                        $post = get_post($id);
                        return $title . get_the_time('Y-m-d');
                    }

                    add_filter('get_archives_link', 're_format_link', 10, 7);
                    function re_format_link($link_html, $url, $text, $format, $before, $after, $selected) {
                        $date = substr($text, -10);
                        $text = substr($text, 0, strlen($text) - 10);
                        $link_html = "\t<li class='archive-item alignfull'>$before<span class='date meta-text'>$date</span> <a href='$url'>$text</a>$after</li>\n";
                        return $link_html;
                    }

                    wp_get_archives(array('type' => 'postbypost'));
                    ?>
                </div>
            </div>
        </article>
    </main>
<?php get_footer();
