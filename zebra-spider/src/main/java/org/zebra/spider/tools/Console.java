package org.zebra.spider.tools;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.zebra.common.Configuration;
import org.zebra.common.domain.dao.SeedDao;

public class Console {
    private final static String PREFIX_MSG = "-";
    private final static String HELP_MSG = "-help";
    private final static String VERS_MSG = "-version";
    private final static String FILE_MSG = "-localfile";
    private final static String IMPORT_CMD = "import";
    private final static String EXPORT_CMD = "export";
    private final static String CLEAR_CMD = "clear";
    private final static int IMPORT_TYPE = 0x0001;
    private final static int EXPORT_TYPE = 0x0002;
    private final static int CLEAR_TYPE = 0x0004;
    private final static String CONFIG_PATH;
    static {
        String properties = System.getProperty("core.properties");
        if (null == properties || properties.isEmpty()) {
            CONFIG_PATH = "spider_tools_context.xml";
        } else {
            CONFIG_PATH = properties;
        }
    }

    private static void usage() {
        System.out.println("usage: console [-help] [-version] COMMAND ARGS");
        System.out.println("The most commonly used console commands are:");
        System.out.println("	import	import regular seeds to urlPool.");
        System.out.println("	export	export seeds from urlPool.");
        System.out.println("	clear	delete all seeds from urlPool.");
        System.out.println("");
        System.out.println("for import command, the useage is:");
        System.out.println("	console import -localfile local_file");
        System.out.println("OPTIONS:");
        System.out.println("	localfile	local seed file.");
        System.out.println("");
        System.out.println("for export command, the useage is:");
        System.out.println("	console export [-localfile local_file]");
        System.out.println("OPTIONS:");
        System.out.println("	localfile	local seed file.");
        System.out.println("");
        System.out.println("for clear command, the useage is:");
        System.out.println("	console clear");
        System.out.println("OPTIONS:");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        boolean helpFlag = false;
        boolean versionFlag = false;
        String localFile = "";
        boolean invalidOption = false;
        int opType = 0;
        String errorMsg = "";
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith(PREFIX_MSG)) {
                if (args[i].equalsIgnoreCase(HELP_MSG)) {
                    helpFlag = true;
                } else if (args[i].equalsIgnoreCase(VERS_MSG)) {
                    versionFlag = true;
                } else if (args[i].equalsIgnoreCase(FILE_MSG)) {
                    if (i < args.length - 1) {
                        localFile = args[i + 1];
                        ++i;
                    } else {
                        errorMsg = "Must specific file path for option: " + FILE_MSG;
                        invalidOption = true;
                        break;
                    }
                } else {
                    errorMsg = "unknown option: " + args[i];
                    invalidOption = true;
                    break;
                }
            } else if (args[i].equalsIgnoreCase(IMPORT_CMD)) {
                opType |= IMPORT_TYPE;
            } else if (args[i].equalsIgnoreCase(EXPORT_CMD)) {
                opType |= EXPORT_TYPE;
            } else if (args[i].equalsIgnoreCase(CLEAR_CMD)) {
                opType |= CLEAR_TYPE;
            } else {
                errorMsg = "unknown COMMAND: " + args[i];
                invalidOption = true;
                break;
            }
        }
        if (helpFlag) {
            usage();
            return;
        }
        if (versionFlag) {
            System.out.println("zebra_spider 1.0.0");
            return;
        }
        if (invalidOption) {
            System.err.println(errorMsg);
            System.out.println("type 'console -help' for more information.");
            return;
        }
        if (opType != IMPORT_TYPE && opType != EXPORT_TYPE && opType != CLEAR_TYPE) {
            System.err.println("COMMAND(import/export/clear) is mandotary!");
            System.out.println("usage: console [-help] [-version] COMMAND ARGS");
            System.out.println("type 'console -help' for more information.");
            return;
        }
        if (opType == IMPORT_TYPE && localFile.isEmpty()) {
            System.err.println("for import COMMAND, localfile option is mandotary!");
            System.out.println("	console import -localfile local_file");
            System.out.println("type 'console -help' for more information.");
            return;
        }
        int result = 0;
        ApplicationContext appContext = new ClassPathXmlApplicationContext(CONFIG_PATH);
        SeedDao dao = (SeedDao) appContext.getBean("seedDAO");
        if (null == dao) {
            System.err.println("configuration(" + CONFIG_PATH + ") is invalid. SeedDao is null");
            System.exit(-1);
        }
        if (opType == IMPORT_TYPE) {
            System.out.println("begin to import seed from localfile " + localFile);
            ImportSeed importCmd = new ImportSeed(dao);
            result = importCmd.execute(localFile);
        } else if (opType == EXPORT_TYPE) {
            System.out.println("begin to export seed to localfile " + localFile);
            ListSeed exportCmd = new ListSeed(dao);
            result = exportCmd.execute(localFile);
        } else if (opType == CLEAR_TYPE) {
            System.out.println("begin to clear seed");
            ClearSeed clearCmd = new ClearSeed(dao);
            result = clearCmd.execute();
        }
        if (result != 0) {
            System.out.println("error occured when modify the url pool, please send the log file to xxx");
        }
        return;
    }

}
