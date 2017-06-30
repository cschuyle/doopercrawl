package dragnon.doopercrawl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

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

}
