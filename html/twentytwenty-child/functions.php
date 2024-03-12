<?php
// 子主题 functions 先于父主题加载

//region 样式与脚本

// region remove action
// 移除这两个 不影响引用文章的卡片样式
// 移除 wp-json 链接
remove_action( 'wp_head', 'rest_output_link_wp_head', 10 );
// 移除 oembed 链接
remove_action( 'wp_head', 'wp_oembed_add_discovery_links', 10 );

// removes EditURI/RSD (Really Simple Discovery) link.
remove_action( 'wp_head', 'rsd_link' );
// removes wlwmanifest (Windows Live Writer) link.
remove_action( 'wp_head', 'wlwmanifest_link' );

// 隐藏版本
// html 和 feed 里隐藏版本
add_filter( 'the_generator', '__return_false' );
// js css 链接隐藏版本
add_filter( 'style_loader_src', 'remove_version_from_style_js' );
add_filter( 'script_loader_src', 'remove_version_from_style_js' );
function remove_version_from_style_js( $src ) {
	return str_replace( 'ver=' . get_bloginfo( 'version' ), 'ver=' . date( 'Ym' ), $src );
}

// all actions related to emojis
remove_action( 'admin_print_styles', 'print_emoji_styles' );
remove_action( 'wp_head', 'print_emoji_detection_script', 7 );
remove_action( 'admin_print_scripts', 'print_emoji_detection_script' );
remove_action( 'wp_print_styles', 'print_emoji_styles' );
remove_filter( 'wp_mail', 'wp_staticize_emoji_for_email' );
remove_filter( 'the_content_feed', 'wp_staticize_emoji' );
remove_filter( 'comment_text_rss', 'wp_staticize_emoji' );
add_filter( 'emoji_svg_url', '__return_false' );
// endregion remove action

add_action( 'wp_head', 'lin_wp_head' );
function lin_wp_head() {
	?>
    <script>
        const darkQuery = window.matchMedia('(prefers-color-scheme: dark)');

        function themeSwitch(theme) {
            console.log(`theme=${theme},sys=${darkQuery.matches}`);
            if (theme === 'dark' || (theme === 'auto' && darkQuery.matches)) {
                document.getElementById('twentytwenty-child-dark-css').disabled = false;
                document.getElementById('highlight-dark-css').disabled = false;
            } else {
                document.getElementById('twentytwenty-child-dark-css').disabled = 'disabled';
                document.getElementById('highlight-dark-css').disabled = 'disabled';
            }
        }

        let theme = 'auto';
        if (window.localStorage) {
            // 看上次是否有保存
            const saved = localStorage.getItem('theme');
            if (saved !== null) {
                theme = saved;
            }
        }
        themeSwitch(theme);
    </script>
	<?php
	// 评论页面不要收录
	if ( is_single() || is_page() ) {
		if ( function_exists( 'get_query_var' ) ) {
			$cpage       = intval( get_query_var( 'cpage' ) );
			$commentPage = intval( get_query_var( 'comment-page' ) );
		}
		if ( ! empty( $cpage ) || ! empty( $commentPage ) ) {
			echo '<meta name="robots" content="noindex, nofollow"/>' . "\n";
		}
	}
}

add_action( 'wp_enqueue_scripts', 'lin_enqueue_styles' );
function lin_enqueue_styles() {
	// 修复 IE 跳转到内容的好像
	remove_action( 'wp_print_footer_scripts', 'twentytwenty_skip_link_focus_fix' );
	// 移除 wp-embed.min.js 移除的话引用文章没有卡片样式
	// 实际是 iframe 的默认 style 不显示，这个文件会去掉默认 style 并调整高度
	// 但引用文章的话 src 是 '永久链接/embed' 背景是白色的 和暗色主题不调 所以禁用吧
	wp_deregister_script( 'wp-embed' );

	// remove wp-block-library-css /wp-includes/css/dist/block-library/style.css
	wp_dequeue_style( 'wp-block-library' );

	$theme_version = wp_get_theme()->get( 'Version' );
	$parent_style  = 'twentytwenty-style';

	wp_enqueue_style( $parent_style, get_template_directory_uri() . '/style.css' );
	wp_enqueue_style( 'twentytwenty-child', get_stylesheet_directory_uri() . '/style.css',
		array( $parent_style ), $theme_version );
	wp_enqueue_style( 'twentytwenty-child-dark', get_stylesheet_directory_uri() . '/style-dark.css',
		array( $parent_style ), $theme_version );

	wp_enqueue_style( 'highlight',
		'https://cdn.bootcss.com/highlight.js/9.18.1/styles/default.min.css', array(), null );
	wp_enqueue_style( 'highlight-dark',
		'https://cdn.bootcss.com/highlight.js/9.18.1/styles/tomorrow-night.min.css', array(), null );
	wp_enqueue_style( 'baguetteBox',
		'https://cdn.bootcss.com/baguettebox.js/1.11.1/baguetteBox.min.css', array(), null );
	wp_enqueue_script( 'google-adsense', 'https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js', array(), null );
	wp_script_add_data( 'google-adsense', 'async', true );
	wp_script_add_data( 'google-adsense', 'data-keys', array( 'data-ad-client' ) );
	wp_script_add_data( 'google-adsense', 'data-ad-client', 'ca-pub-2099119617202841' );
}

