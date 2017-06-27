package dragnon.doopercrawl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class LinkNormalizer implements BiFunction<String, String, Function<String, Stream<String>>> {

    /**
     * Given two strings - fromUrl and rootPage, returns a function than can be passed to Stream::flatMap which will
     * transform a stream of URLs into a "normalized" (or, "canonical") form.
     * <p>
     * The method behind my madness of implementing this as a function from toUrl to a stream, thereby forcing the
     * caller to use flatMap is: I can write it here using chained .map()s, which I think makes life way better.  For me.
     */
    @Override
    public Function<String, Stream<String>> apply(String fromUrl, String rootPage) {
        return toUrl -> Stream.of(toUrl)
                .map(String::trim)
                .map(LinkNormalizer::dotsAndRootSlashAreAllTheSame)
                .map(toUrlParam -> resolveRelativePath(fromUrl, toUrlParam))
                .map(LinkNormalizer::stripRootSlash)
                .map(LinkNormalizer::stripTag)
                .map(toUrlParam -> prefixWithRootPage(rootPage, toUrlParam))
                .map(LinkNormalizer::stripTrailingSlash)
                ;
    }

    private static String resolveRelativePath(String fromUrl, String toUrl) {
        if (!toUrl.startsWith("..")) {
            return toUrl;
        }
        while (toUrl.startsWith("..")) {
            toUrl = toUrl.substring(2);
            if (toUrl.startsWith("/")) {
                toUrl = toUrl.substring(1);
            }
            fromUrl = upOneDirectory(fromUrl);
        }
        return fromUrl + "/" + toUrl;
    }

    private static String upOneDirectory(String fromUrl) {
        if (!fromUrl.endsWith("/")) {
            fromUrl = fromUrl + "/";
        }
        // Sorry. There are tests...
        int lastSlash = fromUrl.lastIndexOf('/');
        if (lastSlash >= 0) {
            fromUrl = fromUrl.substring(lastSlash);
            lastSlash = fromUrl.lastIndexOf('/');
            if (lastSlash >= 0) {
                return fromUrl.substring(0, lastSlash);
            }
        }
        return fromUrl;
    }

    private static String dotsAndRootSlashAreAllTheSame(String urlString) {
        switch (urlString) {
            case "./":
            case ".":
            case "/":
                return "";
            default:
                return urlString;
        }
    }

    private static String stripTrailingSlash(String urlString) {
        while (urlString.endsWith("/")) {
            urlString = urlString.substring(0, urlString.length() - 1);
        }
        return urlString;
    }


    private static String stripRootSlash(String urlString) {
        while (urlString.startsWith("/")) {
            urlString = urlString.substring(1);
        }
        return urlString;
    }

    private static String prefixWithRootPage(String rootPage, String urlString) {
        if (rootPage == null) {
            return urlString;
        }
        try {
            URI url = new URI(urlString);
            if (isEmpty(url.getHost())) {
                return rootPage + "/" + stripCurrentDirPrefix(urlString);
            }
            return urlString;
        } catch (URISyntaxException e) {
            return urlString;
        }
    }

    private static String stripCurrentDirPrefix(String urlString) {
        if (urlString.startsWith("./")) {
            return urlString.substring(2);
        }
        return urlString;
    }

    private static String stripTag(String s) {
        int tagIndex = s.indexOf('#');
        if (tagIndex >= 0) {
            return s.substring(0, tagIndex);
        }
        return s;
    }

}
