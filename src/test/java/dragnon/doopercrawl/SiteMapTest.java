package dragnon.doopercrawl;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import static dragnon.doopercrawl.Link.link;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SiteMapTest {

    @Test
    public void onlyAddIfAbsent() {
        SiteMap siteMap = new SiteMap();
        Link link = link("a", "b");

        siteMap.addIfAbsent(link);
        assertThat(siteMap.getLinks(), is(ImmutableSet.of(link)));

        siteMap.addIfAbsent(link);
        assertThat(siteMap.getLinks(), is(ImmutableSet.of(link)));
    }

    @Test
    public void remembersFromLinks() {
        SiteMap siteMap = new SiteMap();
        assertThat(siteMap.containsFromLink("a"), is(false));
        siteMap.addIfAbsent(link("a", "b"));
        assertThat(siteMap.containsFromLink("a"), is(true));
    }
}