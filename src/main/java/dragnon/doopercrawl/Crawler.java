package dragnon.doopercrawl;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static dragnon.doopercrawl.Link.link;

class Crawler {

    private SiteMap siteMap = new SiteMap();
    private Function<String, Stream<String>> pageProcessor;
    private Predicate<String> followPolicy;

    public Crawler(Function<String, Stream<String>> pageProcessor, Predicate<String> followPolicy) {
        this.pageProcessor = pageProcessor;
        this.followPolicy = followPolicy;
    }

    public Crawler crawl(String url) {
        processPage(url);
        return this;
    }

    private void processPage(String fromUrl) {
        if (siteMap.containsFromLink(fromUrl)) {
            return;
        }
        if (followPolicy.test(fromUrl)) {
            pageProcessor.apply(fromUrl)
                    .forEach(toUrl -> {
                        siteMap.addIfAbsent(link(fromUrl, toUrl));
                        if (!siteMap.containsToLink(toUrl)) {
                            processPage(toUrl);
                            siteMap.markFollowedTo(toUrl);
                        }
                    });
        }
    }

    public Set<Link> getLinks() {
        return siteMap.getLinks();
    }
}
