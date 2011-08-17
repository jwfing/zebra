package org.zebra.search.crawler.analysis;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.chenlb.mmseg4j.Chunk;
import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.Chunk.Word;

public class MMSegWrapper {

	protected Dictionary dic = null;

	public MMSegWrapper(String dicPath) {
		dic = new Dictionary(dicPath);
	}

	public MMSegWrapper(Dictionary dic) {
		this.dic = dic;
	}

	protected Seg getSeg() {
		return new ComplexSeg(dic);
	}

	public String segWords(Reader input, String wordSpilt) throws IOException {
		StringBuilder sb = new StringBuilder();
		Seg seg = getSeg();
		MMSeg mmSeg = new MMSeg(input, seg);
		Chunk chunk = null;
		boolean first = true;
		while ((chunk = mmSeg.next()) != null) {
			for (int i = 0; i < chunk.getCount(); i++) {
				Word word = chunk.getWords()[i];
				if (!first) {
					sb.append(wordSpilt);
				}
				String w = word.getString();
				sb.append(w);
				first = false;
			}
		}
		return sb.toString();
	}

	public String segWords(String txt, String wordSpilt) throws IOException {
		return segWords(new StringReader(txt), wordSpilt);
	}
}