package com.huntkey.rx.config.server;

import com.huntkey.rx.config.server.model.GitCommit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent;
import org.springframework.cloud.config.monitor.GitlabPropertyPathNotificationExtractor;
import org.springframework.cloud.config.monitor.PropertyPathEndpoint;
import org.springframework.cloud.config.monitor.PropertyPathNotification;
import org.springframework.cloud.config.monitor.PropertyPathNotificationExtractor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.beans.ConstructorProperties;
import java.util.*;

/**
 * Created by zhaomj on 2017/4/11.
 */
@RestController
@RequestMapping(
        path = {"${spring.cloud.config.monitor.endpoint.path:}/sceo"}
)
public class TestController implements ApplicationEventPublisherAware, ApplicationContextAware {

    @ConstructorProperties({"gitlabPropertyPathNotificationExtractor"})
    public TestController(GitlabPropertyPathNotificationExtractor extractor) {
        this.extractor = extractor;
    }

    private static final Log log = LogFactory.getLog(TestController.class);
    private final GitlabPropertyPathNotificationExtractor extractor;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    private String contextId = UUID.randomUUID().toString();

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.contextId = applicationContext.getId();
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @RequestMapping("/test")
    public String test(){
        return "自定义monitor";
    }

    @RequestMapping(value = "/monitor",
            method = {RequestMethod.POST}
    )
    public Set<String> myMonitor(@RequestBody Map<String, Object> request) {
        List<GitCommit> commits = (List<GitCommit>)request.get("commits");
        System.out.println("===================================="+commits);
        Set<String> services = new LinkedHashSet();
        services.addAll(this.guessServiceName("*"));
        if (this.applicationEventPublisher != null) {
            Iterator var9 = services.iterator();

            while (var9.hasNext()) {
                String service = (String) var9.next();
                log.info("Refresh for : " + service);
                this.applicationEventPublisher.publishEvent(new RefreshRemoteApplicationEvent(this, this.contextId, service));
            }

            return services;
        }

        return Collections.emptySet();
    }

    private Set<String> guessServiceName(String path) {
        Set<String> services = new LinkedHashSet();
        if (path != null) {
            String stem = StringUtils.stripFilenameExtension(StringUtils.getFilename(path));

            for (int index = stem.indexOf("-"); index >= 0; index = stem.indexOf("-", index + 1)) {
                String name = stem.substring(0, index);
                String profile = stem.substring(index + 1);
                if ("application".equals(name)) {
                    services.add("*:" + profile);
                } else if (!name.startsWith("application")) {
                    services.add(name + ":" + profile);
                }
            }

            if ("application".equals(stem)) {
                services.add("*");
            } else if (!stem.startsWith("application")) {
                services.add(stem);
            }
        }

        return services;
    }
}
