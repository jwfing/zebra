package org.zebra.search.crawler.core;

import java.util.List;
import org.springframework.context.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.UrlInfo;
import org.zebra.search.crawler.fetcher.HttpClientFetcher;
import org.zebra.search.crawler.plugin.DocumentParser;
import org.zebra.search.crawler.plugin.LinkFollower;
import org.zebra.search.crawler.plugin.DeduperClient;
import org.zebra.search.crawler.plugin.RulesetFilter;
import org.zebra.search.crawler.util.ProcessorUtil;

import junit.framework.TestCase;

public class LinkFollowerTests extends TestCase {
    private HttpClientFetcher fetcher = new HttpClientFetcher();
    private static boolean initialized = false;
    private DocumentParser parser;
    private LinkFollower follower;
    private RulesetFilter filter = new RulesetFilter();
    private DeduperClient deduper = new DeduperClient();

    protected void setUp() throws Exception {
        parser = new DocumentParser();
        parser.initialize();
        follower = new LinkFollower();
        follower.initialize();
        super.setUp();
        if (!initialized) {
            HttpClientFetcher.startConnectionMonitorThread();
            initialized = true;
        }
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected void downloadAndParse(UrlInfo urlInfo, boolean recurve) {
        CrawlDocument doc = fetcher.fetchDocument(urlInfo);
        if (null == doc || null == doc.getContentString()) {
            return;
        }
        Context context = new Context();
        boolean result = parser.process(doc, context);
        if (!result) {
            return;
        }
        result = follower.process(doc, context);
        if (!result) {
            return;
        }
        filter.process(doc, context);
        deduper.process(doc, context);
        List<UrlInfo> outlinks = (List<UrlInfo>) context
                .getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
        for (UrlInfo link : outlinks) {
            System.out.println(link.getUrl());
            if (recurve) {
                downloadAndParse(link, false);
            }
        }
    }

    public void testHomeRepair() {
        System.out.println("************DIY Home Repair*************");
        String urls[] = {
                "http://www.homeconstructionimprovement.com/",
                "http://www.apartmenttherapy.com/",
                "http://www.oneprojectcloser.com/",
                "http://cindydole.com/",
                "http://www.ask-the-electrician.com/",
                "http://www.askthebuilder.com/",
                "http://thishomesweethome.blogspot.com/",
                "http://www.russetstreetreno.com/",
                "http://www.electrical-online.com/",
                "http://www.doorsixteen.com/",
                "https://www.dannylipford.com",
                "http://www.oldhouseweb.com/",
                "http://ecofriendlyhomemaking.com/",
                "http://www.renovateaustralia.com/",
                "http://retrorenovation.com/"};
        for (String url : urls) {
            UrlInfo urlInfo = new UrlInfo(url);
            downloadAndParse(urlInfo, true);
        }
    }
    public void testCivilWar() {
        System.out.println("************Civil War Here I come*************");
        String urls[] = {
                "http://www.civilwarbaptists.com/",
                "http://cenantua.wordpress.com/",
                "http://renegadesouth.wordpress.com/",
                "http://gettysburgcwi.posterous.com/",
                "http://dclawyeronthecivilwar.blogspot.com",
                "http://civilwarhistorian.wordpress.com/",
                "http://civilwarnavy.blogspot.com/",
                "http://civilwarnavy150.blogspot.com/",
                "http://www.civilwar.org/",
                "http://civilwarcavalry.com/",
                "http://www.civilwarconnect.com/",
                "http://sablearm.blogspot.com/",
                "http://www.nccivilwar150.com/",
                "http://acwarproject.wordpress.com/",
                "http://www.teachthecivilwar.com/"};
        for (String url : urls) {
            UrlInfo urlInfo = new UrlInfo(url);
            downloadAndParse(urlInfo, true);
        }
    }
    public void testStreetArt() {
        System.out.println("************Street Art*************");
        String urls[] = {
                "http://www.robotswillkill.com/",
                "http://blog.vandalog.com/",
                "http://dayinthelyfe.com/",
                "http://theseventhletter.com/",
                "http://www.artofthestate.co.uk/",
                "http://mikegiant.com/",
                "http://www.woostercollective.com/",
                "http://www.urbanartcore.eu/",
                "http://hurtyoubad.com/",
                "http://saberone.com/",
                "http://www.sillypinkbunnies.com/",
                "http://www.brooklynstreetart.com/",
        };
        for (String url: urls) {
            UrlInfo urlInfo = new UrlInfo(url);
            downloadAndParse(urlInfo, true);
        }
    }
    public void testBicycling() {
        System.out.println("************Bicycling*************");
        String urls[] = {
                "http://bikesnobnyc.blogspot.com/",
                "http://www.fatcyclist.com/",
                "http://www.cyclingtipsblog.com/",
                "http://www.cyclelicio.us",
                "http://bikeportland.org/",
                "http://bikeporntour.blogspot.com/",
                "http://www.ecovelo.info/",
                "http://www.copenhagenize.com",
                "http://www.commutebybike.com/",
                "http://www.bikehugger.com/",
                "http://www.bikeblognyc.com/",
                "http://www.pinkbike.com/",
        };
        for (String url: urls) {
            UrlInfo urlInfo = new UrlInfo(url);
            downloadAndParse(urlInfo, true);
        }
    }
    public void testMacAholics() {
        System.out.println("************Mac-aholics*************");
        String urls[] = {
                "http://www.macrumors.com",
                "http://9to5mac.com",
                "http://theverge.com",
                "http://www.ilounge.com",
                "http://www.cultofmac.com",
                "http://www.daringfireball.net",
                "http://macdailynews.com",
                "http://appleinsider.com/",
                "http://theipadguide.com",
                "http://blog.windonaleaf.net",
                "http://peter.boctor.net",
                "http://www.info-mac.org",
        };
        for (String url: urls) {
            UrlInfo urlInfo = new UrlInfo(url);
            downloadAndParse(urlInfo, true);
        }
    }
    public void testTattoos() {
        System.out.println("************Tattoos*************");
        String urls[] = {
                "http://www.mdtattoos.com",
                "http://www.mikedemasi.com/",
                "http://jamieleeparker.com",
                "http://www.jjtattoos.com",
                "http://fyeahtattoos.com",
                "http://www.maxdolberg.com/",
                "http://tattooroadtrip.com/blog/",
                "http://ugliesttattoos.failblog.org/",
                "http://www.contrariwise.org",
                "http://www.therinsecup.com/",
                "http://jtgtattoo.com/home.html",
                "http://inknerd.com/",
        };
        for (String url: urls) {
            UrlInfo urlInfo = new UrlInfo(url);
            downloadAndParse(urlInfo, true);
        }
    }
    public void testCelebrityGossip() {
        System.out.println("************Celebrity Gossip*************");
        String urls[] = {
                "http://perezhilton.com/",
                "http://buckhollywood.com/",
                "http://phillyd.tv/",
                "http://www.hollywoodlife.com/",
                "http://girlstalkinsmack.com/",
                "http://haveuheard.net/",
                "http://gossip-juice.com",
                "http://celebglitz.com/",
                "http://www.gossipandsoaps.com/",
                "http://earsucker.com/",
                "http://blindgossip.com/",
                "http://justjared.buzznet.com/",
        };
        for (String url: urls) {
            UrlInfo urlInfo = new UrlInfo(url);
            downloadAndParse(urlInfo, true);
        }
    }
}
