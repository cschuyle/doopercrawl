package dragnon.doopercrawl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class LinkExtractor implements Function<String, Stream<String>> {

    private final String rootPage;

    public LinkExtractor(String urlString) {
        if(urlString == null) {
            rootPage = null;
            return;
        }
        try {
            URL url = new URL(urlString);

            rootPage = url.getProtocol() + "://" + url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<String> apply(String s) {
        Pattern pattern = Pattern.compile("<a[^>]*?\\s+href\\s*=\\s*\"([^\"]*?)\"\\s*>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNIX_LINES);
        Matcher matcher = pattern.matcher(s);
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches.stream().map(String::trim)
                .map(this::stripTag)
                .map(this::prefixWithRootPage);
    }

    private String prefixWithRootPage(String urlString) {
        if(rootPage == null) {
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

    private String stripCurrentDirPrefix(String urlString) {
        if (urlString.startsWith("./")) {
            return urlString.substring(2);
        }
        return urlString;
    }

    private String stripTag(String s) {
        int tagIndex = s.indexOf('#');
        if (tagIndex >= 0) {
            return s.substring(tagIndex + 1);
        }
        return s;
    }
}
