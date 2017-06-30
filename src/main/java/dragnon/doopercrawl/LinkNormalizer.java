package dragnon.doopercrawl;

import java.net.URL;
import java.util.function.Function;
import java.util.stream.Stream;

public class LinkNormalizer implements Function<String, Function<String, String>> {

    /**
     * Given - fromUr, returns a function than can be passed to Stream::map which will
     * transform a toUrl into a "normalized" (or, "canonical") form.
     * The toUrl is the URL to be transformed.
     * The fromUrl is the page which contains the link to toUrl.
     * <p>
     * This is essentially a wrapper for the java.netURL constructor, made easily usable in Stream.map; with a
     * couple minorly different behaviors.
     */
    @Override
    public Function<String, String> apply(String fromUrlParam) {
        String fromUrl = fromUrlParam.trim();
        return toUrl -> Stream.of(toUrl)
                .map(this::stripTag)
                .map(String::trim)
                .map(transformUsingFromUrl(fromUrl))
                .map(this::stripTrailingSlash)
                .findFirst().get();
    }

    private Function<String, String> transformUsingFromUrl(String fromUrl) {
        return toUrl -> {
            try {
                return new URL(new URL(fromUrl), toUrl).toString();
            } catch (Exception e) {
                Logger.error(e);
                return fromUrl;
            }
        };
    }

    private String stripTrailingSlash(String urlString) {
        boolean done;
        do {
            done = true;
            if (urlString.endsWith("/")) {
                urlString = urlString.substring(0, urlString.length() - 1);
                done = false;
            }
            if (urlString.endsWith("/.")) {
                urlString = urlString.substring(0, urlString.length() - 2);
                done = false;
            }
        } while (!done);
        return urlString;
    }

    private String stripTag(String s) {
        int tagIndex = s.indexOf('#');
        if (tagIndex >= 0) {
            return s.substring(0, tagIndex);
        }
        return s;
    }

}
