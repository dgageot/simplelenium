package net.codestory.simplelenium;

import org.junit.Test;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JavascriptTest extends AbstractTest {

    @Test
    public void executeBasicJavaScript() {
        goTo("/");
        assertThat((Long)executeJavascript("return 3+1")).isEqualTo(4);
    }


    @Test
    public void executeJavaScript() {
        goTo("/");
        assertThat((List<RemoteWebElement>) executeJavascript("return window.top.document.querySelectorAll('a')")).hasSize(2);
    }
}
