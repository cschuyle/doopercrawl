package dragnon.doopercrawl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LinkExtractor implements Function<String, Stream<String>> {

    private final String rootPage;
    private final LinkNormalizer linkNormalizer;

    public LinkExtractor(String rootUrl, LinkNormalizer linkNormalizer) {
        this.linkNormalizer = linkNormalizer;

        if (rootUrl == null) {
            rootPage = null;
            return;
        }
        try {
            URL url = new URL(rootUrl);

            rootPage = url.getProtocol() + "://" + url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<String> apply(String fromUrl) {
        Pattern pattern = Pattern.compile("<a[^>]*?\\s+href\\s*=\\s*\"([^\"]*?)\"\\s*>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNIX_LINES);
        Matcher matcher = pattern.matcher(fromUrl);
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches.stream()
                .flatMap(linkNormalizer.apply(fromUrl, rootPage))
                .distinct();
    }

}
