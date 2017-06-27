package dragnon.doopercrawl;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.net.HttpHeaders.USER_AGENT;

class PageProcessor implements Function<String, Stream<String>> {
    //            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    //            int cores = Runtime.getRuntime().availableProcessors();
    //            ThreadPoolExecutor executor = new ThreadPoolExecutor(cores, 2 * cores, 1, TimeUnit.MINUTES, queue);

    private Function<String, Stream<String>> linkExtractor;

    public PageProcessor(Function<String, Stream<String>> linkExtractor) {
        this.linkExtractor = linkExtractor;
    }

    @Override
    public Stream<String> apply(String url) {
        System.out.print(".");
        StringBuffer result = null;
        try {
            result = getContent(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return linkExtractor.apply(result.toString());
    }

    private StringBuffer getContent(String url) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = client.execute(request);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result;
    }
}
