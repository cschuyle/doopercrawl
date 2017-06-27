package dragnon.doopercrawl;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

class SiteMap {

    private final Set<String> fromUrls = new HashSet<>();
    private final Set<Link> links = new HashSet<>();

    public boolean containsFromLink(String fromUrl) {
        return fromUrls.contains(fromUrl);
    }

    public void addIfAbsent(Link link) {
        if (links.contains(link)) {
            return;
        }
        markFollowed(link.from());
        links.add(link);
    }

    public void markFollowed(String fromUrl) {
        fromUrls.add(fromUrl);
    }

    public Set<Link> getLinks() {
        return links;
    }
}
