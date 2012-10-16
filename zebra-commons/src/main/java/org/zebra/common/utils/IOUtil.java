package org.zebra.common.utils;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */


public final class IOUtil {

	public static boolean deleteFolder(File folder) {
		return deleteFolderContents(folder) && folder.delete();
	}
	
	public static boolean deleteFolderContents(File folder) {
		System.out.println("Deleting content of: " + folder.getAbsolutePath());
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				if (!file.delete()) {
					return false;
				}
			} else {
				if (!deleteFolder(file)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void writeBytesToFile(byte[] bytes, String destination) {
		try {
			FileChannel fc = new FileOutputStream(destination).getChannel();
			fc.write(ByteBuffer.wrap(bytes));
			fc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
