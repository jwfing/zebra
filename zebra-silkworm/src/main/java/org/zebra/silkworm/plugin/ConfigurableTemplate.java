package org.zebra.silkworm.plugin;

import java.util.*;
import java.io.*;

import org.apache.commons.digester.BeanPropertySetterRule;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.SetNextRule;
import org.apache.commons.digester.SetPropertiesRule;
import org.apache.log4j.Logger;

public class ConfigurableTemplate {
	private final Logger logger = Logger.getLogger(ConfigurableTemplate.class);
	static public class RuleItem {
		private String regUrl = "";
		private String tag = "";
		private String beginPattern = "";
		private String endPattern = "";
		private Map<String, String> attrs = new HashMap<String, String>();
		public Map<String, String> getAttrs() {
			return attrs;
		}
		public void setAttrs(Map<String, String> attrs) {
			this.attrs = attrs;
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
		public String getRegUrl() {
			return regUrl;
		}
		public void setRegUrl(String regUrl) {
			this.regUrl = regUrl;
		}
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			if (null == tag || tag.isEmpty()) {
				return;
			}
			String[] attrs = tag.split(" ");
			this.tag = attrs[0];
			for (int i = 1; i < attrs.length; i++) {
				String[] kv = attrs[i].split("=");
				if (kv.length == 2) {
					this.attrs.put(kv[0], kv[1]);
				}
			}
		}
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("regUrl=" + this.regUrl + ", tag=" + this.tag);
			for (Map.Entry<String, String> entry : attrs.entrySet()) {
				sb.append(", " + entry.getKey() + "=" + entry.getValue());
			}
			return sb.toString();
		}
	}

	protected List<RuleItem> rules;
	protected Digester digester = new Digester();

	public ConfigurableTemplate() {
		Rules rules = digester.getRules();
		rules.add("rules-template/site", new ObjectCreateRule(
				ConfigurableTemplate.RuleItem.class));
		rules.add("rules-template/site",
						new SetNextRule("add", "java.lang.Object"));
		rules.add("rules-template/site", new SetPropertiesRule());

		rules.add("rules-template/site/regurl", new BeanPropertySetterRule("regUrl"));
		rules.add("rules-template/site/tag", new BeanPropertySetterRule("tag"));
		rules.add("rules-template/site/beginPattern", new BeanPropertySetterRule("beginPattern"));
		rules.add("rules-template/site/endPattern", new BeanPropertySetterRule("endPattern"));
	}

	public boolean load(String path) {
		try {
			InputStream input = this.getClass().getClassLoader().getResourceAsStream(path);
			List<RuleItem> result = new ArrayList<RuleItem>();
			this.digester.push(result);
			this.digester.parse(input);
			input.close();
			this.rules = result;
			logger.info("read contents from configuration file: " + path);
			for (RuleItem item : this.rules) {
			    logger.info("rule item: " + item.toString());
			}
		} catch (Exception ex) {
			logger.warn("failed to load config file: " + path + ", cause: " + ex.getMessage());
			return false;
		}
		return true;
	}

	public List<RuleItem> getRules() {
		return rules;
	}

	public void setRules(List<RuleItem> rules) {
		this.rules = rules;
	}
}
