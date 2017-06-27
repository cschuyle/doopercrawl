package dragnon.doopercrawl;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.google.common.net.HttpHeaders.USER_AGENT;
import static java.util.Comparator.comparing;


public class Main {

    public static void main(String[] args) {
        try {
            if(args.length != 1) {
                throw new RuntimeException("");
            }
            new Browser(new PageProcessor(new LinkExtractor(args[0])), new FollowPolicy(args[0]))
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