add_action( 'wp_footer', 'lin_wp_footer' );
function lin_wp_footer() {
	lin_theme_switch();
	lin_top_bottom_nav();
	wp_enqueue_script( 'baguetteBox-js',
		'https://cdn.bootcss.com/baguettebox.js/1.11.1/baguetteBox.min.js', array(), null );
	wp_enqueue_script( 'highlight-js-main',
		'https://cdn.bootcss.com/highlight.js/9.18.1/highlight.min.js', array(), null );
	wp_enqueue_script( 'highlight-js-linenumber',
		'https://cdn.bootcss.com/highlightjs-line-numbers.js/2.7.0/highlightjs-line-numbers.min.js', array(), null );
	// 在 baguetteBox, highlight 之后
	wp_enqueue_script( 'twentytwenty-child-js', get_stylesheet_directory_uri() . '/index.js',
		array(), wp_get_theme()->get( 'Version' ), true );
	wp_enqueue_script( 'gtag', 'https://www.googletagmanager.com/gtag/js?id=UA-46211856-1', array(), null );
	wp_script_add_data( 'gtag', 'async', true );
	wp_add_inline_script( 'gtag', 'window.dataLayer = window.dataLayer || [];
function gtag(){dataLayer.push(arguments);}
gtag("js", new Date());
gtag("config", "UA-46211856-1");' );
}

add_filter( 'script_loader_tag', 'lin_add_data_to_script', 10, 2 );
function lin_add_data_to_script( $tag, $handle ) {
	$keys = wp_scripts()->get_data( $handle, "data-keys" );
	if ( ! $keys ) {
		return $tag;
	}
	foreach ( $keys as $attr ) {
		$v = wp_scripts()->get_data( $handle, $attr );
		if ( ! $v ) {
			$v = '';
		} else {
			$v = "='$v'";
		}
		// Prevent adding attribute when already added in #12009.
		if ( ! preg_match( ":\s$attr(=|>|\s):", $tag ) ) {
			$tag = preg_replace( ':(?=></script>):', " $attr$v", $tag, 1 );
		}
	}

	return $tag;
}

function lin_theme_switch() {
	?>
    <div id="theme-switch">
        <button class="theme auto" data-theme="auto">跟随系统</button>
        <button class="theme light" data-theme="light">浅色</button>
        <button class="theme dark" data-theme="dark">深色</button>
    </div>
	<?php
}

function lin_top_bottom_nav() {
	$hasComments  = false;
	$commentsOpen = false;
	if ( ( is_single() || is_page() ) && ( comments_open() || get_comments_number() ) && ! post_password_required() ) {
		$hasComments = true;
		if ( comments_open() ) {
			$commentsOpen = true;
		}
	}
	?>
    <div id="svg-nav">
        <!-- https://icons.bootcss.com/ -->
        <svg id="svg-go-top" viewBox="0 0 16 16" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
            <path d="M7.646 4.646a.5.5 0 01.708 0l6 6a.5.5 0 01-.708.708L8 5.707l-5.646 5.647a.5.5 0 01-.708-.708l6-6z"/>
        </svg>
		<?php if ( $hasComments ) { ?>
            <svg id="svg-go-comments" viewBox="0 0 16 16" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
                <path d="M5 11.5a.5.5 0 01.5-.5h9a.5.5 0 010 1h-9a.5.5 0 01-.5-.5zm0-4a.5.5 0 01.5-.5h9a.5.5 0 010 1h-9a.5.5 0 01-.5-.5zm0-4a.5.5 0 01.5-.5h9a.5.5 0 010 1h-9a.5.5 0 01-.5-.5zm-3 1a1 1 0 100-2 1 1 0 000 2zm0 4a1 1 0 100-2 1 1 0 000 2zm0 4a1 1 0 100-2 1 1 0 000 2z"/>
            </svg>
		<?php } else {
			echo "<svg></svg>\n";// 用空的占个高度
		}
		if ( $commentsOpen ) { ?>
            <svg id="svg-go-reply" viewBox="0 0 16 16" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
                <path fill-rule="evenodd" clip-rule="evenodd"
                      d="M14 1H2a1 1 0 00-1 1v8a1 1 0 001 1h2.5a2 2 0 011.6.8L8 14.333 9.9 11.8a2 2 0 011.6-.8H14a1 1 0 001-1V2a1 1 0 00-1-1zM2 0a2 2 0 00-2 2v8a2 2 0 002 2h2.5a1 1 0 01.8.4l1.9 2.533a1 1 0 001.6 0l1.9-2.533a1 1 0 01.8-.4H14a2 2 0 002-2V2a2 2 0 00-2-2H2z"/>
            </svg>
		<?php } ?>
        <svg id="svg-go-bottom" viewBox="0 0 16 16" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
            <path d="M1.646 4.646a.5.5 0 01.708 0L8 10.293l5.646-5.647a.5.5 0 01.708.708l-6 6a.5.5 0 01-.708 0l-6-6a.5.5 0 010-.708z"/>
        </svg>
    </div>
	<?php
}

function lin_svg_loading() {
	echo lin_get_svg_loading();
}

function lin_get_svg_loading() {
	// <!-- https://youthlin.com/demo/marked.html -->
	return '<svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="24px"
         height="30px" viewBox="0 0 24 30" xml:space="preserve">
                    <rect x="0" y="10" width="4" height="10" fill="#cd2653" opacity="0.2">
                        <animate attributeName="opacity" attributeType="XML" values="0.2; 1; .2" begin="0s" dur="0.6s"
                                 repeatCount="indefinite"></animate>
                        <animate attributeName="height" attributeType="XML" values="10; 20; 10" begin="0s" dur="0.6s"
                                 repeatCount="indefinite"></animate>
                        <animate attributeName="y" attributeType="XML" values="10; 5; 10" begin="0s" dur="0.6s"
                                 repeatCount="indefinite"></animate>
                    </rect>
        <rect x="8" y="10" width="4" height="10" fill="#cd2653" opacity="0.2">
            <animate attributeName="opacity" attributeType="XML" values="0.2; 1; .2" begin="0.15s"
                     dur="0.6s" repeatCount="indefinite"></animate>
            <animate attributeName="height" attributeType="XML" values="10; 20; 10" begin="0.15s" dur="0.6s"
                     repeatCount="indefinite"></animate>
            <animate attributeName="y" attributeType="XML" values="10; 5; 10" begin="0.15s" dur="0.6s"
                     repeatCount="indefinite"></animate>
        </rect>
        <rect x="16" y="10" width="4" height="10" fill="#cd2653" opacity="0.2">
            <animate attributeName="opacity" attributeType="XML" values="0.2; 1; .2" begin="0.3s" dur="0.6s"
                     repeatCount="indefinite"></animate>
            <animate attributeName="height" attributeType="XML" values="10; 20; 10" begin="0.3s" dur="0.6s"
                     repeatCount="indefinite"></animate>
            <animate attributeName="y" attributeType="XML" values="10; 5; 10" begin="0.3s" dur="0.6s"
                     repeatCount="indefinite"></animate>
        </rect>
    </svg>';
}

add_action( 'admin_enqueue_scripts', 'lin_admin_style' );
function lin_admin_style() {
	wp_enqueue_style( 'twentytwenty-child-admin', get_stylesheet_directory_uri() . '/style-admin.css',
		null, wp_get_theme()->get( 'Version' ) );
}

add_action( 'all_admin_notices', 'lin_admin_notice' );
function lin_admin_notice() {
	?>
    <div class="update-nag"><a href="javascript:void(0);" id="dark-switch">DarkMode</a></div>
    <script>
        const darkCss = document.getElementById('twentytwenty-child-admin-css');
        const adminDark = localStorage.getItem('admin-dark');
        if (adminDark === 'true') {
            darkCss.disabled = null;
        } else {
            darkCss.disabled = 'disabled';
        }
        const darkSwitch = document.getElementById('dark-switch');
        darkSwitch.addEventListener('click', () => {
            if (darkCss.disabled) {
                darkCss.disabled = null;
                localStorage.setItem('admin-dark', 'true');
            } else {
                darkCss.disabled = 'disabled';
                localStorage.setItem('admin-dark', 'false');
            }
        });
    </script>
	<?php
}

//endregion

//region wordpress版权信息代码
function add_copyright( $content ) {
	if ( is_single() or is_feed() ) {
		$content .= "<hr/>";
		$content .= '<div class="copyright" style="border: 1px solid; font-size: smaller; border-left: 5px solid; padding: 1%;">
		<h5 style="margin:0;"><small>声明</small></h5>
		<ul style="margin:0">
    		<li class="copyright-li">本作品采用<a rel="license" 
    			href="http://creativecommons.org/licenses/by-nc-sa/4.0/deed.zh" title="CC BY-NC-SA 4.0" 
    			target="_blank">署名-非商业性使用-相同方式共享 4.0 国际</a>许可协议进行许可。除非特别注明，
    			    <a href="https://youthlin.com" target="_blank"><strong>霖博客</strong></a>文章均为原创。</li>
		    <li class="copyright-li">转载请保留本文(<a 
		        href="' . wp_get_shortlink( get_the_ID() ) . '">《' . get_the_title() . '》</a>)链接地址：
		        <u>' . wp_get_shortlink( get_the_ID() ) . '</u></li>
			<li class="copyright-li">订阅本站：<a title="霖博客的 RSS 源" href="https://youthlin.com/feed/" 
				    rel="nofollow">https://youthlin.com/feed/</a></li>
		</ul></div>';
	}

	return $content;
}

add_filter( 'the_content', 'add_copyright' );

//endregion----------------------------------------------------------

//region 近期评论
class My_Widget_Recent_Comments extends WP_Widget_Recent_Comments {
	public function __construct() {
		$widget_ops = array(
			'classname'   => 'my_widget_recent_comments',
			'description' => __( '近期评论(带头像)' ),
		);
		WP_Widget::__construct( 'my-recent-comments', __( '近期评论(带头像)' ), $widget_ops );
	}

	/**
	 * 输出近期评论
	 *
	 * @param array $args Display arguments including 'before_title', 'after_title',
	 *                        'before_widget', and 'after_widget'.
	 * @param array $instance 当前挂件的设置数据
	 *
	 * @since 2.8.0
	 *
	 */
	public function widget( $args, $instance ) {
		if ( ! isset( $args['widget_id'] ) ) {
			$args['widget_id'] = $this->id;
		}

		$output = '';

		$title = ( ! empty( $instance['title'] ) ) ? $instance['title'] : __( 'Recent Comments' );

		/** This filter is documented in wp-includes/widgets/class-wp-widget-pages.php */
		$title = apply_filters( 'widget_title', $title, $instance, $this->id_base );

		$number = ( ! empty( $instance['number'] ) ) ? absint( $instance['number'] ) : 5;
		if ( ! $number ) {
			$number = 5;
		}

		/**
		 * Filters the arguments for the Recent Comments widget.
		 *
		 * @param array $comment_args An array of arguments used to retrieve the recent comments.
		 * @param array $instance Array of settings for the current widget.
		 *
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
					'number'      => $number,
					'status'      => 'approve',
					'post_status' => 'publish',
					'type'        => 'comment',    // 仅显示评论 不包括 ping-back
					'user_id'     => 0,         // 不显示作者评论
				),
				$instance
			)
		);

		$output .= $args['before_widget'];
		if ( $title ) {
			$output .= $args['before_title'] . $title . $args['after_title'];
		}

		$output .= '<ul id="my-recent-comments">';
		if ( is_array( $comments ) && $comments ) {
			// Prime cache for associated posts. (Prime post term cache if we need it for permalinks.)
			$post_ids = array_unique( wp_list_pluck( $comments, 'comment_post_ID' ) );
			_prime_post_caches( $post_ids, strpos( get_option( 'permalink_structure' ), '%category%' ), false );

			foreach ( (array) $comments as $comment ) {
				$avatar = get_avatar( $comment, 56, '', "@$comment->comment_author" );
				$output .= '<li class="recentcomments">';
				$output .=
					'<div class="comment-avatar">' . $avatar . '</div>'
					. '<div class="comment-content">'
					. '  <div class="line line-1" title="' . htmlentities( $comment->comment_content ) . '">'
					. mb_strimwidth( strip_tags( $comment->comment_content ), 0, 50, '...' ) . '</div>'
					. '  <div class="line line-2">' . sprintf(
					/* translators: Comments widget. 1: Comment author, 2: Post link. */
						_x( '%1$s on %2$s', 'widgets' ),
						get_comment_author_link( $comment ),
						'<a href="' . esc_url( get_comment_link( $comment ) ) . '">' . get_the_title( $comment->comment_post_ID ) . '</a>' )
					. '</div></div>';
				$output .= '</li>';
			}
		}
		$output .= '</ul>';
		$output .= $args['after_widget'];

		echo $output;
	}

}

