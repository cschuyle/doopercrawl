package dragnon.doopercrawl;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

class PageProcessor implements IPageProcessor, Closeable {

    private CloseableHttpClient client = null;
    private ILinkExtractor linkExtractor;

    PageProcessor(ILinkExtractor linkExtractor) {
        this.linkExtractor = linkExtractor;
        initHttpClient();
    }

    @Override
    public Stream<String> apply(String referringUrl, String url) {
        System.err.print(".");
        if (shouldExtractLinks(url)) {
            return linkExtractor.apply(url, getContent(referringUrl, url).orElse(""));
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
            setUserAgent(request);
            response = client.execute(request);

            Header[] allHeaders = response.getHeaders("Content-Type");
            if (allHeaders == null || allHeaders.length == 0) {
                return true;
            }
            return Arrays.stream(allHeaders).anyMatch(h -> h.getValue().toLowerCase().contains("html"));
        } catch (Exception e) {
            //  Be resilient ... Anything can happen
            Logger.error(e);
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
                // GULP
            }
        }

    }

    // https://www.mkyong.com/java/apache-httpclient-examples/
    private Optional<String> getContent(String referringUrl, String url) {
        CloseableHttpResponse response = null;
        HttpGet request = null;
        HttpEntity entity = null;
        try {
            request = new HttpGet(url);

            // add request header
            setUserAgent(request);
            response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                Logger.warn("STATUS " + statusCode + ", URL " + url + ", REFERRING URL: " + referringUrl);
                return Optional.empty();
            }

            entity = response.getEntity();
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(entity.getContent()));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return Optional.of(result.toString());
        } catch (Exception e) {
            //  Be resilient ... Anything can happen
            Logger.error("URL: " + url + ", REFERRING URL: " + referringUrl , e);
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
                // GULP
            }
        }
    }

    private void setUserAgent(AbstractHttpMessage request) {
        request.addHeader("User-Agent", "Mozilla/5.0 (compatible; https://github.com/cschuyle/doopercrawl)");
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}
