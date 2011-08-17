package org.zebra.search.crawler.urlPool.storage;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public final class URLTupleBinding extends TupleBinding{
	@Override
	public UrlSeed entryToObject(TupleInput input) {
		UrlSeed webURL = new UrlSeed();
		webURL.setUrl(input.readString());
		webURL.setLevel(input.readInt());
		webURL.setFeatures(input.readString());
		return webURL;
	}

	@Override
	public void objectToEntry(Object url, TupleOutput output) {
		if (null == url || !(url instanceof UrlSeed)) {
			return;
		}
		UrlSeed seed = (UrlSeed)url;
		output.writeString(seed.getUrl());
		output.writeInt(seed.getLevel());
		output.writeString(seed.getFeatures());
	}

}