register_widget( 'My_Widget_Recent_Comments' );
//endregion

//region 博主动态

/** http://nichen.info/add-mood-widget/
 *  WP添加侧边栏“心情随笔”
 *
 *  http://www.nuodou.com/a/856.html
 * WordPress自定义侧边栏小工具
 * 2012年06月07日　Mr.诺豆
 *
 * 小工具接口
 * http://codex.wordpress.org/zh-cn:%E5%B0%8F%E5%B7%A5%E5%85%B7%E6%8E%A5%E5%8F%A3/
 */
class Saying_Comments extends WP_Widget {
	public function __construct() {
		$widget_ops = array( 'classname' => 'widget_saying', 'description' => _( '一个类似QQ空间“说说”的小工具。' ) );
		parent::__construct( false, _( '说说' ), $widget_ops );
	}

	function form( $instance ) {
		//title:标题	pageid:页面ID	listnum:显示数量	charnum:截取字长
		$instance = wp_parse_args( (array) $instance, array(
			'title'   => '说说',
			'pageid'  => 0,
			'listnum' => 5,
			'charnum' => 54
		) );//默认值
		$title    = htmlspecialchars( $instance['title'] );
		$pageid   = htmlspecialchars( $instance['pageid'] );
		$listnum  = htmlspecialchars( $instance['listnum'] );
		$charnum  = htmlspecialchars( $instance['charnum'] );
		echo '<p style="text-align:left;"><label for="' . $this->get_field_name( 'title' )
		     . '">标题:<input style="width:200px;" id="' . $this->get_field_id( 'title' ) . '" name="' . $this->get_field_name( 'title' )
		     . '" type="text" value="' . $title . '" /></label></p>';
		echo '<p style="text-align:left;"><label for="' . $this->get_field_name( 'pageid' )
		     . '">页面ID:<input style="width:200px;" id="' . $this->get_field_id( 'pageid' )
		     . '" name="' . $this->get_field_name( 'pageid' ) . '" type="text" value="' . $pageid . '" /></label></p>';
		echo '<p style="text-align:left;"><label for="' . $this->get_field_name( 'listnum' )
		     . '">显示条数:<input style="width:200px;" id="' . $this->get_field_id( 'listnum' ) . '" name="'
		     . $this->get_field_name( 'listnum' ) . '" type="text" value="' . $listnum . '" /></label></p>';
		echo '<p style="text-align:left;"><label for="' . $this->get_field_name( 'charnum' )
		     . '">截取字长:<input style="width:200px" id="' . $this->get_field_id( 'charnum' ) . '" name="'
		     . $this->get_field_name( 'charnum' ) . '" type="text" value="' . $charnum . '" /></label></p>';
	}

