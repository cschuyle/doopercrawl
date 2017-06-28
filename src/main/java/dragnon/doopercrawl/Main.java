package dragnon.doopercrawl;

import static java.util.Comparator.comparing;


public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException("");
        }
        try (PageProcessor pageProcessor = new PageProcessor(new LinkExtractor(args[0], new LinkNormalizer()))) {
            new Browser(pageProcessor, new FollowPolicy(args[0]))
                    .crawl(args[0])
                    .getLinks().stream()
                    .sorted(comparing(Link::from)
                            .thenComparing(Link::to))
                    .forEach(link -> System.out.println("LINK: " + link));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("USAGE: doopercrawl <URL to crawl>");
            System.exit(-1);
        }
    }
}
