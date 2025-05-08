package backend.academy.bot.client;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

public class WireMockTestUtil {

    private static WireMockServer wireMockServer;

    public static WireMockServer getWireMockServer() {
        return wireMockServer;
    }

    public static void setUp(int FIXED_PORT) {
        wireMockServer = new WireMockServer(wireMockConfig().port(FIXED_PORT));
        wireMockServer.start();
        WireMock.configureFor("localhost", FIXED_PORT);
    }

    public static void tearDown() {
        wireMockServer.stop();
    }
}
