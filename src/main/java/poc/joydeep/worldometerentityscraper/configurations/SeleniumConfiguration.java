package poc.joydeep.worldometerentityscraper.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "selenium")
public class SeleniumConfiguration {
    private String webDriverPath;
    private String webDriverType;
    private int webDriverPageLoadTimeout;

    public void setWebDriverPath(String webDriverPath) {
        this.webDriverPath = webDriverPath;
    }

    public void setWebDriverType(String webDriverType) {
        this.webDriverType = webDriverType;
    }

    public void setWebDriverPageLoadTimeout(int webDriverPageLoadTimeout) {
        this.webDriverPageLoadTimeout = webDriverPageLoadTimeout;
    }

    public String getWebDriverPath() {
        return webDriverPath;
    }

    public String getWebDriverType() {
        return webDriverType;
    }

    public int getWebDriverPageLoadTimeout() {
        return webDriverPageLoadTimeout;
    }
}
