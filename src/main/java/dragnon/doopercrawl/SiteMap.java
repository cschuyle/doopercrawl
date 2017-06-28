package dragnon.doopercrawl;

import java.util.HashSet;
import java.util.Set;

class SiteMap {

    private final Set<String> fromUrls = new HashSet<>();
    private final Set<String> toUrls = new HashSet<>();
    private final Set<Link> links = new HashSet<>();

    public boolean containsFromLink(String fromUrl) {
        synchronized(fromUrls) {
            return fromUrls.contains(fromUrl);
        }
    }

    public boolean containsToLink(String fromUrl) {
        synchronized(toUrls) {
            return toUrls.contains(fromUrl);
        }
    }

    public boolean addIfAbsent(Link link) {
        synchronized(links) {
            if (links.contains(link)) {
                return false;
            }
            links.add(link);
        }
        markFollowedFrom(link.from());
        return true;
    }

    public void markFollowedFrom(String fromUrl) {
        synchronized(fromUrls) {
            fromUrls.add(fromUrl);
        }
    }

    public void markFollowedTo(String toUrl) {
        synchronized(toUrls) {
            toUrls.add(toUrl);
        }
    }

    public Set<Link> getLinks() {
        return links;
    }
}
