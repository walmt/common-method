# [如何让spring mvc web应用启动时就执行特定处理](http://www.cnblogs.com/yjmyzz/p/4747251.html)

- Spring-MVC的应用中，要实现类似的功能，主要是通过实现下面这些接口（任选一，至少一个即可）

### 一、ApplicationContextAware接口

```Java
package org.springframework.context;
 
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.context.ApplicationContext;
 
public interface ApplicationContextAware extends Aware {
    void setApplicationContext(ApplicationContext var1) throws BeansException;
}
```

### 二、ServletContextAware 接口

```Java
package org.springframework.web.context;
 
import javax.servlet.ServletContext;
import org.springframework.beans.factory.Aware;
 
public interface ServletContextAware extends Aware {
    void setServletContext(ServletContext var1);
}
```

### 三、InitializingBean 接口

```Java
package org.springframework.beans.factory;
 
public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
```

### 四、ApplicationListener<ApplicationEvent> 接口

```Java
package org.springframework.context;
 
import java.util.EventListener;
import org.springframework.context.ApplicationEvent;
 
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
    void onApplicationEvent(E var1);
}
```

### 示例程序：

```Java
package test.web.listener;
 
import org.apache.logging.log4j.*;
import org.springframework.beans.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import javax.servlet.ServletContext;
 
@Component
public class StartupListener implements ApplicationContextAware, ServletContextAware,
        InitializingBean, ApplicationListener<ContextRefreshedEvent> {
 
    protected Logger logger = LogManager.getLogger();
 
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        logger.info("1 => StartupListener.setApplicationContext");
    }
 
    @Override
    public void setServletContext(ServletContext context) {
        logger.info("2 => StartupListener.setServletContext");
    }
 
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("3 => StartupListener.afterPropertiesSet");
    }
 
    @Override
    public void onApplicationEvent(ContextRefreshedEvent evt) {
        logger.info("4.1 => MyApplicationListener.onApplicationEvent");
        if (evt.getApplicationContext().getParent() == null) {
            logger.info("4.2 => MyApplicationListener.onApplicationEvent");
        }
    }
 
}
```

### 运行时，输出的顺序如下：

```控制台
1 => StartupListener.setApplicationContext
2 => StartupListener.setServletContext
3 => StartupListener.afterPropertiesSet
4.1 => MyApplicationListener.onApplicationEvent
4.2 => MyApplicationListener.onApplicationEvent
4.1 => MyApplicationListener.onApplicationEvent
```

- 注意：**onApplicationEvent方法会触发多次，初始化这种事情，越早越好，建议在setApplicationContext方法中处理。**

