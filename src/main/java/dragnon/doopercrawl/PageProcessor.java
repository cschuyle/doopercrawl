package dragnon.doopercrawl;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.base.Throwables.propagate;

class PageProcessor implements Function<String, Stream<String>>, Closeable {

    private CloseableHttpClient client = null;
    private Function<String, Stream<String>> linkExtractor;

    public PageProcessor(Function<String, Stream<String>> linkExtractor) {
        this.linkExtractor = linkExtractor;
        initHttpClient();
    }

    @Override
    public Stream<String> apply(String url) {
        System.out.print(".");
        StringBuffer result;
        result = getContent(url);
        return linkExtractor.apply(result.toString());
    }

    // https://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html
    private void initHttpClient() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(10);
        cm.setDefaultMaxPerRoute(2);

        client = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
    }

    // https://www.mkyong.com/java/apache-httpclient-examples/
    private StringBuffer getContent(String url) {
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
            return result;
        } catch (IOException e) {
            throw propagate(e);
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
                throw propagate(e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}
