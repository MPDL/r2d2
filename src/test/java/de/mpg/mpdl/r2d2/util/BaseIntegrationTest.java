package de.mpg.mpdl.r2d2.util;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(initializers = {DataBaseLauncher.Initializer.class, SearchEngineLauncher.Initializer.class})
public abstract class BaseIntegrationTest {

  //TODO: Stop/Reset/Clear DB/ES container after test?

}
