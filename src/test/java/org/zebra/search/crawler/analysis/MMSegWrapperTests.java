package org.zebra.search.crawler.analysis;

import junit.framework.TestCase;

public class MMSegWrapperTests extends TestCase {
	private static String testString1 = "中华人民共和国在1949年建立，从此开始了新中国的伟大篇章."
			+ "比尔盖茨从事餐饮业和服务业方面的工作.";
	private static String testString2 = "today,…………i'am chenlb,<《公主小妹》>?我@$#%&*()$!!,";
	private static String testString3 = "Based on the novel by Gail Carson Levine, Ella Enchanted " +
			"reimagines the Cinderella story in a medieval world of elves, ogres, fairies and magic but also " +
			"replete with droll anachronisms such as a wooden \\“escalator\\” powered by peasants turning a wheel." +
			" There is understated sword violence and talk of ogres ripping someone apart. The ogres are huge and " +
			"scary at first, with partially bare behinds. Ella is played by the talented Anne Hathaway.";
	private static String testString4 = "为确保高铁运营绝对安全，铁道部决定，全面提高高铁设备质量，抓好高铁信号设备、动车组、" +
			"供电系统设备的隐患整治和养护维修，加强高铁线路以及防灾系统的维护管理，保证设备质量处于良好状态；提高高铁运输组织水平，" +
			"实现高铁调度对行车安全的精细化管理，加强非正常情况下的行车组织工作，确保列车运行安全；提高高铁应急处置能力，加快推进高铁" +
			"防灾体系建设，以防范火灾、雨雪冰冻、大风大雾和地质变化为重点，加强对防灾设备设施的维护，充分发挥防灾系统的作用";
	private static String testString5 = "菊花茶，青花瓷，高铁侠,温家宝要求猪肉价格稳定，强调宏观调控取向不变";

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testChineseString() {
		MMSegWrapper wrapper = new MMSegWrapper("/host/opensource/mmseg/data");
		try {
		    System.out.println(wrapper.segWords(testString1, " | "));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void testComplexString() {
		MMSegWrapper wrapper = new MMSegWrapper("/host/opensource/mmseg/data");
		try {
		    System.out.println(wrapper.segWords(testString2, " | "));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void testEnglishString() {
		MMSegWrapper wrapper = new MMSegWrapper("/host/opensource/mmseg/data");
		try {
		    System.out.println(wrapper.segWords(testString3, " | "));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void testLongText() {
		MMSegWrapper wrapper = new MMSegWrapper("/host/opensource/mmseg/data");
		try {
		    System.out.println(wrapper.segWords(testString4, " | "));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void testArbitraryText() {
		MMSegWrapper wrapper = new MMSegWrapper("/host/opensource/mmseg/data");
		try {
		    System.out.println(wrapper.segWords(testString5, " | "));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