	function update( $new_instance, $old_instance ) {
		$instance            = $old_instance;
		$instance['title']   = strip_tags( stripslashes( $new_instance['title'] ) );
		$instance['pageid']  = $new_instance['pageid'];
		$instance['listnum'] = strip_tags( stripslashes( $new_instance['listnum'] ) );
		$instance['charnum'] = strip_tags( stripslashes( $new_instance['charnum'] ) );

		return $instance;

	}
	//$args是注册侧边栏的注册的几个变量
	//$instance是小工具的设置数据
	function widget( $args, $instance ) {
		// http://nichen.info/add-mood-widget/
		// @see WP_Widget_Recent_Comments
		global $comments, $comment;

		extract( $args ); //将数组展开
		$title = ( ! empty( $instance['title'] ) ) ? $instance['title'] : __( '博主动态' );
		$title = apply_filters( 'widget_title', $title, $instance, $this->id_base );
		//$title = apply_filters('widget_title', empty($instance['title']) ? __('说说') : $instance['title']);
		$pageid  = empty( $instance['pagedid'] ) ? $instance['pageid'] : 0;
		$listnum = empty( $instance['listnum'] ) ? 5 : $instance['listnum'];
		$charnum = empty( $instance['charnum'] ) ? 140 : $instance['charnum'];

		$avatar = get_avatar( 1, 60, '', '头像' );
		// http://codex.wordpress.org/Function_Reference/get_avatar

		$argument = array(
			'number'  => $listnum,
			'post_id' => $pageid,
			'user_id' => '1',
			'parent'  => '0'
		);

		// http://codex.wordpress.org/zh-cn:函数参考/get_comments
		$comments = get_comments( $argument );
		$output   = '';
		$output   .= $args['before_widget'];
		if ( $title ) {
			$title  = $avatar . '<a href="' . get_permalink( $pageid ) . '">' . $title . '</a>';
			$output .= $args['before_title'] . $title . $args['after_title'];
		}
		//echo $pageid;//输出页面ID调试以查看pageid是否正确
		$output .= '<ul id="saying" style="clear: both;">';
		if ( $comments ) {
			foreach ( (array) $comments as $comment ) {
				$output .= '<li class="saying">'
				           . '<span><a href="' . esc_url( get_comment_link( $comment->comment_ID ) )
				           . '" title="' . htmlentities( strip_tags( $comment->comment_content ) ) . '">'
				           . convert_smilies( mb_strimwidth( strip_tags( $comment->comment_content ), 0, $charnum, '[...]' ) )
				           // http://blog.wpjam.com/function_reference/convert_smilies/
				           . '</a></span><br />'
				           . '<span>' . get_comment_date( 'm月d日H:i ', $comment->comment_ID ) . '</span></li>';
			}
		}
		$output .= '</ul>';
		$output .= $args['after_widget'];

		echo $output;
	}
}

