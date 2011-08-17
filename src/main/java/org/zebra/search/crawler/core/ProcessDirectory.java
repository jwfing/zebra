package org.zebra.search.crawler.core;

public class ProcessDirectory {
	private static enum DIRECTORY {
		LIST,
		CONTENT,
		USR1,
		USR2,
		USR3
	};
	public final static ProcessDirectory LIST_PAGE = new ProcessDirectory(DIRECTORY.LIST);
	public final static ProcessDirectory CONTENT_PAGE = new ProcessDirectory(DIRECTORY.CONTENT);
	public final static ProcessDirectory USR1_PAGE = new ProcessDirectory(DIRECTORY.USR1);
	public final static ProcessDirectory USE2_PAGE = new ProcessDirectory(DIRECTORY.USR2);
	public final static ProcessDirectory USE3_PAGE = new ProcessDirectory(DIRECTORY.USR3);

	private DIRECTORY _directory = DIRECTORY.LIST;
	private ProcessDirectory(DIRECTORY i) {
		_directory = i;
	}
}
