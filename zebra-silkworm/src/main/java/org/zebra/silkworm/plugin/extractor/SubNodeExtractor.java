package org.zebra.silkworm.plugin.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.zebra.common.*;
import org.zebra.common.utils.*;
import org.zebra.silkworm.plugin.ConfigurableTemplate.RuleItem;

public class SubNodeExtractor {
	private final Logger logger = Logger.getLogger(SubNodeExtractor.class);
	private List<RuleItem> rules;

	public List<RuleItem> getRules() {
		return rules;
	}
	public void setRules(List<RuleItem> rules) {
		this.rules = rules;
	}
	protected List<Node> findTargetNode(RuleItem rule, Node parent) {
		List<Node> result = new ArrayList<Node>();
		if (null != parent) {
			if (nodeIsWanted(rule, parent)) {
				result.add(parent);
			}
			NodeList childrens = parent.getChildren();
			if (null != childrens) {
				List<Node> tmpResult = null;
				NodeIterator e = childrens.elements();
				try {
					while(e.hasMoreNodes()) {
						Node child = e.nextNode();
						tmpResult = findTargetNode(rule, child);
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
	protected boolean nodeIsWanted(RuleItem rule, Node node) {
		boolean result = false;
		String tagName = rule.getTag();
		Map<String, String> hasAttributes = rule.getAttrs();
		if (null != node && node instanceof TagNode) {
			TagNode tag = (TagNode)node;
			if (tagName.equalsIgnoreCase(tag.getTagName())) {
			    Set<Entry<String, String> > entries = hasAttributes.entrySet();
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
		String host = UrlUtil.getHostFromUrl(doc.getUrl());
		RuleItem targetRule = null;
		for(RuleItem item : rules) {
			if (host.matches(item.getRegUrl())) {
				targetRule = item;
				break;
			}
		}
		if (targetRule == null) {
			logger.warn("rule is poor for host:" + host);
			return result;
		}
		List<Node> wantedNodes = new ArrayList<Node>();
		for (Node node : nodeList.toNodeArray()) {
			List<Node> tmpNodes = findTargetNode(targetRule, node);
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
