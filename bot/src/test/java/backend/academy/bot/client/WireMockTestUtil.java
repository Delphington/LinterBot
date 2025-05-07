package backend.academy.bot.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.Getter;
import org.junit.jupiter.api.AfterAll;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockTestUtil {

    private static WireMockServer wireMockServer;


    public static WireMockServer getWireMockServer() {
        return wireMockServer;
    }

    public static void setUp(int FIXED_PORT){
        wireMockServer = new WireMockServer(wireMockConfig().port(FIXED_PORT));
        wireMockServer.start();
        WireMock.configureFor("localhost", FIXED_PORT);
    }


    public static void tearDown() {
        wireMockServer.stop();
    }

}
