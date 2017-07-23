# Date和String按格式的互相转换

``` Java
Date currentTime = new Date();
SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//时间转String
String dateString = formatter.format(currentTime);
//String转时间
Date date = formatter.parse(dateString);
```