register_widget( 'Saying_Comments' );
//endregion---------------------------------------------------

//region 表情
// 系统表情指向主题表情(修改表情代码_代码1/4)
add_filter( 'smilies_src', 'custom_smilies_src', 10, 3 );
function custom_smilies_src( $img_src, $img, $siteurl ) {
	return get_stylesheet_directory_uri() . '/images/' . $img;
}

// 表情代码转换图片(修改表情代码_代码2/4)
if ( ! isset( $wpsmiliestrans ) ) {
	$wpsmiliestrans = array(
		/*
	   '[亲亲]' => 'QQ/亲亲.gif',
	   '[偷笑]' => 'QQ/偷笑.gif',
	   '[再见]' => 'QQ/再见.gif',
	   '[发呆]' => 'QQ/发呆.gif',
	   '[发怒]' => 'QQ/发怒.gif',
	   '[可怜]' => 'QQ/可怜.gif',
	   '[吓]' => 'QQ/吓.gif',
	   '[呲牙]' => 'QQ/呲牙.gif',
	   '[害羞]' => 'QQ/害羞.gif',
	   '[得意]' => 'QQ/得意.gif',
	   '[微笑]' => 'QQ/微笑.gif',
	   '[惊恐]' => 'QQ/惊恐.gif',
	   '[抠鼻]' => 'QQ/抠鼻.gif',
	   '[擦汗]' => 'QQ/擦汗.gif',
	   '[敲打]' => 'QQ/敲打.gif',
	   '[晕]' => 'QQ/晕.gif',
	   '[流汗]' => 'QQ/流汗.gif',
	   '[流泪]' => 'QQ/流泪.gif',
	   '[疑问]' => 'QQ/疑问.gif',
	   '[色]' => 'QQ/色.gif',
	   '[调皮]' => 'QQ/调皮.gif',
	   '[鄙视]' => 'QQ/鄙视.gif',
	   '[阴险]' => 'QQ/阴险.gif',
	   '[鼓掌]' => 'QQ/鼓掌.gif',
	   */
		'[/鼓掌]' => 'smilies/鼓掌.gif',
		'[/难过]' => 'smilies/难过.gif',
		'[/调皮]' => 'smilies/调皮.gif',
		'[/白眼]' => 'smilies/白眼.gif',
		'[/疑问]' => 'smilies/疑问.gif',
		'[/流泪]' => 'smilies/流泪.gif',
		'[/流汗]' => 'smilies/流汗.gif',
		'[/撇嘴]' => 'smilies/撇嘴.gif',
		'[/抠鼻]' => 'smilies/抠鼻.gif',
		'[/惊讶]' => 'smilies/惊讶.gif',
		'[/微笑]' => 'smilies/微笑.gif',
		'[/得意]' => 'smilies/得意.gif',
		'[/大兵]' => 'smilies/大兵.gif',
		'[/坏笑]' => 'smilies/坏笑.gif',
		//'[/咒骂]' => 'smilies/咒骂.gif',
		'[/呲牙]' => 'smilies/呲牙.gif',
		'[/吓到]' => 'smilies/吓到.gif',
		'[/可爱]' => 'smilies/可爱.gif',
		'[/发怒]' => 'smilies/发怒.gif',
		'[/发呆]' => 'smilies/发呆.gif',
		'[/偷笑]' => 'smilies/偷笑.gif',
		'[/亲亲]' => 'smilies/亲亲.gif',

	);
}
// 3/4 输出表情到评论框
function lin_get_smilies() {
	global $wpsmiliestrans;
	$wpsmilies = array_unique( $wpsmiliestrans );
	$output    = "\n";
	foreach ( $wpsmilies as $alt => $src_path ) {
		$output .= '<img title="' . $alt . '" alt="' . $alt . '" class="wp-smiley my-smiley" src="'
		           . get_stylesheet_directory_uri() . '/images/' . $src_path . "\">\n";
	}

	return $output;
}

add_filter( 'comment_form_defaults', 'lin_output_smilies_to_comment_form' );
function lin_output_smilies_to_comment_form( $default ) {
	$default['comment_field'] .= '<p class="comment-form-smilies">' . lin_get_smilies() . '</p>';

	return $default;
}

// 4/4: 在 js 中实现点击图片插入表情到评论框
//endregion

//region comment ajax
add_action( 'comment_form', 'lin_ajax_comment_nonce', 10, 1 );
function lin_ajax_comment_nonce( $postId ) {
	wp_localize_script( 'comment-reply', 'lin_ajax', array(
		'url'               => admin_url( 'admin-ajax.php' ),
		'lin_comment_nonce' => wp_create_nonce( 'lin_comment_nonce_' . $postId )
	) );
}

