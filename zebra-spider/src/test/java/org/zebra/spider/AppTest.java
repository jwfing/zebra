package org.zebra.spider;

import java.util.regex.Pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        String url = "http://www.baidu.com tags=Travel strict=abc";
        String[] parts = url.split("\\s");
        for (String part : parts) {
            System.out.println(part);
        }
        System.out.println(parts.length + " " + parts[0]);
        
        Pattern pattern = Pattern.compile("http://www\\.mafengwo\\.cn/i/\\d*\\.html");
        String[] urls = {"http://www.mafengwo.cn/i/703988.html"};
        for (String urlTest : urls) {
            if (Pattern.matches("http://www\\.mafengwo\\.cn/i/\\d*\\.html", urlTest)) {
                System.out.println(urlTest + " is good");
            } else {
                System.out.println(urlTest + " is bad");
            }
        }
        assertTrue( true );
    }
}
