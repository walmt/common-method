# Listener监听器

- 监听器Listener就是在application,session,request三个对象创建、销毁或者往其中添加修改删除属性时自动执行代码的功能组件。
- Listener是Servlet的监听器，可以监听客户端的请求，服务端的操作等。

---

### ServletContext监听

- ServletContextListener：用于对Servlet整个上下文进行监听（创建、销毁）。

```Java
public void contextInitialized(ServletContextEvent sce);//上下文初始化
public void contextDestroyed(ServletContextEvent sce);//上下文销毁

public ServletContext getServletContext();//ServletContextEvent事件：取得一个ServletContext（application）对象
```

- ServletContextAttributeListener：对Servlet上下文属性的监听（增删改属性）。

```Java
public void attributeAdded(ServletContextAttributeEvent scab);//增加属性
public void attributeRemoved(ServletContextAttributeEvent scab);//属性删除
public void attributeRepalced(ServletContextAttributeEvent scab);//属性替换（第二次设置同一属性）

//ServletContextAttributeEvent事件：能取得设置属性的名称与内容
public String getName();//得到属性名称
public Object getValue();//取得属性的值
```

---

### Session监听

- Session属于http协议下的内容，接口位于javax.servlet.http.*包下。

- HttpSessionListener接口：对Session的整体状态的监听。

```Java
public void sessionCreated(HttpSessionEvent se);//session创建
public void sessionDestroyed(HttpSessionEvent se);//session销毁

//HttpSessionEvent事件：
public HttpSession getSession();//取得当前操作的session
```

- HttpSessionAttributeListener接口：对session的属性监听。

```Java
public void attributeAdded(HttpSessionBindingEvent se);//增加属性
public void attributeRemoved(HttpSessionBindingEvent se);//删除属性
public void attributeReplaced(HttpSessionBindingEvent se);//替换属性

//HttpSessionBindingEvent事件：
public String getName();//取得属性的名称
public Object getValue();//取得属性的值
public HttpSession getSession();//取得当前的session
```

> session的销毁有两种情况：
> - session超时，web.xml配置：
> ```XML
> <session-config>
>     <session-timeout>120</session-timeout><!--session120分钟后超时销毁-->
> </session-config>
> ```
>
> - 手工使session失效
>
> ```Java
> public void invalidate();//使session失效方法。session.invalidate();
> ```

---

### Request监听

- ServletRequestListener：用于对Request请求进行监听（创建、销毁）。

```Java
public void requestInitialized(ServletRequestEvent sre);//request初始化
public void requestDestroyed(ServletRequestEvent sre);//request销毁

//ServletRequestEvent事件：
public ServletRequest getServletRequest();//取得一个ServletRequest对象
public ServletContext getServletContext();//取得一个ServletContext（application）对象
```

- ServletRequestAttributeListener：对Request属性的监听（增删改属性）。

```Java
public void attributeAdded(ServletRequestAttributeEvent srae);//增加属性
public void attributeRemoved(ServletRequestAttributeEvent srae);//属性删除
public void attributeReplaced(ServletRequestAttributeEvent srae);//属性替换（第二次设置同一属性）

//ServletRequestAttributeEvent事件：能取得设置属性的名称与内容
public String getName();//得到属性名称
public Object getValue();//取得属性的值
```

---

### **在web.xml中配置**

```XML
<!-- 如果监听器需要用到Spring的bean，则需要把该监听器的顺序写在Spring监听器的后面 -->
<listener>
    <listener-class>com.listener.class</listener-class>
</listener>
```

### Listener应用实例

##### 自定义Listener（监听器）中使用Spring容器管理的bean

- 在java web项目中我们通常会有这样的需求：当项目启动时执行一些初始化操作，例如从数据库加载全局配置文件等，通常情况下我们会用javaee规范中的Listener去实现，例如

```Java
public class ConfigListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
          //执行初始化操作
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
```

- 这样当servlet容器初始化完成后便会调用contextInitialized方法。但是通常我们在执行初始化的过程中会调用service和dao层提供的方法，而现在web项目通常会采用spring框架来管理和装配bean，我们想当然会像下面这么写，假设执行初始化的过程中需要调用ConfigService的initConfig方法，而ConfigService由spring容器管理（标有@Service注解）

```Java
public class ConfigListener implements ServletContextListener {
 
    @Autowired
    private ConfigService configService;
     
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        configService.initConfig();
    }
 
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
```

- 然而以上代码会在项目启动时抛出空指针异常！ConfigService实例并没有成功注入。这是为什么呢？要理解这个问题，首先要区分Listener的生命周期和spring管理的bean的生命周期。

1. Listener的生命周期是由servlet容器（例如tomcat）管理的，项目启动时上例中的ConfigListener是由servlet容器实例化并调用其contextInitialized方法，而servlet容器并不认得@Autowired注解，因此导致ConfigService实例注入失败。
2. 而spring容器中的bean的生命周期是由spring容器管理的。

- 那么该如何在spring容器外面获取到spring容器bean实例的引用呢？这就需要用到spring为我们提供的WebApplicationContextUtils工具类，该工具类的作用是获取到spring容器的引用，进而获取到我们需要的bean实例。代码如下

```Java
public class ConfigListener implements ServletContextListener {
     
    @Override
    public void contextInitialized(ServletContextEvent sce) {   
        ConfigService configService = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext()).getBean(ConfigService.class);
        configService.initConfig();
    }
 
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
 
}
```

- 注意：以上代码有一个前提，那就是servlet容器在实例化ConfigListener并调用其方法之前，要确保spring容器已经初始化完毕！而spring容器的初始化也是由Listener（ContextLoaderListener）完成，因此只需在web.xml中先配置初始化spring容器的Listener，然后在配置自己的Listener，配置如下

