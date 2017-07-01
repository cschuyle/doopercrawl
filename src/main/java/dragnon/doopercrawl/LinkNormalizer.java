package dragnon.doopercrawl;

import org.apache.commons.lang3.tuple.Pair;

import java.net.URL;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
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
                .peek(u -> {
                    if (u.contains("/../")) {
                        Logger.warn("Suspicious dots! URL: " + toUrl + ", REFERRING URL: " + fromUrl);
                    }
                })
                .map(this::stripTrailingSlash)
                .map(this::collapseRelativePaths)
                .findFirst().get();
    }

    private String collapseRelativePaths(String url) {
        Optional<Pair<String, String>> protocolAndPath = Uris.protocolAndPath(url);
        return collapseRelativePathsInPath(protocolAndPath).orElse(url);
    }

    private Optional<String> collapseRelativePathsInPath(Optional<Pair<String, String>> protocolAndPath) {
        if (!protocolAndPath.isPresent()) {
            return Optional.empty();
        }
        String path = protocolAndPath.get().getRight();
        while (true) {
            String newPath = path.replaceAll("[^/]+/\\.\\./", "/");
            if (newPath.equals(path)) {
                return Optional.of(protocolAndPath.get().getLeft() + path);
            }
            path = newPath;
        }
    }

    private Function<String, String> transformUsingFromUrl(String fromUrlParam) {
        return toUrl -> {
            try {
                String fromUrl;
                if (doesntEndWIthSlash(fromUrlParam) && couldBeADirectory(fromUrlParam) && isRelativePath(toUrl)) {
                    fromUrl = fromUrlParam + "/";
                } else {
                    fromUrl = fromUrlParam;
                }
                return new URL(new URL(fromUrl), toUrl).toString();
            } catch (Exception e) {
                Logger.error(e);
                return fromUrlParam;
            }
        };
    }

    private boolean couldBeADirectory(String url) {
        // This is just a heuristic, and admittedly not a great one.
        return !looksLikeAFileNameToMe(url);
    }

    private final static Pattern fileNamePattern = Pattern.compile(".*[a-z0-9_-]+\\.[a-z0-9_-]+$", Pattern.CASE_INSENSITIVE);

    private boolean looksLikeAFileNameToMe(String url) {
        return fileNamePattern.matcher(url).matches();
    }

    private boolean doesntEndWIthSlash(String url) {
        return !url.endsWith("/");
    }

    private boolean isRelativePath(String toUrl) {
        return toUrl.startsWith("../") || toUrl.startsWith("./");
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
