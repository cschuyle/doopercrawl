package dragnon.doopercrawl;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static dragnon.doopercrawl.Link.link;

class Browser {

    private Function<String, Stream<String>> linkExtractor;
    private Predicate<String> followPolicy;

    private Queue<String> pendingQueue = new LinkedBlockingDeque<>(1000);

    public Browser(Function<String, Stream<String>> linkExtractor, Predicate<String> followPolicy) {
        this.linkExtractor = linkExtractor;
        this.followPolicy = followPolicy;
    }

    public Browser crawl(String url) {
        pendingQueue.add(url);
        while (!pendingQueue.isEmpty()) {
            processPage(pendingQueue.remove());
        }
        return this;
    }

    private SiteMap siteMap = new SiteMap();

    private void processPage(String fromUrl) {
        if (siteMap.containsFromLink(fromUrl)) {
            return;
        }
        if(followPolicy.test(fromUrl)) {
            linkExtractor.apply(fromUrl)
                    .forEach(toUrl -> {
                        siteMap.addIfAbsent(link(fromUrl, toUrl));
                        processPage(toUrl);
                    });
        }
    }

    public Set<Link> getLinks() {
        return siteMap.getLinks();
    }
}
