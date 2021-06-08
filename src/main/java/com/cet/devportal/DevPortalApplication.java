package com.cet.devportal;

import com.cet.devportal.ini.IniFilePropertySource;
import com.cet.devportal.ini.IniFileReader;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.io.*;
import java.util.Objects;
import java.util.stream.Stream;

@SpringBootApplication
@ComponentScan(basePackages = {"com.cet.devportal"})
@MapperScan(basePackages = "com.cet.devportal.dao")
public class DevPortalApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevPortalApplication.class);


    public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DevPortalApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.addListeners(new EnvironmentSetupListener());
		app.run(args);
	}

    /**
     * 环境变量监视
     */
    private static class EnvironmentSetupListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {
        public static final String SERVER_CONFIG_DIR = "DEVPORTAL_SERVER_CONFIG_DIR";

        @Override
        public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
            final ConfigurableEnvironment environment = event.getEnvironment();
            //环境变量包含配置文件路径
            if (environment.containsProperty(SERVER_CONFIG_DIR)){
                String configFilePath = environment.getProperty(SERVER_CONFIG_DIR);
                setupEnvironment(environment, configFilePath);
            }
        }

        /**
         * 读取配置文件
         * @param environment
         * @return
         */
        private static IniFilePropertySource readProperties(Environment environment, String name, String configFilePath)
        {
            final File file = new File(configFilePath);
            if (file.exists() && file.canRead())
            {
                try (InputStream in = new FileInputStream(file);
                     Reader reader = new InputStreamReader(in, "UTF-8"))
                {
                    return new IniFilePropertySource(
                            name,
                            new IniFileReader().read(reader),
                            environment.getActiveProfiles());
                }
                catch (IOException ex)
                {
                    LOGGER.error("Unable to read configuration file {}", file, ex);
                }
            }
            return null;
        }

        /**
         * 构造环境变量
         * @param environment
         */
        public static void setupEnvironment(ConfigurableEnvironment environment, String configFilePath){
            Stream.of("cetdev_portal")
                    .map(name -> readProperties(environment, name, configFilePath))
                    .filter(Objects::nonNull)
                    .forEach(iniPropSource -> environment.getPropertySources()
                            .addBefore("systemEnvironment", iniPropSource));
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }
    }



}
