package dragnon.doopercrawl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static dragnon.doopercrawl.Uris.parseUrl;

class FollowPolicy implements IFollowPolicy {

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
            String host = parseUrl(s).map(URL::getHost).orElse(null);
            if (host == null) {
                return false;
            }
            return host.toLowerCase().equals(this.host);
        } catch (Exception e) {
            Logger.error(e);
            return false;
        }
    }
}
