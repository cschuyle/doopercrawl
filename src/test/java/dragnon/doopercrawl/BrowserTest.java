package dragnon.doopercrawl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BrowserTest {

    private static final String homePage = "http://homePage";

    private static final String link1 = "http://one";
    private static final String link2 = "http://two";
    private static final String alsoInHomeDomain = "http://homePage/hello.html";

    private static final Predicate<String> alwaysFollow = always -> true;
    private static final Predicate<String> onlyHomeDomain = url -> url.startsWith(homePage);

    @Test
    public void pageWithNoLink() {

        Browser browser = new Browser(url -> Stream.of(), alwaysFollow);

        Set<Pair<String, String>> links = browser.crawl(homePage).getLinks();

        assertThat(links, is(Collections.emptySet()));
    }

    @Test
    public void pageWithLink() {

        Browser browser = new Browser(url -> fakeLinkExtractor(url, ImmutableMap.of(homePage, Stream.of(link1))), alwaysFollow);

        Set<Pair<String, String>> links = browser.crawl(homePage).getLinks();

        assertThat(links, is(ImmutableSet.of(Pair.of(homePage, link1))));
    }

    @Test
    public void pageWith2Links() {

        Browser browser = new Browser(url -> fakeLinkExtractor(url, ImmutableMap.of(homePage, Stream.of(link1, link2))), alwaysFollow);

        Set<Pair<String, String>> links = browser.crawl(homePage).getLinks();

        assertThat(links, is(ImmutableSet.of(
                Pair.of(homePage, link1),
                Pair.of(homePage, link2))));
    }

    @Test
    public void linksAreNotDuplicatedInOutput() {

        Browser browser = new Browser(url -> fakeLinkExtractor(url, ImmutableMap.of(homePage, Stream.of(link1, link1))), alwaysFollow);

        Set<Pair<String, String>> links = browser.crawl(homePage).getLinks();

        assertThat(links, is(ImmutableSet.of(
                Pair.of(homePage, link1))));
    }

    @Test
    public void linkTraversalIsRecursive() {
        Browser browser = new Browser(url -> fakeLinkExtractor(url, ImmutableMap.of(homePage, Stream.of(link1), link1, Stream.of(link2))), alwaysFollow);
        Set<Pair<String, String>> links = browser.crawl(homePage).getLinks();

        assertThat(links, is(ImmutableSet.of(
                Pair.of(homePage, link1),
                Pair.of(link1, link2))));

    }

    @Test
    public void tolerateCircularDependencies() {
        Browser browser = new Browser(url -> fakeLinkExtractor(url, ImmutableMap.of(homePage, Stream.of(link1), link1, Stream.of(homePage))), alwaysFollow);
        Set<Pair<String, String>> links = browser.crawl(homePage).getLinks();

        assertThat(links, is(ImmutableSet.of(Pair.of(homePage, link1), Pair.of(link1, homePage))));
    }

    @Test
    public void doNotFollowLinksOutsideHomePageDomain() {
        Browser browser = new Browser(url -> fakeLinkExtractor(url, ImmutableMap.of(
                homePage, Stream.of(link1, alsoInHomeDomain),
                link1, Stream.of(link2))), onlyHomeDomain);
        Set<Pair<String, String>> links = browser.crawl(homePage).getLinks();
        assertThat(links, is(ImmutableSet.of(
                Pair.of(homePage, alsoInHomeDomain),
                Pair.of(homePage, link1))));
    }

    private Stream<String> fakeLinkExtractor(String url, Map<String, Stream<String>> responses) {
        if (responses.containsKey(url)) {
            return responses.get(url);
        }
        return Stream.empty();
    }
}