add_action( 'pre_comment_on_post', 'lin_ajax_comment_verify', 10, 1 );
function lin_ajax_comment_verify( $postId ) {
	if ( ! wp_verify_nonce( $_POST['lin_comment_nonce'], 'lin_comment_nonce_' . $postId ) ) {
		header( 'HTTP/1.0 403 Forbidden' );
		header( 'Content-Type: text/plain;charset=UTF-8' );
		echo '403 Forbidden';
		exit;
	}
}

// wp_ajax_nopriv_{action} 用于登录用户 wp_ajax_{action} 用于未登录用户
add_action( 'wp_ajax_nopriv_lin_ajax_comment', 'lin_ajax_comment' );
add_action( 'wp_ajax_lin_ajax_comment', 'lin_ajax_comment' );
function lin_ajax_comment() {
	$comment = wp_handle_comment_submission( wp_unslash( $_POST ) );
	if ( is_wp_error( $comment ) ) {
		header( 'HTTP/1.0 500 Internal Server Error' );
		header( 'Content-Type: text/plain;charset=UTF-8' );
		echo $comment->get_error_message();
		exit;
	}
	$user            = wp_get_current_user();
	$cookies_consent = ( isset( $_POST['wp-comment-cookies-consent'] ) );
	do_action( 'set_comment_cookies', $comment, $user, $cookies_consent );

	$comment_depth  = 1;
	$comment_parent = $comment->comment_parent;
	while ( $comment_parent ) {
		$comment_depth ++;
		$parent_comment = get_comment( $comment_parent );
		$comment_parent = $parent_comment->comment_parent;
	}

	// 设置在 $GLOBALS 里 就可以用评论相关的函数了 如 comment_class() comment_text()
	$GLOBALS['comment']       = $comment;
	$GLOBALS['comment_depth'] = $comment_depth;
	?>
    <div id="comment-<?php comment_ID(); ?>" <?php comment_class(); ?>>
        <article id="div-comment-<?php comment_ID(); ?>" class="comment-body">
            <footer class="comment-meta">
                <div class="comment-author vcard">
					<?php
					$comment_author_url = get_comment_author_url( $comment );
					$comment_author     = get_comment_author( $comment );
					$avatar             = get_avatar( $comment, 120 );
					if ( empty( $comment_author_url ) ) {
						echo wp_kses_post( $avatar );
					} else {
						printf( '<a href="%s" rel="external nofollow" class="url">', $comment_author_url );
						echo wp_kses_post( $avatar );
					}

					printf(
						'<span class="fn">%1$s</span><span class="screen-reader-text says">%2$s</span>',
						esc_html( $comment_author ),
						__( 'says:', 'twentytwenty' )
					);

					if ( ! empty( $comment_author_url ) ) {
						echo '</a>';
					}
					?>
                </div>
                <div class="comment-metadata">
                    <a href="<?php echo esc_url( get_comment_link( $comment ) ); ?>">
						<?php
						/* Translators: 1 = comment date, 2 = comment time */
						$comment_timestamp = sprintf( __( '%1$s at %2$s', 'twentytwenty' ), get_comment_date( '', $comment ), get_comment_time() );
						?>
                        <time datetime="<?php comment_time( 'c' ); ?>"
                              title="<?php echo esc_attr( $comment_timestamp ); ?>">
							<?php echo esc_html( $comment_timestamp ); ?>
                        </time>
                    </a>
					<?php
					if ( get_edit_comment_link() ) {
						echo ' <span aria-hidden="true">&bull;</span> <a class="comment-edit-link" href="' . esc_url( get_edit_comment_link() ) . '">' . __( 'Edit', 'twentytwenty' ) . '</a>';
					}
					?>
                </div><!-- .comment-metadata -->
            </footer>
            <div class="comment-content entry-content">
				<?php
				comment_text();
				if ( '0' === $comment->comment_approved ) {
					?>
                    <p class="comment-awaiting-moderation"><?php _e( 'Your comment is awaiting moderation.', 'twentytwenty' ); ?></p>
					<?php
				}
				?>
            </div>
            <footer class="comment-footer-meta">
				<?php
				$comment_reply_link = get_comment_reply_link( array(
					'add_below' => 'div-comment',
					'depth'     => $comment_depth,
					'max_depth' => get_option( 'thread_comments_depth' ),
					'before'    => '<span class="comment-reply">',
					'after'     => '</span>',
				) );
				if ( $comment_reply_link ) {
					echo $comment_reply_link;
				}
				if ( twentytwenty_is_comment_by_post_author( $comment ) ) {
					echo '<span class="by-post-author">' . __( 'By Post Author', 'twentytwenty' ) . '</span>';
				}
				?>
            </footer>
        </article>
    </div>
	<?php die();
}

add_action( 'wp_ajax_nopriv_lin_list_comment', 'lin_ajax_comment_list' );
add_action( 'wp_ajax_lin_list_comment', 'lin_ajax_comment_list' );
// WordPress Ajax 评论分页/翻页 https://fatesinger.com/286
function lin_ajax_comment_list() {
	global $wp_query, $comments, $post;
	$wp_query->is_singular = true;//paginate_comments_links 判断不是文章会直接返回
	$postId                = $_POST['p'];
	$page                  = $_POST['c'];
	$post                  = get_post( $postId );//paginate_comments_links 里调用 get_permalink 需要用到这个全局变量
	$order                 = '';
	if ( strtolower( get_option( 'comment_order' ) ) == 'desc' ) {
		// 竟然是反的 无语
		$order = 'asc';
	} else {
		$order = 'desc';
	}
	$comments = get_comments( array( 'post_id' => $postId, 'order' => $order ) );
	?>
    <div class="comment-nav-wrap" id="comment-nav-wrap-top">
        <nav class="nav-links comment-nav-links">
			<?php paginate_comments_links( array(
				'total'   => get_comment_pages_count( $comments ),
				'current' => $page,
			) ); ?>
        </nav>
    </div>
    <div id="comment-nav-list">
		<?php
		wp_list_comments(
			array(
				'walker'      => new TwentyTwenty_Walker_Comment(),
				'avatar_size' => 120,
				'page'        => $page,
				'per_page'    => get_option( 'comments_per_page' ),
				'style'       => 'div',
				'order'       => get_option( 'comment_order' )
			),
			$comments
		);
		?>
    </div>
    <div class="comment-nav-wrap" id="comment-nav-wrap-bottom">
        <nav class="nav-links comment-nav-links">
			<?php paginate_comments_links( array(
				'total'   => get_comment_pages_count( $comments ),
				'current' => $page
			) ); ?>
        </nav>
    </div>
	<?php
	die;
}

