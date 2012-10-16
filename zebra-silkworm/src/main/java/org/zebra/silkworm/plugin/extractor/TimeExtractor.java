package org.zebra.silkworm.plugin.extractor;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.*;
import org.htmlparser.util.*;
import org.apache.oro.text.regex.*;

import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.util.ProcessorUtil;

public class TimeExtractor {
	private final Logger logger = Logger.getLogger(TimeExtractor.class);
	private static final String TIME_REGEX_PATTERN =
		"([0-9]{2,4})年(\\s)*(0?[1-9]|1[0-2])月(\\s)*([0-2]?[0-9]|3[01])(日|号)(\\s)*([01]?[0-9]|2[0-3])?((:|时|点)(\\s)*([0-5]?[0-9]))?((:|分)(\\s)*([0-5]?[0-9]))?|([0-9]{2,4})-(\\s)*(0?[1-9]|1[0-2])-(\\s)*([0-2]?[0-9]|3[01])(\\s)*([01]?[0-9]|2[0-3])?(:([0-5]?[0-9]))?(:([0-5]?[0-9]))?|([0-9]{2,4})/(\\s)*(0?[1-9]|1[0-2])/(\\s)*([0-2]?[0-9]|3[01])(\\s)*([01]?[0-9]|2[0-3])?(:([0-5]?[0-9]))?(:([0-5]?[0-9]))?|([0-9]{2,4})\\.(\\s)*(0?[1-9]|1[0-2])\\.(\\s)*([0-2]?[0-9]|3[01])(\\s)+([01]?[0-9]|2[0-3])(:([0-5]?[0-9]))(:([0-5]?[0-9]))?";
	String TIME_REGEX_PATTERN1 = "([0-9]{2,4})年(\\s)*(0?[1-9]|1[0-2])月(\\s)*([0-2]?[0-9]|3[01])(日|号)(\\s)*" +
		"([01]?[0-9]|2[0-3])?((:|时|点)(\\s)*([0-5]?[0-9]))?((:|分)(\\s)*([0-5]?[0-9]))?.*";
	String TIME_REGEX_PATTERN2 = "([0-9]{2,4})-(\\s)*(0?[1-9]|1[0-2])-(\\s)*([0-2]?[0-9]|3[01])(\\s)*" +
		"([01]?[0-9]|2[0-3])?(:([0-5]?[0-9]))?(:([0-5]?[0-9]))?.*";
	String TIME_REGEX_PATTERN3 = "([0-9]{2,4})/(\\s)*(0?[1-9]|1[0-2])/(\\s)*([0-2]?[0-9]|3[01])(\\s)*" +
		"([01]?[0-9]|2[0-3])?(:([0-5]?[0-9]))?(:([0-5]?[0-9]))?.*";

	private PatternCompiler orocom = new Perl5Compiler();
	private Pattern pattern1 = null;
	private Pattern pattern2 = null;
	private Pattern pattern3 = null;
	private PatternMatcher pm = new Perl5Matcher();
	public TimeExtractor() {
		try {
		    pattern1 = orocom.compile(TIME_REGEX_PATTERN1);
		    pattern2 = orocom.compile(TIME_REGEX_PATTERN2);
		    pattern3 = orocom.compile(TIME_REGEX_PATTERN3);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String getTimeString(String str) {
		if (null == str)
			return "";
		String[] allLines = str.split("\n");
		String candidate = null;
		for (String line : allLines) {
			if (line.trim().isEmpty()) {
				continue;
			}
			candidate = line.trim();
			break;
		}
		String[] allSections = candidate.split("\\s|　");
		candidate = allSections[0];
		if (allSections.length > 1) {
			if (allSections[1].matches("^[0-9].*")) {
				candidate += " " + allSections[1];
			}
		}
		return candidate.trim();
	}
    public String extract(CrawlDocument doc, Context context) {
    	if (null == doc || null == context) {
    		return "";
    	}
		NodeList nodeList = (NodeList) context.getVariable(ProcessorUtil.COMMON_PROP_NODELIST);
		if (nodeList == null) {
			logger.debug("the node list is null");
			return "";
		}
		// just for 163.com
		NodeFilter linkFilters = new AndFilter(new TagNameFilter("span"),
				new OrFilter(new HasAttributeFilter("class", "info"),
						new HasAttributeFilter("id", "pub_date")));
		NodeList candidates = nodeList.extractAllNodesThatMatch(linkFilters, true);
		if (null == candidates || candidates.size() < 1) {
			return "";
		}
		String result = "";
		for (int i = 0; i < candidates.size(); i++) {
			Node node = candidates.elementAt(i);
			String plainText = getTimeString(node.toPlainTextString());
			if (!plainText.isEmpty()) {
				if (plainText.matches(TIME_REGEX_PATTERN1)
					|| plainText.matches(TIME_REGEX_PATTERN2)
					|| plainText.matches(TIME_REGEX_PATTERN3)) {
					if (plainText.length() > result.length()) {
						result = plainText;
					}
					continue;
				}
			}
		}

    	return result;
    }

}
