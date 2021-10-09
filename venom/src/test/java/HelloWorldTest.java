import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HelloWorldTest {

  @BeforeEach
  void setup() {
  }

  @Test
  @DisplayName("Sample Test")
  void sampleTest() {
    assertEquals(1,HelloWorld.testFunction());
  }

}
