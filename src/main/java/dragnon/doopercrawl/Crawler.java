package dragnon.doopercrawl;

import java.util.Set;

import static dragnon.doopercrawl.Link.link;

class Crawler {

    private SiteMap siteMap = new SiteMap();
    private IPageProcessor pageProcessor;
    private IFollowPolicy followPolicy;

    Crawler(IPageProcessor pageProcessor, IFollowPolicy followPolicy) {
        this.pageProcessor = pageProcessor;
        this.followPolicy = followPolicy;
    }

    Crawler crawl(String url) {
        processPage("INITIAL PAGE", url, 0);
        return this;
    }

    private void processPage(String referringPage, String url, int depth) {
        // This is not optimal.  But to not stack overflow on crawls where loops are not detected, we have this here for now.
        if(depth > 100) {
            Logger.warn("Returning without continuing for too-deep recursion");
            return;
        }
        try {
            if (siteMap.containsFromLink(url)) {
                return;
            }
            if (followPolicy.test(url)) {
                pageProcessor.apply(referringPage, url)
//                        .parallel() // This is too aggressive for many sites.  Instead, do some backoff/nice-ness to not get status 429, etc.
                        .forEach(toUrl -> {
                            siteMap.addIfAbsent(link(url, toUrl));
                            if (!siteMap.containsToLink(toUrl)) {
                                processPage(url, toUrl, depth + 1);
                                siteMap.markFollowedTo(toUrl);
                            }
                        });
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    Set<Link> getLinks() {
        return siteMap.getLinks();
    }
}
