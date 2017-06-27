package dragnon.doopercrawl;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LinkNormalizerTest {

    public static final String rootUrl = "http://domain.com";
    LinkNormalizer normalizer = new LinkNormalizer();

    @Test
    public void stripsSpaces() {
        assertThat(normalized("http://domain.com  "), is(rootUrl));
        assertThat(normalized("  http://domain.com  "), is(rootUrl));
    }

    @Test
    public void stripsTrailingSlash() {
        assertThat(normalized("http://wot.fr/"), is("http://wot.fr"));
        assertThat(normalized("http://wot.fr/a/b/"), is("http://wot.fr/a/b"));
        assertThat(normalized("http://wot.fr/a////"), is("http://wot.fr/a"));
    }

    @Test
    public void normalizedSynonymsForRootDirectory() {
        assertThat(normalized("."), is(rootUrl));
        assertThat(normalized("./"), is(rootUrl));
        assertThat(normalized("/"), is(rootUrl));
        assertThat(normalized("///"), is(rootUrl));
    }

    @Test
    public void resolvesRelativePath() {
        assertThat(normalized("a/page.html", ".."), is(rootUrl));
        assertThat(normalized("a/b/page.html", "../.."), is(rootUrl));
        assertThat(normalized("a/b/page.html", "../..//"), is(rootUrl));
    }

    @Test
    public void normalizesRootSlash() {
        assertThat(normalized("/"), is(rootUrl));
        assertThat(normalized("//"), is(rootUrl));
    }

    @Test
    public void stripsTags() {
        assertThat(normalized("page.html#tag"), is(rootUrl + "/page.html"));
    }

    @Test
    public void prefixesWithRootPage() {
        assertThat(normalized("a/page"), is(rootUrl + "/a/page"));
        assertThat(normalized("a/page//"), is(rootUrl + "/a/page"));
        assertThat(normalized(""), is(rootUrl));
    }

    private String normalized(String t) {
        return normalizer.apply("use the other form of normalized()", rootUrl)
                .apply(t)
                .findFirst().get();
    }

    private String normalized(String fromUrl, String t) {
        return normalizer.apply(fromUrl, rootUrl)
                .apply(t)
                .findFirst().get();
    }


}