package com.packtpub.yummy.ui;


import com.google.common.base.Predicate;
import com.packtpub.yummy.model.Bookmark;
import com.packtpub.yummy.service.BookmarkService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UiSeleniumTest {
    private static ChromeDriverService DRIVER_SERVICE;

    List<Bookmark> bookmarks = Arrays.asList(
            new Bookmark("Packt publishing",
                    "http://packtpub.com").withUuid(UUID.randomUUID()),
            new Bookmark("orchit GmbH homepage", "http://orchit.de")
                    .withUuid(UUID.randomUUID())
    );

    @LocalServerPort
    int port;

    @MockBean
    BookmarkService bookmarkService;

    private WebDriver wd;

    @BeforeClass
    public static void beforeClass() throws IOException {
        DRIVER_SERVICE = new ChromeDriverService.Builder()
                .usingAnyFreePort()
                .usingDriverExecutable(new File("seleniumdriver/chromedriver"))
                .build();
        DRIVER_SERVICE.start();
    }

    @AfterClass
    public static void shutdown() {
        DRIVER_SERVICE.stop();
    }

    @Before
    public void before() {
        when(bookmarkService.findAll()).thenReturn(bookmarks);
        wd = new RemoteWebDriver(DRIVER_SERVICE.getUrl(), DesiredCapabilities.chrome());
    }


    @After
    public void after() {
        wd.close();
    }

    @Test
    public void testSeleniumLogin() {
        wd.get("http://localhost:"+port);

        WebDriverWait wait = new WebDriverWait(wd,3);

        wait.until((Predicate<WebDriver>)  webDriver ->{
            webDriver.findElement(By.name("username"));
            return true;
        } );

        wd.findElement(By.name("username")).sendKeys("admin");
        wd.findElement(By.name("password")).sendKeys("password");
        wd.findElement(By.name("submit")).click();

        wait.until((Predicate<WebDriver>)  webDriver ->{
            webDriver.findElement(By.linkText("http://packtpub.com"));
            return true;
        } );




    }
}
