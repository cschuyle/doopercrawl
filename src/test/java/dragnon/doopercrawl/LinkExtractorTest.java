package dragnon.doopercrawl;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.of;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class LinkExtractorTest {

    private static LinkExtractor linkExtractor;

    private static LinkNormalizer linkNormalizer = new LinkNormalizer() {
        @Override
        public Function<String, Stream<String>> apply(String fromUrl, String rootPage) {
            return toUrl -> Stream.of(toUrl);
        }
    };

    @BeforeClass
    public static void setUp() {
        linkExtractor = new LinkExtractor(null, linkNormalizer);
    }

    @Test
    public void noMatches() {
        String input = "";
        assertThat(extractLink(input), is(empty()));
    }

    @Test
    public void aMatch() {
        assertThat(extractLink("Hi there <a href=\"http://domain.com\">This</a> is  a link"),
                is(of("http://domain.com")));
    }

    @Test
    public void aMatchWithOtherStuffBesidesHref() {
        assertThat(extractLink("<a class=\"wd-navbar-brand navbar-brand\" href=\"http://wiprodigital.com\">"),
                is(of("http://wiprodigital.com")));

    }

    @Test
    public void severalMatches() {
        assertThat(extractLink("I do not like <A HREF=\"green eggs and ham\">Green Eggs And Ham</a>, \nI do not like them, <a href=\"Sam I am\">Sam I am</a>"),
                is(of("green eggs and ham", "Sam I am")));
    }

    private List<String> extractLink(String input) {
        return linkExtractor.apply(input).collect(toList());
    }
}