```Java
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:spring.xml</param-value>
</context-param>
 
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
 
<listener>
    <listener-class>example.ConfigListener</listener-class>
</listener>
```



##### **利用HttpSessionListener统计最多在线用户人数**

```Java
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class HttpSessionListenerImpl implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent event) {
        ServletContext app = event.getSession().getServletContext();
        int count = Integer.parseInt(app.getAttribute("onLineCount").toString());
        count++;
        app.setAttribute("onLineCount", count);
        int maxOnLineCount = Integer.parseInt(app.getAttribute("maxOnLineCount").toString());
        if (count > maxOnLineCount) {
            //记录最多人数是多少
            app.setAttribute("maxOnLineCount", count);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //记录在那个时刻达到上限
            app.setAttribute("date", df.format(new Date()));
        }
    }
    //session注销、超时时候调用，停止tomcat不会调用
    public void sessionDestroyed(HttpSessionEvent event) {
        ServletContext app = event.getSession().getServletContext();
        int count = Integer.parseInt(app.getAttribute("onLineCount").toString());
        count--;
        app.setAttribute("onLineCount", count);    
        
    }
}
```

##### Spring使用ContextLoaderListener加载ApplicationContext配置信息

- ContextLoaderListener的作用就是启动Web容器时，自动装配ApplicationContext的配置信息。因为它实现了ServletContextListener这个接口，在web.xml配置这个监听器，启动容器时，就会默认执行它实现的方法。
- ContextLoaderListener如何查找ApplicationContext.xml的配置位置以及配置多个xml：如果在web.xml中不写任何参数配置信息，默认的路径是"/WEB-INF/applicationContext.xml"，在WEB-INF目录下创建的xml文件的名称必须是applicationContext.xml（在MyEclipse中把xml文件放置在src目录下）。如果是要自定义文件名可以在web.xml里加入contextConfigLocation这个context参数。

```XML
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:spring/applicationContext-*.xml</param-value><!-- 采用的是通配符方式，查找WEB-INF/spring目录下xml文件。如有多个xml文件，以“,”分隔。 -->
</context-param>

<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

##### **Spring使用Log4jConfigListener配置Log4j日志**

- Spring使用Log4jConfigListener的好处：

1. 动态的改变记录级别和策略，不需要重启Web应用。
2. 把log文件定在 /WEB-INF/logs/ 而不需要写绝对路径。因为系统把web目录的路径压入一个叫webapp.root的系统变量。这样写log文件路径时不用写绝对路径了。
3. 可以把log4j.properties和其他properties一起放在/WEB-INF/ ，而不是Class-Path。
4. 设置log4jRefreshInterval时间，开一条watchdog线程每隔段时间扫描一下配置文件的变化。

```XML
<context-param>
    <param-name>webAppRootKey</param-name>
    <param-value>project.root</param-value><!-- 用于定位log文件输出位置在web应用根目录下，log4j配置文件中写输出位置：log4j.appender.FILE.File=${project.root}/logs/project.log -->
</context-param>
<context-param>
    <param-name>log4jConfigLocation</param-name>
    <param-value>classpath:log4j.properties</param-value><!-- 载入log4j配置文件 -->
</context-param>
<context-param>
    <param-name>log4jRefreshInterval</param-name>
    <param-value>60000</param-value><!--Spring刷新Log4j配置文件的间隔60秒,单位为millisecond-->
</context-param>

<listener>
    <listener-class>org.springframework.web.util.Log4jConfigListener</listener-class>
</listener>
```

##### **Spring使用IntrospectorCleanupListener清理缓存**

- 这个监听器的作用是在web应用关闭时刷新JDK的JavaBeans的Introspector缓存，以确保Web应用程序的类加载器以及其加载的类正确的释放资源。
- 如果JavaBeans的Introspector已被用来分析应用程序类，系统级的Introspector缓存将持有这些类的一个硬引用。因此，这些类和Web应用程序的类加载器在Web应用程序关闭时将不会被垃圾收集器回收！而IntrospectorCleanupListener则会对其进行适当的清理，已使其能够被垃圾收集器回收。
- 唯一能够清理Introspector的方法是刷新整个Introspector缓存，没有其他办法来确切指定应用程序所引用的类。这将删除所有其他应用程序在服务器的缓存的Introspector结果。
- 在使用Spring内部的bean机制时，不需要使用此监听器，因为Spring自己的introspection results cache将会立即刷新被分析过的JavaBeans Introspector cache，而仅仅会在应用程序自己的ClassLoader里面持有一个cache。虽然Spring本身不产生泄漏，注意，即使在Spring框架的类本身驻留在一个“共同”类加载器（如系统的ClassLoader）的情况下，也仍然应该使用使用IntrospectorCleanupListener。在这种情况下，这个IntrospectorCleanupListener将会妥善清理Spring的introspection cache。
- 应用程序类，几乎不需要直接使用JavaBeans Introspector，所以，通常都不是Introspector resource造成内存泄露。相反，许多库和框架，不清理Introspector，例如： Struts和Quartz。
- 需要注意的是一个简单Introspector泄漏将会导致整个Web应用程序的类加载器不会被回收！这样做的结果，将会是在web应用程序关闭时，该应用程序所有的静态类资源（比如：单实例对象）都没有得到释放。而导致内存泄露的根本原因其实并不是这些未被回收的类！
- 注意：IntrospectorCleanupListener应该注册为web.xml中的第一个Listener，在任何其他Listener之前注册，比如在Spring's ContextLoaderListener注册之前，才能确保IntrospectorCleanupListener在Web应用的生命周期适当时机生效。

```XML
<!-- memory clean -->
<listener>
    <listener-class>org.springframework.web.util.IntrospectorCleanupListener</listener-class>
</listener>
```

