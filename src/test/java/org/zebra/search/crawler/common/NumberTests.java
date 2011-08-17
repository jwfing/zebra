package org.zebra.search.crawler.common;

import junit.framework.TestCase;

import org.zebra.search.crawler.analysis.MMSegWrapper;
import org.zebra.search.crawler.clustering.*;

public class NumberTests extends TestCase {
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEnglishDigister() {
		String content = new String("hello world hello you hello me");
		String[] terms = content.split(" ");
		for (String term : terms) {
			System.out.println(term);
		}
		SimHashDigister digister = new SimHashDigister();
		System.out.println(digister.digister(terms));
		terms = "hello world hello you and hello me".split(" ");
		System.out.println(digister.digister(terms));
		terms = "hello world hello you me".split(" ");
		System.out.println(digister.digister(terms));
	}
	public void testChineseDigister() {
		String content1 = "Java语言的输入输出功能是十分强大而灵活的，美中不足的是看上去输入输出的代码并不是很简洁，因为你往往需要包装许多不同的对象。在Java类库中，IO部分的内容是很庞大的，因为它涉及的领域很广泛:标准输入输出，文件的操作，网络上的数据流，字符串流，对象流，zip文件流....本文的目的是为大家做一个简要的介绍";
		String content2 = "流是一个很形象的概念，当程序需要读取数据的时候，就会开启一个通向数据源的流，这个数据源可以是文件，内存，或是网络连接。类似的，当程序需要写入数据的时候，就会开启一个通向目的地的流。这时候你就可以想象数据好像在这其中流动一样，如下图";
		String content3 = "流是很形象的概念，当程序需要读取数据的时候，就会开启一个通向数据源的流，这个数据源可以是文件，内存，或是网络连接。类似的，当程序需要写入数据的时候，就会开启一个通向目的地的流。这时候你就可以想象数据好像在这其中流动一样，如下图";
		MMSegWrapper wrapper = new MMSegWrapper("/host/opensource/mmseg/data");
		try {
			SimHashDigister digister = new SimHashDigister();
			String content1Tokens = wrapper.segWords(content1, " || ");
			String content2Tokens = wrapper.segWords(content2, " || ");
			String content3Tokens = wrapper.segWords(content3, " || ");
			String[] terms = content1Tokens.split(" || ");
			System.out.println(digister.digister(terms));
			terms = content2Tokens.split(" || ");
			System.out.println(digister.digister(terms));
			terms = content3Tokens.split(" || ");
			System.out.println(digister.digister(terms));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
