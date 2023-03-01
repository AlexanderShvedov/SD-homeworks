import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CommandLineTest {

    @Test
    public void testEcho() {
        CommandLine testCommandLine = new CommandLine(true);
        assertEquals(testCommandLine.testParser("echo Hello, World!"), "Hello, World!");
        assertEquals(testCommandLine.testParser("echo Hello, | echo World!"), "Hello, World!");
        assertEquals(testCommandLine.testParser("x=Hello, y=World! echo $x $y"), "Hello, World!");
        assertEquals(testCommandLine.testParser("echo 2 | echo + | echo 2 | echo = | echo 4"), "2 + 2 = 4");
    }

    @Test
    public void testCat() {
        CommandLine testCommandLine = new CommandLine(true);
        assertEquals(testCommandLine.testParser("cat FilesForTests/File1.txt"), "Hello, World!");
        assertEquals(testCommandLine.testParser("cat FilesForTests/File1.txt | echo"), "Hello, World!");
        assertEquals(testCommandLine.testParser("FILE=FilesForTests/File1.txt cat $FILE"), "Hello, World!");
        assertEquals(testCommandLine.testParser("echo FilesForTests/File2.txt | cat"), "I love Software Design!");
        assertEquals(testCommandLine.testParser("cat FilesForTests/File2.txt | echo And Java too!"), "I love Software Design! And Java too!");
    }

    @Test
    public void testWc() {
        CommandLine testCommandLine = new CommandLine(true);
        assertEquals(testCommandLine.testParser("wc FilesForTests/File1.txt"), "1 2 13");
        assertEquals(testCommandLine.testParser("echo FilesForTests/File2.txt | wc | echo and me"), "1 4 23 and me");
    }
}