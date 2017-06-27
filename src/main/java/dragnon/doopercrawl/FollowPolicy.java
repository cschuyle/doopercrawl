package dragnon.doopercrawl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Predicate;

class FollowPolicy implements Predicate<String> {

    private String host;

    public FollowPolicy(String homePage) {
        try {
            host = new URI(homePage).getHost().toLowerCase();
        } catch (URISyntaxException e) {
            host = null;
        }
    }

    @Override
    public boolean test(String s) {
        try {
            return new URI(s).getHost().toLowerCase().equals(host);
        } catch(URISyntaxException e) {
            return false;
        }
    }
}