//endregion comment ajax

// region 评论回复邮件通知
function notify_when_reply( $commentId ) {
	return 'yes' == get_comment_meta( $commentId, 'notify_when_reply', true );
}

add_filter( 'preprocess_comment', 'lin_comment_notify_add_notify_meta' );
// 如果勾选了邮件通知则放在评论 meta 里
function lin_comment_notify_add_notify_meta( $commentdata ) {
	if ( ! isset( $commentdata['comment_meta'] ) ) {
		$commentdata['comment_meta'] = array();
	}
	if ( isset( $_POST['wp-comment-notify-when-reply'] ) && $_POST['wp-comment-notify-when-reply'] == 'yes' ) {
		$commentdata['comment_meta']['notify_when_reply'] = 'yes';
	}

	return $commentdata;
}

add_action( 'comment_post', 'lin_comment_post_then_notify', 10, 3 );
// 当有新评论时
function lin_comment_post_then_notify( $comment_ID, $comment_approved, $commentdata ) {
	$comment   = get_comment( $comment_ID );
	$parent_id = $comment->comment_parent ? $comment->comment_parent : '';
	if ( $comment_approved == '1' && $parent_id ) {
		// 评论通过了审核，有父评论(是回复，不是顶级评论) 才需要发邮件给被回复人
		$parent_comment = get_comment( $parent_id );
		if ( notify_when_reply( $parent_id ) && $parent_comment->comment_approved == '1' ) {
			// 被回复人勾选了邮件通知
			$blogname = get_option( "blogname" );
			$subject  = "您在 [ $blogname ] 的留言有了回复";
			$to       = trim( $parent_comment->comment_author_email );

			$message     = '<div style="border: 1px dotted #ccc;padding: 16px;">
    <p style="border-bottom: 1px dashed #ccc;"><strong>' . trim( $parent_comment->comment_author ) . '</strong>，你好！</p>
    <p>你曾在 <a href="' . get_option( 'home' ) . '">' . $blogname . '</a>《<a href="' . get_permalink( $comment->comment_post_ID ) . '">' . get_the_title( $comment->comment_post_ID ) . '</a>》的留言有人回复你了！你的留言内容：</p>
    <blockquote style="border: 1px solid #ccc;border-radius:10px;padding: 16px;">' . trim( $parent_comment->comment_content ) . '</blockquote>
    <p><strong>' . trim( $comment->comment_author ) . '</strong> 给你的回复：</p>
    <blockquote style="border: 1px solid #ccc;border-radius:10px;padding: 16px;">' . trim( $comment->comment_content ) . '</blockquote>
    <p>你可以 <a href="' . htmlspecialchars( get_comment_link( $comment_ID ) ) . '">点此查看回复</a></p>
    <p>欢迎再度光临 <a href="' . get_option( 'home' ) . '">' . $blogname . '</a></p>
    <p style="border-top: 1px dashed #ccc;margin: 0;">
        <small>注 1: 你之所以收到此邮件，是因为你在评论该文章时勾选了<em>有人回复时邮件通知我</em><br>
            注 2: 此邮件由系统自动发送，请勿回复</small>
    </p>
</div>';
			$wp_email    = 'no-reply@' . preg_replace( '#^www\.#', '', strtolower( $_SERVER['SERVER_NAME'] ) );
			$from        = "From: $blogname <$wp_email>";
			$admin_email = get_bloginfo( 'admin_email' );
			$bcc         = "BCC: $blogname <$admin_email>";
			$headers     = "$from\n$bcc\nContent-Type: text/html; charset=" . get_option( 'blog_charset' ) . "\n";

			wp_mail( $to, $subject, $message, $headers );
		}
	}
}

add_filter( 'comment_form_submit_field', 'lin_add_notify_when_reply_checkbox', 10, 2 );
// 评论表单 在提交按钮前加个勾选框 默认选中
function lin_add_notify_when_reply_checkbox( $submit_field, $args ) {
	$checkbox = sprintf(
		'<p class="comment-form-notify-when-reply">%s %s</p>',
		'<input id="wp-comment-notify-when-reply" name="wp-comment-notify-when-reply" type="checkbox" checked value="yes" />',
		sprintf(
			'<label for="wp-comment-notify-when-reply">%s</label>',
			__( '有人回复时邮件通知我' )
		)
	);

	return $checkbox . $submit_field;
}

add_filter( 'comment_form_submit_button', 'lin_add_comment_submit_loading', 10, 2 );
// 评论提交中
function lin_add_comment_submit_loading( $submit_button, $args ) {
	$submit_button .= '<span id="comment-submit-loading">' . lin_get_svg_loading() . '</span>';

	return $submit_button;
}

