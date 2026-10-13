package team2.race_service.config;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true")
public class H2ConsoleConfig {

    @Value("${spring.h2.console.path:/h2-console}")
    private String consolePath;

    // Overrides H2ConsoleAutoConfiguration (which only maps /h2-console/*)
    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2Console() {
        JakartaWebServlet servlet = new JakartaWebServlet();
        ServletRegistrationBean<JakartaWebServlet> bean =
                new ServletRegistrationBean<>(servlet, consolePath, consolePath + "/*");
        bean.addInitParameter("webAllowOthers", "true");
        bean.setLoadOnStartup(1);
        return bean;
    }
}
