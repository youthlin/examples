<?php
header("Content-type: image/png");
//创建图像http://www.php.net/imagecreate
$im = imagecreate(130, 30);
//http://www.php.net/ImageColorAllocate
$background_color = ImageColorAllocate($im, 255, 255, 255);
unset($ip);
if ($_SERVER['HTTP_CLIENT_IP']) {
    $ip = $_SERVER['HTTP_CLIENT_IP'];
} else if ($_SERVER['HTTP_X_FORWARDED_FOR']) {
    $ip = $_SERVER['HTTP_X_FORWARDED_FOR'];
} else {
    $ip = $_SERVER['REMOTE_ADDR'];
}
//设置颜色http://www.php.net/imagecolorallocate
$col = imagecolorallocate($im, 0, 51, 102);
//画字符串http://www.php.net/imagestring
imagestring($im, 3, 5, 1, $ip, $col);
$ip = "By Youth.Lin";
imagestring($im, 3, 15, 15, $ip, $col);
//输出图像http://www.php.net/imagegif
imagegif($im);
imagedestroy($im);