add_filter( 'comment_reply_link_args', 'lin_add_notify_text_after_replay_link', 10, 3 );
// 接收邮件通知则在回复按钮后显示
function lin_add_notify_text_after_replay_link( $args, $comment, $post ) {
	if ( notify_when_reply( $comment->comment_ID ) ) {
		$note          = __( '回复时对方会收到邮件通知' );
		$after         = '<span class="notify-when-reply" title="' . $note . '">
<svg class="bi bi-envelope" width="1em" height="1em" viewBox="0 0 16 16" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
  <path fill-rule="evenodd" d="M14 3H2a1 1 0 00-1 1v8a1 1 0 001 1h12a1 1 0 001-1V4a1 1 0 00-1-1zM2 2a2 2 0 00-2 2v8a2 2 0 002 2h12a2 2 0 002-2V4a2 2 0 00-2-2H2z" clip-rule="evenodd"/>
  <path fill-rule="evenodd" d="M.071 4.243a.5.5 0 01.686-.172L8 8.417l7.243-4.346a.5.5 0 01.514.858L8 9.583.243 4.93a.5.5 0 01-.172-.686z" clip-rule="evenodd"/>
  <path d="M6.752 8.932l.432-.252-.504-.864-.432.252.504.864zm-6 3.5l6-3.5-.504-.864-6 3.5.504.864zm8.496-3.5l-.432-.252.504-.864.432.252-.504.864zm6 3.5l-6-3.5.504-.864 6 3.5-.504.864z"/>
</svg><span class="screen-reader-text">' . $note . '</span></span>';
		$args['after'] .= $after;
	}

	return $args;
}

add_filter( 'manage_edit-comments_columns', 'lin_manage_edit_comments_columns' );
// 后台评论页面添加一列
function lin_manage_edit_comments_columns( $columns ) {
	$columns['notify'] = __( '邮件通知' );

	return $columns;
}

add_action( 'manage_comments_custom_column', 'lin_comments_column_notify', 10, 2 );
// 新加的列的逻辑 显示是否接收邮件通知
function lin_comments_column_notify( $column_name, $comment_ID ) {
	if ( $column_name == 'notify' ) {
		$comment = get_comment( $comment_ID );
		if ( notify_when_reply( $comment->comment_parent ) ) {
			$parent_comment = get_comment( $comment->comment_parent );
			echo sprintf( __( '<span>已邮件通知 <strong>%s</strong></span><br>' ), $parent_comment->comment_author );
		}
		if ( notify_when_reply( $comment_ID ) ) {
			echo '<span>' . __( '被回复时接收邮件通知' ) . '</span>';
		}
	}
}

add_filter( 'comment_row_actions', 'lin_add_nop_action_notify', 10, 2 );
function lin_add_nop_action_notify( $actions, $comment ) {
	if ( notify_when_reply( $comment->comment_ID ) ) {
		$actions['notify'] = '<span style="color: #000" title="' . __( '被回复时接收邮件通知' ) . '">' . __( '可通知' ) . '</span>';
	}

	return $actions;
}

// endregion 评论回复邮件通知

// region 暴露 rest api 使用，但不使用 cookie 来认证，而是通过后台设置的密码
// 访问 /wp-admin/options.php 搜索 lin_rest_token 可以修改 token
add_action( 'init', 'lin_on_init' );
function lin_on_init() {
	$save = get_option( 'lin_rest_token' );
	if ( ! $save ) {
		$generate = wp_generate_password( 64, true, true );
		update_option( 'lin_rest_token', $generate );
	}
}

add_action( 'rest_api_init', 'on_rest_init' );
function on_rest_init( $wp_rest_server ) {
	$save = get_option( 'lin_rest_token' );
	if ( ! $save ) {
		return;
	}
	if ( isset( $_SERVER['HTTP_X_LIN_TOKEN'] ) ) {
		$token = $_SERVER['HTTP_X_LIN_TOKEN'];
		if ( $token == $save ) {
			// 如果请求头和设置的密码一致，则认为是 id=1 的用户
			wp_set_current_user( 1 );
		}
	}
}
// endregion rest-api

// region 头像
// https://cravatar.com/developer/for-wordpress

if ( ! function_exists( 'get_cravatar_url' ) ) {
    /**
     * 替换 Gravatar 头像为 Cravatar 头像
     *
     * Cravatar 是 Gravatar 在中国的完美替代方案，您可以在 https://cravatar.com 更新您的头像
     */
    function get_cravatar_url( $url ) {
        $sources = array(
            'www.gravatar.com',
            '0.gravatar.com',
            '1.gravatar.com',
            '2.gravatar.com',
            'secure.gravatar.com',
            'cn.gravatar.com',
            'gravatar.com',
        );
        return str_replace( $sources, 'cravatar.cn', $url );
    }
    add_filter( 'um_user_avatar_url_filter', 'get_cravatar_url', 1 );
    add_filter( 'bp_gravatar_url', 'get_cravatar_url', 1 );
    add_filter( 'get_avatar_url', 'get_cravatar_url', 1 );
}
if ( ! function_exists( 'set_defaults_for_cravatar' ) ) {
    /**
     * 替换 WordPress 讨论设置中的默认头像
     */
    function set_defaults_for_cravatar( $avatar_defaults ) {
        $avatar_defaults['gravatar_default'] = 'Cravatar 标志';
        return $avatar_defaults;
    }
    add_filter( 'avatar_defaults', 'set_defaults_for_cravatar', 1 );
}
if ( ! function_exists( 'set_user_profile_picture_for_cravatar' ) ) {
    /**
     * 替换个人资料卡中的头像上传地址
     */
    function set_user_profile_picture_for_cravatar() {
        return '<a href="https://cravatar.com" target="_blank">您可以在 Cravatar 修改您的资料图片</a>';
    }
    add_filter( 'user_profile_picture_description', 'set_user_profile_picture_for_cravatar', 1 );
}
//endregion
