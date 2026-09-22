package si.puzzle.cobiss.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cobiss")
public class CobissProperties {

    /**
     * Base URL of the COBISS Plus API, e.g. https://plus.cobiss.net
     */
    private String apiBaseUrl = "https://plus.cobiss.net";

    /**
     * Base URL of COBISS Plus record pages used for cover images.
     * Pattern: {recordBaseUrl}/{id}
     */
    private String recordBaseUrl = "https://plus.cobiss.net/cobiss/si/sl/search/cobib";

    /**
     * When true, use built-in mock books instead of calling COBISS Plus.
     */
    private boolean mock = true;

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getRecordBaseUrl() {
        return recordBaseUrl;
    }

    public void setRecordBaseUrl(String recordBaseUrl) {
        this.recordBaseUrl = recordBaseUrl;
    }

    public boolean isMock() {
        return mock;
    }

    public void setMock(boolean mock) {
        this.mock = mock;
    }

    public String searchUrl() {
        return apiBaseUrl + "/cobiss/api/si/sl/search/cobib";
    }

    public String displayUrl(String id) {
        return apiBaseUrl + "/cobiss/api/si/sl/search/cobib/display/" + id;
    }

    public String recordPageUrl(String id) {
        return recordBaseUrl + "/" + id;
    }
}
