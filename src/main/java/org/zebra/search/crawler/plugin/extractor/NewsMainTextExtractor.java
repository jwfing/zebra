package org.zebra.search.crawler.plugin.extractor;

import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.htmlparser.Node;  
import org.htmlparser.NodeFilter;  
import org.htmlparser.Parser;  
import org.htmlparser.filters.NodeClassFilter;  
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.LinkTag;  
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;  
import org.htmlparser.util.ParserException;  
import org.htmlparser.visitors.HtmlPage;  

import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.util.ProcessorUtil;

public class NewsMainTextExtractor {
	private final Logger logger = Logger.getLogger(NewsMainTextExtractor.class);
	private String tagName = "";
	private String beginPattern = "";
	private String endPattern = "";
	private Map<String, String> hasAttributes = new HashMap<String, String>();

	public NewsMainTextExtractor() {
		// for 163 template
		tagName = "Div";
		hasAttributes.put("id", "endText");
	}

	public String getBeginPattern() {
		return beginPattern;
	}
	public void setBeginPattern(String beginPattern) {
		this.beginPattern = beginPattern;
	}
	public String getEndPattern() {
		return endPattern;
	}
	public void setEndPattern(String endPattern) {
		this.endPattern = endPattern;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public void setAttributeFilters(Map<String, String> filters) {
		if (null != filters) {
			this.hasAttributes = filters;
		}
	}

	public void addAttributeFilters(String name, String value) {
		this.hasAttributes.put(name, value);
	}

	private List<Node> findTargetNode(Node parent) {
		List<Node> result = new ArrayList<Node>();
		if (null != parent) {
			if (nodeIsWanted(parent)) {
				result.add(parent);
			}
			NodeList childrens = parent.getChildren();
			if (null != childrens) {
				List<Node> tmpResult = null;
				NodeIterator e = childrens.elements();
				try {
					while(e.hasMoreNodes()) {
						Node child = e.nextNode();
						tmpResult = findTargetNode(child);
						if (null != tmpResult && tmpResult.size() > 0) {
							result.addAll(tmpResult);
						}
					}
				} catch (ParserException ex) {
					ex.printStackTrace();
				}
			}
		}
		return result;
	}
	private boolean nodeIsWanted(Node node) {
		boolean result = false;
		if (null != node && node instanceof TagNode) {
			TagNode tag = (TagNode)node;
			if (this.tagName.equalsIgnoreCase(tag.getTagName())) {
			    Set<Entry<String, String> > entries = this.hasAttributes.entrySet();
			    result = true;
			    for (Entry<String, String> entry : entries) {
			    	String attr = tag.getAttribute(entry.getKey());
			    	if (entry.getValue().equalsIgnoreCase(attr)) {
			    		continue;
			    	} else {
			    		result = false;
			    		break;
			    	}
			    }
			}
		}
		return result;
	}

	public String extract(CrawlDocument doc, Context context) {
		String result = "";
		if (null == doc || null == context) {
			return result;
		}
		NodeList nodeList = (NodeList) context
				.getVariable(ProcessorUtil.COMMON_PROP_NODELIST);
		if (nodeList == null) {
			logger.debug("the node list is null");
			return result;
		}
		List<Node> wantedNodes = new ArrayList<Node>();
		for (Node node : nodeList.toNodeArray()) {
			List<Node> tmpNodes = findTargetNode(node);
			if (null != tmpNodes && tmpNodes.size() > 0) {
				wantedNodes.addAll(tmpNodes);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (Node node : wantedNodes) {
			sb.append(node.toPlainTextString().trim());
		}
		result = sb.toString();
		return result;
	}
}
