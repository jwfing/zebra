package org.zebra.common.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UrlUtil {
    private static final String kindvprov = "aero|asia|biz|cat|com|co|coop|edu|gov|info|int|jobs|mil|mobi" +
            "|museum|name|net|org|pro|tel|travel";
    private static final String nations = "ac|ad|ae|af|ag|ai|al|am|ao|aq|ar|as|at|au|aw|ax|az|an" +
            "|ba|bb|bd|be|bf|bg|bh|bi|bj|bl|bq|bm|bn|bo|br|bs|bt|bw|bv|bu|by|bz" +
            "|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|cr|cv|cs|cu|cw|cx|cy|cz" +
            "|de|dj|dk|dm|do|dz|dd" +
            "|ec|ee|eg|er|es|et|eu|eh" +
            "|fi|fj|fk|fo|fr|fm" +
            "|ga|gb|gd|ge|gf|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy" +
            "|hk|hm|hn|hr|ht|hu" +
            "|id|ie|il|im|in|io|iq|ir|is|it" +
            "|je|jm|jo|jp" +
            "|ke|kg|kh|ki|km|kn|kp|kr|ky|kw|kz" +
            "|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly" +
            "|ma|mc|md|me|mf|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|my|mz" +
            "|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz" +
            "|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py" +
            "|qa|re|ro|rs|ru|rw" +
            "|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|ss|st|sv|sx|sy|sz|su" +
            "|tc|td|tf|tg|th|tj|tk|tl|tm|tn|to|tr|tt|tw|tz|tp" +
            "|ua|ug|uk|us|uy|uz|um|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw|zr";
    private static final String shortPatternStr = "^(\\w+\\.)*(\\w+\\.(" + kindvprov + "|" + nations + "))$";
    private static final String longPatternStr = "^(\\w+\\.)*(\\w+\\.(" + kindvprov + ")(\\.(" + nations
            + ")))$";
    private static final Pattern shortPattern = Pattern.compile(shortPatternStr, Pattern.CASE_INSENSITIVE);
    private static final Pattern longPattern = Pattern.compile(longPatternStr, Pattern.CASE_INSENSITIVE);

    public static String getDomainFromHost(String host) {
        Matcher matcher = longPattern.matcher(host);
        if (matcher.matches()) {
            return matcher.group(2);
        } else {
            matcher = shortPattern.matcher(host);
            if (matcher.matches()) {
                return matcher.group(2);
            }
        }
        return host;
    }

    public static String getAbsoluteUrl(String parentUrl, String relativeUrl) {
        try {
            return getAbsoluteUrl(new URL(parentUrl), relativeUrl);
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getHostFromUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return url.getHost();
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getAbsoluteUrl(URL parentUrl, String relativeUrl) {
        if (null == relativeUrl || relativeUrl.isEmpty()) {
            return "";
        }
        if (relativeUrl.startsWith("//")) {
            return "http:" + relativeUrl;
        } else if (relativeUrl.startsWith("/")) {
            return parentUrl.getProtocol() + "://" + parentUrl.getHost() + relativeUrl;
        } else if (relativeUrl.startsWith(".")) {
            return parentUrl.getProtocol() + "://" + parentUrl.getHost() + parentUrl.getPath() + "/" + relativeUrl;
        } else {
            return relativeUrl;
        }
    }

    public static String getCanonicalURL(String url) {
        URL canonicalURL = getCanonicalURL(url, null);
        if (canonicalURL != null) {
            return canonicalURL.toExternalForm();
        }
        return null;
    }

    public static URL genURL(String url) {
        URL parentUrl = null;
        try {
            parentUrl = new URL(url);
        } catch (MalformedURLException e1) {
            return null;
        }
        return parentUrl;
    }

    public static URL getCanonicalURL(String href, String context) {
        if (href.contains("#")) {
            href = href.substring(0, href.indexOf("#"));
        }
        href = href.replace(" ", "%20");
        try {
            URL canonicalURL;
            if (context == null) {
                canonicalURL = new URL(href);
            } else {
                canonicalURL = new URL(new URL(context), href);
            }
            String path = canonicalURL.getPath();
            if (path.startsWith("/../")) {
                path = path.substring(3);
                canonicalURL = new URL(canonicalURL.getProtocol(), canonicalURL.getHost(),
                        canonicalURL.getPort(), path);
            } else if (path.contains("..")) {
                System.out.println(path);
            }
            return canonicalURL;
        } catch (MalformedURLException ex) {
            return null;
        }
    }
}
