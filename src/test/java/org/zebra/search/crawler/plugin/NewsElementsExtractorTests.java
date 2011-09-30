package org.zebra.search.crawler.plugin;

import org.apache.oro.text.regex.*;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.plugin.extractor.*;
import org.zebra.search.crawler.util.ProcessorUtil;

import com.sun.org.apache.xpath.internal.operations.Div;

public class NewsElementsExtractorTests extends PluginTestBase {
	public NewsElementsExtractorTests() {
		super("./testdata/163_7BIHQEKD00253B0H.html");
	}
	public void testTravelHTML() {
		String pageSource = "<!DOCTYPE html PUBLIC -//W3C//DTD XHTML 1.0 Transitional//EN http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd>"
				+ "<html xmlns=http://www.w3.org/1999/xhtml>"
				+ "<head>"
				+ "<meta http-equiv=Content-Type content=\"text/html; charset=gb2312\" />"
				+ "<meta http-equiv=Content-Language content=zh-CN />"
				+ "<title>日本央行宣布维持利率不变 增加资产购买规模_网易财经</title>"
				+ "</head>"
				+ "<body>"
				+ "<span class=colEnd></span><h1 id=h1title>日本央行宣布维持利率不变 增加资产购买规模</h1><span class=info style=display:block;>2011-08-04 13:18:15　来源: <a href=http://money.163.com target=_blank>网易财经</a>　<a href=# id=endpageUrl1 class=cDRed>有<span class=tieTotalCount>0</span>人参与</a> <a href=http://help.3g.163.com/>手机看新闻</a>"
				+ "<div class=wb-article-sharewraper wb-article-sharewraper-sm id=wb-article-sharewraper-sm></div></span>"
				+ "<span class=blank12></span>"
				+ "<div id=endText>"
				+ "<p><b>网易财经8月4日讯</b> 日本央行周四(4日)宣布维持利率在0-0.1%不变，符合预估。此决策获得委员一致通过。日本央行并增加资产购买规模，自10万亿日元提高至15万亿日元。</p><p>日本央行决定进一步放宽货币政策。日本央行表示，将资产购买以及有担保市场操作的资金规模从40万亿日元提高至50万亿日元；将有担保市场操作规模提高至35万亿日元。</p><p>该行补充称，担保市场操作规模提高至35万亿日元。将长期日本国债购买规模提高至4万亿日元。</p><p>日本央行称，日本经济前景变数很大，对日本经济的下行风险需愈发谨慎。</p><p>日本央行指出，欧洲债务问题的紧张形势依旧存在，市场对美国经济前景的谨慎情绪升温，市场仍担忧美国债务问题。日本央行称，源于目前海外形势发展的外汇与资本市场走势，可能会损及日本企业信心与经济活动。</p><p>日本央行特别指出，日本央行行长白川方明的新闻发布会时间改到北京时间周四15:00。</p><p>该行还称，日本实现物价稳定仍需要一段时间。但不确定新兴经济体是否能达到经济增长和物价稳定的目标。</p><br><div class=gg200x300>"
				+ "<iframe src=http://g.163.com/r?site=netease&affiliate=money&cat=article&type=tvscreen200x300&location=1 width=200 height=300 frameborder=no border=0 marginwidth=0 marginheight=0 scrolling=no></iframe>"
				+ "</div><p><b>相关新闻：</b></p><p><a href=http://money.163.com/11/0804/11/7AK1N7AQ00252V5G.html#from=relevant>高盛：日银货币刺激规模料再增加10万亿日元</a></p><p><a href=http://money.163.com/11/0804/11/7AK0Q98F00252V5G.html>日本政府干预汇市 日经225指数早盘收涨0.9%</a></p><p><a href=http://money.163.com/11/0804/10/7AJU0KKR00253B0H.html>日本政府和央行抛售日元 干预汇市阻止日元升值</a></p>"
				+ "<span class=blank6></span>"
				+ "<div class=clearfix>(本文来源：网易财经  )<a href=http://money.163.com/><img src=http://img3.cache.netease.com/stock/2011/7/9/20110709222151be928.gif alt=张学正 width=12 height=11 border=0 class=icon /></a>"
				+ "</div></div>"
				+ "<div><h2>相关新闻</h2></div><div>"
				+ "<ul>"
				+ "<li><a href=http://money.163.com/11/0712/15/78P8QJBL00253B0H.html#from=relevant>日本央行维持利率於0-0.1厘不变</a> <span>2011/07/12</span></li>"
				+ "<li><a href=http://money.163.com/11/0712/14/78P3CD8A00252V5G.html#from=relevant>日本央行维持利率0-0.1%不变 上调经济评估</a> <span>2011/07/12</span></li>"
				+ "<li><a href=http://money.163.com/11/0712/13/78P15HET00253B0H.html#from=relevant>日本维持利率 12日全球市场投资信息</a> <span>2011/07/12</span></li>"
				+ "<li><a href=http://money.163.com/11/0711/08/78LVAC2U00253B0H.html#from=relevant>日本议息 7月11日全球市场投资信息</a> <span>2011/07/11</span></li>"
				+ "<li><a href=http://money.163.com/11/0613/10/76E0P4PG00253B0H.html#from=relevant>[汇市]美元兑日元走高 料日本央行维持低利率不变</a> <span>2011/06/13</span></li>"
				+ "</ul></div><p>网易声明：网易转载上述内容出于传递更多信息之目的，不表明证实其描述或赞同其观点。文章内容仅供参考，不构成投资建议。投资者据此操作，风险自担。</p>"
				+ "</body></html>";
		Page page = new Page(pageSource, "GB2312");
		page.setBaseUrl(doc.getUrl());

		Lexer lexer = new Lexer(page);
		Parser parser = new Parser(lexer);

		try {
			NodeList nodeList = parser.parse(null);
			NodeIterator e = nodeList.elements();
			for (; e.hasMoreNodes();) {
				Node node = e.nextNode();
				printNode(node, 0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void printNode(Node node, int depth) {
		if (node == null) {
			return;
		}
		for (int i = 0; i < depth; i++) {
			System.out.print("\t");
		}
		if (node instanceof Div) {
			System.out.print("t=!Div!");
		}
		if (node instanceof TagNode) {
			System.out.print("t=" + ((TagNode)node).getTagName());
		}
		System.out.println("\t" + node.getText());
		NodeList childrens = node.getChildren();
		if (childrens == null) {
			return;
		}
		NodeIterator e = childrens.elements();
		try {
			while (e.hasMoreNodes()) {
				Node child = e.nextNode();
				printNode(child, depth + 1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void testExtractMainText() {
		Context context = new Context();
		DocumentParser parser = new DocumentParser();
		boolean result = parser.process(doc, context);
		if (!result) {
			fail("failed to process htmlparser");
		}
		MainTextExtractor extractor = new MainTextExtractor();
		NodeList nodeList = (NodeList) context
				.getVariable(ProcessorUtil.COMMON_PROP_NODELIST);
		if (nodeList == null) {
			return;
		}
		String body = extractor.extract(doc, context);
		System.out.println("body=" + body);
	}
}
