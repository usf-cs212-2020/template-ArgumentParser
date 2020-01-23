import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for the {@link ArgumentParser} class.
 *
 * @see ArgumentParser
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
@TestMethodOrder(Alphanumeric.class)
public class ArgumentParserTest {

  /**
   * Tests for the {@link ArgumentParser#isFlag(String)} method.
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class A_FlagTests {

    /**
     * Tests values that should be considered valid flags.
     *
     * @param flag valid flag value
     */
    @Order(1)
    @ParameterizedTest(name = "[{index}: \"{0}\"]")
    @ValueSource(strings = {"-a", "-hello", "--world", "-hello world", "- ", "-\t", "--1"})
    public void testValidFlags(String flag) {
      boolean actual = ArgumentParser.isFlag(flag);
      Assertions.assertTrue(actual, flag);
    }

    /**
     * Tests values that should be considered invalid flags.
     *
     * @param flag invalid flag value
     */
    @Order(2)
    @ParameterizedTest(name = "[{index}: \"{0}\"]")
    @ValueSource(strings = {"a-b-c", "hello", "hello world", "", " ", "\t", "-", " -a", "97", "1", "-1"})
    public void testInvalidFlags(String flag) {
      boolean actual = ArgumentParser.isFlag(flag);
      Assertions.assertFalse(actual, flag);
    }

    /**
     * Tests that null value should be considered an invalid flag.
     */
    @Order(3)
    @Test
    public void testNullFlag() {
      boolean actual = ArgumentParser.isFlag(null);
      Assertions.assertFalse(actual, "null");
    }

    /**
     * Tests a randomly generated string (with letters).
     */
    @Order(4)
    @Test
    public void testRandomStringFlag() {
      Random random = new Random();

      int a = 'a'; // lowercase a codepoint
      int z = 'z'; // lowercase z codepoint

      // https://www.baeldung.com/java-random-string#java8-alphabetic
      String junk = random.ints(5, a, z + 1)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();

      String flag = "-" + junk;

      boolean actual = ArgumentParser.isFlag(flag);
      Assertions.assertTrue(actual, flag);
    }

