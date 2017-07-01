package dragnon.doopercrawl;

import org.apache.commons.lang3.tuple.Pair;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class Uris {

    public static Optional<URL> parseUrl(String context, String url) {
        try {
            return Optional.of(new URL(parseUrl(context).orElse(null), url));
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
    }

    public static Optional<URL> parseUrl(String url) {
        try {
            return Optional.of(new URL(url));
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
    }

    public static Optional<Pair<String, String>> protocolAndPath(String url) {
        return parseUrl(url).map(parsed ->
                Pair.of(parsed.getProtocol() + "://",
                        parsed.getHost() + append("/", stripStartingSlash(parsed.getPath())) + append("?", stripStartingSlash(parsed.getQuery())))
        );
    }

    private static String append(String prefix, String value) {
        if (isEmpty(value)) {
            return "";
        }
        return prefix + value;
    }

    private static String stripStartingSlash(String s) {
        if (s == null) {
            return null;
        }
        if (s.startsWith("/")) {
            return s.substring(1);
        }
        return s;
    }
}
