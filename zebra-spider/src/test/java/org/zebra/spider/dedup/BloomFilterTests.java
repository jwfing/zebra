package org.zebra.spider.dedup;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class BloomFilterTests extends TestCase {
    public void testSerialize() {
        BloomFilter bf = new BloomFilter(125000, 1024);
        String urls[] = {"http://cn.engadget.com/2012/03/09/microsoft-tango-details/",
                "http://cn.engadget.com/category/features/",
                "http://cn.engadget.com/",
                "http://cn.engadget.com/page/2/",
                "http://cn.engadget.com/category/internet/"};
        for (String url : urls) {
            bf.add(url);
        }
        String fileName = "./bf-test";
        File file = new File(fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            DataOutput dos = new DataOutputStream(fos);
            dos.writeInt(bf.getDumpSize());
            dos.writeBytes(bf.toBinary());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException ex) {
            assert(false);
        } catch (IOException ex) {
            assert(false);
        }

        BloomFilter bf2 = new BloomFilter(0, 0);
        try {
            FileInputStream fis = new FileInputStream(fileName);
            DataInput dis = new DataInputStream(fis);
            int dumpSize = dis.readInt();
            byte[] binary = new byte[dumpSize];
            dis.readFully(binary);
            bf2.reload(binary);
            fis.close();
        } catch (FileNotFoundException ex) {
            assert(false);
        } catch (IOException ex) {
            assert(false);
        }
        for (String url : urls) {
            assert(bf2.contains(url));
        }

    }
}
