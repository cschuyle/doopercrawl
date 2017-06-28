package dragnon.doopercrawl;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

class PageProcessor implements Function<String, Stream<String>>, Closeable {

    private CloseableHttpClient client = null;
    private LinkExtractor linkExtractor;

    public PageProcessor(LinkExtractor linkExtractor) {
        this.linkExtractor = linkExtractor;
        initHttpClient();
    }

    @Override
    public Stream<String> apply(String url) {
        System.err.print(".");
        if (shouldExtractLinks(url)) {
            return linkExtractor.apply(getContent(url).orElse(""));
        }
        return Stream.empty();
    }

    // https://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html
    private void initHttpClient() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);

        client = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
    }

    private boolean shouldExtractLinks(String url) {
        CloseableHttpResponse response = null;
        HttpHead request = null;
        try {
            request = new HttpHead(url);

            // add request header
            request.addHeader("User-Agent", "Mozilla 5.0");
            response = client.execute(request);

            Header[] allHeaders = response.getHeaders("Content-Type");
            if (allHeaders == null || allHeaders.length == 0) {
                return true;
            }
            return Arrays.stream(allHeaders).anyMatch(h -> h.getValue().toLowerCase().contains("html"));
        } catch (Exception e) {
            //  Be resilient ... Anything can happen
            e.printStackTrace();
            return true;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (request != null) {
                    request.releaseConnection();
                }
            } catch (IOException e) {
            }
        }

    }

    // https://www.mkyong.com/java/apache-httpclient-examples/
    private Optional<String> getContent(String url) {
        CloseableHttpResponse response = null;
        HttpGet request = null;
        HttpEntity entity = null;
        try {
            request = new HttpGet(url);

            // add request header
            request.addHeader("User-Agent", "Mozilla 5.0");
            response = client.execute(request);

            entity = response.getEntity();
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(entity.getContent()));

            StringBuffer result = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return Optional.of(result.toString());
        } catch (IOException e) {
            //  Be resilient ... Anything can happen
            e.printStackTrace();
            return Optional.empty();
        } finally {
            try {
                if (entity != null) {
                    EntityUtils.consume(entity);
                }
                if (response != null) {
                    response.close();
                }
                if (request != null) {
                    request.releaseConnection();
                }
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}
