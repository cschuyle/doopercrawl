package dragnon.doopercrawl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static dragnon.doopercrawl.Link.link;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CrawlerTest {

    private static final String homePage = "http://homePage";

    private static final String link1 = "http://one";
    private static final String link2 = "http://two";
    private static final String alsoInHomeDomain = "http://homePage/hello.html";

    private static final Predicate<String> alwaysFollow = always -> true;
    private static final Predicate<String> onlyHomeDomain = url -> url.startsWith(homePage);

    @Test
    public void pageWithNoLink() {

        Crawler crawler = new Crawler(url -> Stream.of(), alwaysFollow);

        Set<Link> links = crawler.crawl(homePage).getLinks();

        assertThat(links, is(Collections.emptySet()));
    }

    @Test
    public void pageWithLink() {

        Crawler crawler = new Crawler(url -> fakeLinkExtractor(url, ImmutableMap.of(homePage, Stream.of(link1))), alwaysFollow);

        Set<Link> links = crawler.crawl(homePage).getLinks();

        assertThat(links, is(ImmutableSet.of(link(homePage, link1))));
    }

    @Test
    public void pageWith2Links() {

        Crawler crawler = new Crawler(url -> fakeLinkExtractor(url, ImmutableMap.of(homePage, Stream.of(link1, link2))), alwaysFollow);

        Set<Link> links = crawler.crawl(homePage).getLinks();

        assertThat(links, is(ImmutableSet.of(
                link(homePage, link1),
                link(homePage, link2))));
    }

    @Test
    public void linksAreNotDuplicatedInOutput() {

        Crawler crawler = new Crawler(url -> fakeLinkExtractor(url, ImmutableMap.of(homePage, Stream.of(link1, link1))), alwaysFollow);

        Set<Link> links = crawler.crawl(homePage).getLinks();

        assertThat(links, is(ImmutableSet.of(
                link(homePage, link1))));
    }

    @Test
    public void linkTraversalIsRecursive() {
        Crawler crawler = new Crawler(url -> fakeLinkExtractor(url, ImmutableMap.of(homePage, Stream.of(link1), link1, Stream.of(link2))), alwaysFollow);
        Set<Link> links = crawler.crawl(homePage).getLinks();

        assertThat(links, is(ImmutableSet.of(
                link(homePage, link1),
                link(link1, link2))));

    }

    @Test
    public void tolerateCircularDependencies() {
        Crawler crawler = new Crawler(url -> fakeLinkExtractor(url, ImmutableMap.of(homePage, Stream.of(link1), link1, Stream.of(homePage))), alwaysFollow);
        Set<Link> links = crawler.crawl(homePage).getLinks();

        assertThat(links, is(ImmutableSet.of(link(homePage, link1), link(link1, homePage))));
    }

    @Test
    public void doNotFollowLinksOutsideHomePageDomain() {
        Crawler crawler = new Crawler(url -> fakeLinkExtractor(url, ImmutableMap.of(
                homePage, Stream.of(link1, alsoInHomeDomain),
                link1, Stream.of(link2))), onlyHomeDomain);
        Set<Link> links = crawler.crawl(homePage).getLinks();
        assertThat(links, is(ImmutableSet.of(
                link(homePage, alsoInHomeDomain),
                link(homePage, link1))));
    }

    private Stream<String> fakeLinkExtractor(String url, Map<String, Stream<String>> responses) {
        if (responses.containsKey(url)) {
            return responses.get(url);
        }
        return Stream.empty();
    }
}