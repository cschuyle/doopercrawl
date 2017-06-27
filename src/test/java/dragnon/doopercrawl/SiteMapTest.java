package dragnon.doopercrawl;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SiteMapTest {

    @Test
    public void onlyAddIfAbsent() {
        SiteMap siteMap = new SiteMap();
        Pair<String, String> link = Pair.of("a", "b");

        siteMap.addIfAbsent(link);
        assertThat(siteMap.getLinks(), is(ImmutableSet.of(link)));

        siteMap.addIfAbsent(link);
        assertThat(siteMap.getLinks(), is(ImmutableSet.of(link)));
    }

    @Test
    public void remembersFromLinks() {
        SiteMap siteMap = new SiteMap();
        assertThat(siteMap.containsFromLink("a"), is(false));
        siteMap.addIfAbsent(Pair.of("a", "b"));
        assertThat(siteMap.containsFromLink("a"), is(true));
    }
}