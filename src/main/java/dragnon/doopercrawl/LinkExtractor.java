package dragnon.doopercrawl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LinkExtractor implements ILinkExtractor {

    private final LinkNormalizer linkNormalizer;

    LinkExtractor(LinkNormalizer linkNormalizer) {
        this.linkNormalizer = linkNormalizer;
    }

    private static final Pattern anchorPattern = Pattern.compile("<a[^>]*?\\s+href\\s*=\\s*\"([^\"]*?)\"",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNIX_LINES);

    private static final Pattern imgPattern = Pattern.compile("<img[^>]*?\\s+src\\s*=\\s*\"([^\"]*?)\"",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNIX_LINES);

    @Override
    public Stream<String> apply(String fromUrl, String content) {
        List<String> matches = new ArrayList<>();
        addMatches(content, anchorPattern, matches);
        addMatches(content, imgPattern, matches);
        String[] unmodified = new String[]{null};
        return matches.stream()
                .peek(unmodifiedUrl -> unmodified[0] = unmodifiedUrl)
                .map(toUrl -> linkNormalizer.apply(fromUrl).apply(toUrl))
                .distinct();
    }

    private void addMatches(String fromUrl, Pattern pattern, List<String> matchesAccumulator) {
        Matcher matcher = pattern.matcher(fromUrl);
        while (matcher.find()) {
            matchesAccumulator.add(matcher.group(1));
        }
    }

}
