package dragnon.doopercrawl;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

class SiteMap {

    private final Set<String> fromUrls = new HashSet<>();
    private final Set<Pair<String, String>> links = new HashSet<>();

    public boolean containsFromLink(String fromUrl) {
        return fromUrls.contains(fromUrl);
    }

    public void addIfAbsent(Pair<String, String> link) {
        if (links.contains(link)) {
            return;
        }
        markFollowed(link.getLeft());
        links.add(link);
    }

    public void markFollowed(String fromUrl) {
        fromUrls.add(fromUrl);
    }

    public Set<Pair<String, String>> getLinks() {
        return links;
    }
}
