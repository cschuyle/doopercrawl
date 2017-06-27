package dragnon.doopercrawl;

import java.util.HashSet;
import java.util.Set;

class SiteMap {

    private final Set<String> fromUrls = new HashSet<>();
    private final Set<String> toUrls = new HashSet<>();
    private final Set<Link> links = new HashSet<>();

    public boolean containsFromLink(String fromUrl) {
        return fromUrls.contains(fromUrl);
    }

    public boolean containsToLink(String fromUrl) {
        return toUrls.contains(fromUrl);
    }

    public boolean addIfAbsent(Link link) {
        if (links.contains(link)) {
            return false;
        }
        markFollowedFrom(link.from());
        links.add(link);
        return true;
    }

    public void markFollowedFrom(String fromUrl) {
        fromUrls.add(fromUrl);
    }

    public void markFollowedTo(String toUrl) {
        toUrls.add(toUrl);
    }

    public Set<Link> getLinks() {
        return links;
    }
}
