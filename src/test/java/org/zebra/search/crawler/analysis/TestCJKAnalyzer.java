package org.zebra.search.crawler.analysis;

import junit.framework.TestCase;

import java.io.Reader;
import java.io.StringReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Version;

import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;

public class TestCJKAnalyzer extends TestCase {
    private static String testString1 = "中华人民共和国在1949年建立，从此开始了新中国的伟大篇章." +
    		"比尔盖茨从事餐饮业和服务业方面的工作.";

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
    public static void testStandard(){
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_33);   
        Reader r = new StringReader(testString1);      
        System.err.println("=====standard analyzer====");
        System.err.println("分析方法：默认没有词只有字");
        TokenStream ts = analyzer.tokenStream("", r);
        TermAttribute termAtt = (TermAttribute)ts.getAttribute(TermAttribute.class);
        TypeAttribute typeAtt = (TypeAttribute)ts.getAttribute(TypeAttribute.class);
        try {
	        while(ts.incrementToken()) {
	        	System.out.println("term: " + termAtt.term() + ", type: " + typeAtt.type());
	        }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    public static void testCJK(){
        Analyzer analyzer = new CJKAnalyzer(Version.LUCENE_33);
        Reader r = new StringReader(testString1);      
        System.err.println("=====cjk analyzer====");
        System.err.println("分析方法:交叉双字分割");
        TokenStream ts = analyzer.tokenStream("", r);
        TermAttribute termAtt = (TermAttribute)ts.getAttribute(TermAttribute.class);
        TypeAttribute typeAtt = (TypeAttribute)ts.getAttribute(TypeAttribute.class);
        try {
	        while(ts.incrementToken()) {
	        	System.out.println("term: " + termAtt.term() + ", type: " + typeAtt.type());
	        }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    public static void testChinese(){
        Analyzer analyzer = new ChineseAnalyzer();
        Reader r = new StringReader(testString1);      
        System.err.println("=====Chinese analyzer====");
        TokenStream ts = analyzer.tokenStream("", r);
        TermAttribute termAtt = (TermAttribute)ts.getAttribute(TermAttribute.class);
//        TypeAttribute typeAtt = (TypeAttribute)ts.getAttribute(TypeAttribute.class);
        try {
	        while(ts.incrementToken()) {
	        	System.out.println("term: " + termAtt.term());
	        }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
}