    /**
     * Tests a randomly generated string (with digits).
     */
    @Order(4)
    @Test
    public void testRandomDigitsFlag() {
      Random random = new Random();

      // https://www.baeldung.com/java-random-string#java8-alphabetic
      String junk = random.ints(5, 0, 10)
          .mapToObj(i -> String.valueOf(i))
          .collect(Collectors.joining());

      String flag = "-" + junk;

      boolean actual = ArgumentParser.isFlag(flag);
      Assertions.assertFalse(actual, flag);
    }
  }

  /**
   * Tests for the {@link ArgumentParser#numFlags()} method.
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class B_CountTests {

    /**
     * Tests count for arguments with a single flag.
     */
    @Order(1)
    @Test
    public void testOneFlag() {
      String[] args = {"-loquat"};
      int expected = 1;
      int actual = new ArgumentParser(args).numFlags();
      assertEquals(expected, actual);
    }

    /**
     * Tests count for arguments with a single flag/value pair.
     */
    @Order(2)
    @Test
    public void testOnePair() {
      String[] args = {"-grape", "raisin"};
      int expected = 1;
      int actual = new ArgumentParser(args).numFlags();
      assertEquals(expected, actual);
    }

    /**
     * Tests count for arguments with two flags.
     */
    @Order(3)
    @Test
    public void testTwoFlags() {
      String[] args = {"-tomato", "-potato"};
      int expected = 2;
      int actual = new ArgumentParser(args).numFlags();
      assertEquals(expected, actual);
    }

    /**
     * Tests count for arguments with a single value.
     */
    @Order(4)
    @Test
    public void testOnlyValue() {
      String[] args = {"rhubarb"};
      int expected = 0;
      int actual = new ArgumentParser(args).numFlags();
      assertEquals(expected, actual);
    }

    /**
     * Tests count for arguments with two values.
     */
    @Order(5)
    @Test
    public void testTwoValues() {
      String[] args = {"constant", "change"};

      int expected = 0;
      int actual = new ArgumentParser(args).numFlags();
      assertEquals(expected, actual);
    }

    /**
     * Tests count for arguments with a non-pair value then flag.
     */
    @Order(6)
    @Test
    public void testPineapple() {
      String[] args = {"pine", "-apple"};

      int expected = 1;
      int actual = new ArgumentParser(args).numFlags();
      assertEquals(expected, actual);
    }

    /**
     * Tests count for arguments with two flag/value pairs.
     */
    @Order(7)
    @Test
    public void testSquash() {
      String[] args = {"-aubergine", "eggplant", "-courgette", "zucchini"};

      int expected = 2;
      int actual = new ArgumentParser(args).numFlags();
      assertEquals(expected, actual);
    }

    /**
     * Tests count for arguments with repeated flags.
     */
    @Order(8)
    @Test
    public void testFruit() {
      String[] args =
          {"-tangerine", "satsuma", "-tangerine", "clementine", "-tangerine", "mandarin"};

      int expected = 1;
      int actual = new ArgumentParser(args).numFlags();
      assertEquals(expected, actual);
    }

    /**
     * Tests count for arguments with no elements.
     */
    @Order(9)
    @Test
    public void testEmpty() {
      String[] args = {};

      int expected = 0;
      int actual = new ArgumentParser(args).numFlags();
      assertEquals(expected, actual);
    }

    /**
     * Tests count for null arguments.
     */
    @Order(10)
    // it is okay to throw a null pointer exception here
    @Test
    public void testNull() {
      String[] args = null;

      assertThrows(java.lang.NullPointerException.class, () -> new ArgumentParser(args).numFlags());
    }
  }

  /**
   * Tests how well the {@link ArgumentParser#parse(String[])} method works.
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class C_ParseTests {

    /** ArgumentParser object being tested */
    private ArgumentParser parser;

    /** String used to output debug messages when tests fail. */
    private String debug;

    /**
     * Sets up the parser object with a single test case.
     */
    @BeforeEach
    public void setup() {
      String[] args = {"", "-a", "42", "-b", "bat", "cat", "-d", "-e", "elk", "-1", "-e", "-f"};

      this.parser = new ArgumentParser();
      this.parser.parse(args);

      this.debug = "\n" + this.parser.toString() + "\n";
    }

    /**
     * Nullifies the parser object after each test.
     */
    @AfterEach
    public void teardown() {
      this.parser = null;
    }

    /**
     * Checks if number of flags is correct for this test case.
     */
    @Order(1)
    @Test
    public void testNumFlags() {
      int expected = 5;
      int actual = this.parser.numFlags();

      assertEquals(expected, actual, this.debug);
    }

    /**
     * Checks if the -d flag was correctly parsed for this test case.
     */
    @Order(2)
    @Test
    public void testHasFlag() {
      assertTrue(this.parser.hasFlag("-d"), this.debug);
    }

    /**
     * Checks if the -f flag was correctly parsed for this test case.
     */
    @Order(3)
    @Test
    public void testHasLastFlag() {
      assertTrue(this.parser.hasFlag("-f"), this.debug);
    }

    /**
     * Checks if the -g flag does not exist as expected.
     */
    @Order(4)
    @Test
    public void testHasntFlag() {
      assertFalse(this.parser.hasFlag("-g"), this.debug);
    }

    /**
     * Checks if the -a value was correctly parsed for this test case.
     */
    @Order(5)
    @Test
    public void testHasValue() {
      assertTrue(this.parser.hasValue("-a"), this.debug);
    }

    /**
     * Checks if the -d value was correctly parsed for this test case.
     */
    @Order(6)
    @Test
    public void testHasFlagNoValue() {
      assertFalse(this.parser.hasValue("-d"), this.debug);
    }

    /**
     * Checks the value for a non-existent flag.
     */
    @Order(7)
    @Test
    public void testNoFlagNoValue() {
      assertFalse(this.parser.hasValue("-g"), this.debug);
    }

    /**
     * Checks if the -b value was correctly parsed for this test case.
     */
    @Order(8)
    @Test
    public void testGetValueExists() {
      String expected = "bat";
      String actual = this.parser.getString("-b");
      assertEquals(expected, actual, this.debug);
    }

    /**
     * Checks if the -d value was correctly parsed for this test case.
     */
    @Order(9)
    @Test
    public void testGetValueNull() {
      String expected = null;
      String actual = this.parser.getString("-d");
      assertEquals(expected, actual, this.debug);
    }

    /**
     * Checks the value for a non-existent flag.
     */
    @Order(10)
    @Test
    public void testGetValueNoFlag() {
      String expected = null;
      String actual = this.parser.getString("-g");
      assertEquals(expected, actual, this.debug);
    }

    /**
     * Checks if the -e flag was correctly parsed for this test case.
     */
    @Order(11)
    @Test
    public void testGetValueRepeatedFlag() {
      String expected = null;
      String actual = this.parser.getString("-e");
      assertEquals(expected, actual, this.debug);
    }

    /**
     * Checks if the -b flag was correctly parsed for this test case.
     */
    @Order(12)
    @Test
    public void testGetDefaultExists() {
      String expected = "bat";
      String actual = this.parser.getString("-b", "bee");
      assertEquals(expected, actual, this.debug);
    }

    /**
     * Checks if the -d flag was correctly parsed for this test case.
     */
    @Order(13)
    @Test
    public void testGetDefaultNull() {
      String expected = "dog";
      String actual = this.parser.getString("-d", "dog");
      assertEquals(expected, actual, this.debug);
    }

    /**
     * Checks if the default value is returned correctly for a non-existent flag.
     */
    @Order(14)
    @Test
    public void testGetDefaultMissing() {
      String expected = "goat";
      String actual = this.parser.getString("-g", "goat");
      assertEquals(expected, actual, this.debug);
    }

    /**
     * Checks that parsing the same arguments twice does not affect count.
     */
    @Order(15)
    @Test
    public void testDoubleParse() {
      String[] args = {"-a", "42", "-b", "bat", "cat", "-d", "-e", "elk", "-e", "-f"};
      this.parser.parse(args);

      int expected = 5;
      int actual = this.parser.numFlags();

      assertEquals(expected, actual, this.debug);
    }
  }

  /**
   * Tests for the {@link ArgumentParser#getPath(String)} method.
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class D_PathTests {

    /**
     * Checks that {@link ArgumentParser#getPath(String)} properly returns path.
     */
    @Order(1)
    @Test
    public void testGetValidPath() {
      String[] args = {"-p", "."};
      ArgumentParser map = new ArgumentParser(args);

      Path expected = Path.of(".");
      Path actual = map.getPath("-p");
      assertEquals(expected, actual);
    }

    /**
     * Checks that {@link ArgumentParser#getPath(String)} returns null as expected.
     */
    @Order(2)
    @Test
    public void testGetInvalidPath() {
      String[] args = {"-p"};
      ArgumentParser map = new ArgumentParser(args);

      Path expected = null;
      Path actual = map.getPath("-p");
      assertEquals(expected, actual);
    }

    /**
     * Checks that {@link ArgumentParser#getPath(String, Path)} returns value properly when
     * flag/value pair exists.
     */
    @Order(3)
    @Test
    public void testGetValidDefaultPath() {
      String[] args = {"-p", "."};
      ArgumentParser map = new ArgumentParser(args);

      Path expected = Path.of(".");
      Path actual = map.getPath("-p", Path.of(".."));
      assertEquals(expected, actual);
    }

    /**
     * Checks that {@link ArgumentParser#getPath(String, Path)} returns value properly when
     * flag/value pair does not exist.
     */
    @Order(4)
    @Test
    public void testGetInvalidDefaultPath() {
      String[] args = {"-p"};
      ArgumentParser map = new ArgumentParser(args);

      Path expected = Path.of(".");
      Path actual = map.getPath("-p", Path.of("."));
      assertEquals(expected, actual);
    }
  }

  /**
   * Imperfect tests to try and determine if the approach may have issues.
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class E_ApproachTests {

    /** Source code loaded as a String object. */
    private String source;

    /**
     * Sets up the parser object with a single test case.
     * 
     * @throws IOException
     */
    @BeforeEach
    public void setup() throws IOException {
      String name = ArgumentParser.class.getSimpleName() + ".java";
      Path path = Path.of(".", "src", name);

      assertTrue(Files.isReadable(path), "Unable to access source code.");
      this.source = Files.readString(path, StandardCharsets.UTF_8);
    }

    /**
     * Tests that the java.io.File class does not appear in the implementation code.
     *
     * @throws IOException
     */
    @Test
    @Order(1)
    public void testFileClass() throws IOException {
      Assertions.assertFalse(source.contains("import java.io.File"), "Do not use the java.io.File class!");
    }

    /**
     * Tests that the java.io.Paths class does not appear in the implementation code.
     *
     * @throws IOException
     */
    @Test
    @Order(2)
    public void testPathsClass() throws IOException {
      Assertions.assertFalse(source.contains("import java.nio.file.Paths"), "Do not use the java.io.file.Paths class!");
    }

    /**
     * Attempts to determine if looping is used improperly. There should only be one loop necessary
     * for the parse method.
     * 
     * @throws IOException
     */
    @Test
    @Order(3)
    public void testLoopCount() throws IOException {
      String regex = "(?i)\\b(for|while)\\b";
      long count = Pattern.compile(regex).matcher(source).results().count();

      assertTrue(count <= 1, "Found " + count + " loops in source code. Only 1 should be necessary.");
    }
  }
}
