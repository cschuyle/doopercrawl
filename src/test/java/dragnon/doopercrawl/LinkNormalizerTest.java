package dragnon.doopercrawl;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LinkNormalizerTest {

    private static final String fromPage = "http://domain.com";
    private LinkNormalizer normalizer = new LinkNormalizer();

    @Test
    public void stripsSpaces() {
        assertThat(normalized("http://domain.com"), is(fromPage));
        assertThat(normalized("  http://domain.com  "), is(fromPage));
        assertThat(normalized("http://domain.com/  "), is(fromPage));
    }

    @Test
    public void stripsTrailingSlash() {
        assertThat(normalized("http://wot.fr"), is("http://wot.fr"));
        assertThat(normalized("http://wot.fr/"), is("http://wot.fr"));
        assertThat(normalized("http://wot.fr/a/b/"), is("http://wot.fr/a/b"));
    }

    @Test
    public void synonymsForRootDirectory() {
        assertThat(normalized("."), is(fromPage));
        assertThat(normalized("./"), is(fromPage));
        assertThat(normalized("/"), is(fromPage));
    }

    @Test
    public void relativePath() {
        assertThat(normalized(fromPage + "/a/page.html", ".."), is(fromPage));
        assertThat(normalized(fromPage + "/a/b/page.html", "../.."), is(fromPage));
    }

    @Test public void relativePathFromDotSlash() {
        assertThat(normalized("http://google.com/about", "./principles.html"), is("http://google.com/about/principles.html"));
    }
    @Test
    public void relativePathWhenSourceIsDirectoryThatLacksTrailingSlash() {
        assertThat(normalized("http://google.com/permissions/blah", "../../permissions/using-the-logo.html"), is("http://google.com/permissions/using-the-logo.html"));
        assertThat(normalized("http://google.com/permissions", "../permissions/using-the-logo.html"), is("http://google.com/permissions/using-the-logo.html"));
        assertThat(normalized("http://google.com/intl/en/safetycenter/resources", "../../safetycenter/files/goodtoknow-booklet.pdf"),
                is("http://google.com/intl/en/safetycenter/files/goodtoknow-booklet.pdf"));
    }

    @Test
    public void normalizesRootSlash() {
        assertThat(normalized("/"), is(fromPage));
    }

    @Test
    public void stripsTags() {
        assertThat(normalized("page.html#tag"), is(fromPage + "/page.html"));
    }

    @Test
    public void prefixesWithRootPage() {
        assertThat(normalized("a/page"), is(fromPage + "/a/page"));
        assertThat(normalized(""), is(fromPage));
    }

    @Test
    public void hostWithoutProtocol() {
        assertThat(normalized("https://somewhere.com", "//www.google.com/intl/en/policies/privacy/"), is("https://www.google.com/intl/en/policies/privacy"));
        assertThat(normalized("https://somewhere.com", "//www.google.com?a=42"), is("https://www.google.com?a=42"));
        assertThat(normalized("https://somewhere.com", "//www.google.com"), is("https://www.google.com"));
    }

    private String normalized(String t) {
        return normalizer.apply(fromPage).apply(t);
    }

    private String normalized(String fromUrl, String t) {
        return normalizer.apply(fromUrl).apply(t);
    }
}