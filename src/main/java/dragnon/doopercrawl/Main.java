package dragnon.doopercrawl;

import java.util.Set;

import static java.util.Comparator.comparing;


public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException("");
        }
        try (PageProcessor pageProcessor = new PageProcessor(new LinkExtractor(args[0], new LinkNormalizer()))) {
            Set<Link> links = new Crawler(pageProcessor, new FollowPolicy(args[0]))
                    .crawl(args[0])
                    .getLinks();
            links.stream().sorted(comparing(Link::from).thenComparing(Link::to))
                    .forEach(link -> System.out.println("LINK: " + link));
            System.out.println("TOTAL: " + links.size() + " LINKS.");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("USAGE: doopercrawl <URL to crawl>");
            System.exit(-1);
        }
    }
}
