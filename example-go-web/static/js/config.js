function fixHead($table) {
    var topValue = $table.data('top-value') || 56;
    var target = $table.data('target');
    var $target = $(target);
    // console.log('fixHead:' + target + "=" + topValue);
    $(window).scroll(function () {
        var topDis = $table.offset().top - $(window).scrollTop();
        var bottomDis = $table.height() + $table.offset().top - $(window).scrollTop() - $target.height();
        if (topDis < topValue && bottomDis > topValue) {
            $target.removeClass('invisible');
            $target.css('top', topValue + 'px');
        } else {
            $target.css('top', 0);
            $target.addClass('invisible');
        }
    });
}

function toggleAside($ctl) {
    var lg = $($ctl.data('lg'));
    var sm = $($ctl.data('sm'));
    if (lg.hasClass('col-8')) {
        lg.removeClass('col-8').addClass('col-12');
        sm.removeClass('col-4').addClass('d-none');
    } else {
        lg.removeClass('col-12').addClass('col-8');
        sm.removeClass('d-none').addClass('col-4');
    }
}

$(document).ready(function () {
    $('.fix-head-when-scroll').each(function () {
        fixHead($(this));
    });
    $('.aside-ctl').click(function () {
        toggleAside($(this));
    });

    $('.date').each(function () {
        var date = $(this).html();
        $(this).html(new Date(date).toLocaleString());
        $(this).attr("title", date);
    })
});

function activeNavItem(className) {
    $(document).ready(function () {
        $('.' + className).addClass('active');
    });
}

function timezoneName() {
    return Intl.DateTimeFormat().resolvedOptions().timeZone;
}

function timezoneMinute() {
    return new Date().getTimezoneOffset();
}
