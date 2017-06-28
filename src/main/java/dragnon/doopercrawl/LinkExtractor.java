package dragnon.doopercrawl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.google.common.base.Throwables.propagate;

public class LinkExtractor implements Function<String, Stream<String>> {

    private final String rootPage;
    private final LinkNormalizer linkNormalizer;

    LinkExtractor(String rootUrl, LinkNormalizer linkNormalizer) {
        this.linkNormalizer = linkNormalizer;

        if (rootUrl == null) {
            rootPage = null;
            return;
        }
        try {
            URL url = new URL(rootUrl);
            rootPage = url.getProtocol() + "://" + url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort());
        } catch (MalformedURLException e) {
            throw propagate(e);
        }
    }

    private static final Pattern anchorPattern = Pattern.compile("<a[^>]*?\\s+href\\s*=\\s*\"([^\"]*?)\"",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNIX_LINES);

    private static final Pattern imgPattern = Pattern.compile("<img[^>]*?\\s+src\\s*=\\s*\"([^\"]*?)\"",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNIX_LINES);

    @Override
    public Stream<String> apply(String content) {
        List<String> matches = new ArrayList<>();
        addMatches(content, anchorPattern, matches);
        addMatches(content, imgPattern, matches);
        return matches.stream()
                .flatMap(linkNormalizer.apply(content, rootPage))
                .distinct();
    }

    private void addMatches(String fromUrl, Pattern pattern, List<String> matchesAccumulator) {
        Matcher matcher = pattern.matcher(fromUrl);
        while (matcher.find()) {
            matchesAccumulator.add(matcher.group(1));
        }
    }

}
