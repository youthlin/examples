<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Demo</title>
    <style>
    *{padding:0;margin:0;}h1{background:green;text-align:center;color:white;}
    ul { list-style: none; width: 50%; text-align: center; margin: 0 auto; }
    li { margin: 1%;  border-left: 10px solid green; }
    a { display: inline-block; width: 100%;padding: 1%; text-decoration: none; border-bottom: 1px solid green; }
    </style>
</head>
<body>
<?php
// https://yian.me/blog/codes/php-list-dirs-in-current-dir.html
$filename=scandir("./"); //遍历当前目录下的所有文件及文件夹
echo "<h1>Index</h1><ul>";
for($i=0;$i<sizeof($filename);$i++){
    if($filename[$i]=="."){
        continue;
    }
    if(is_dir($filename[$i]))  {//判断是否为文件夹
        $filename[$i] = $filename[$i] . "/";
    }
    echo "<li><a href=\"".$filename[$i]."\">".$filename[$i]."</a></li>\n";
}
?></ul>
</body>
</html>