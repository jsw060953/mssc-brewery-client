package guru.springframework.msscbreweryclient.web.config;

import lombok.Value;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

    /**
     * Created by jt on 2019-08-08.
     */
    @Component
    public class BlockingRestTemplateCustomizer implements RestTemplateCustomizer {
        @Value( "${http.connection.timeout}" )
        private int requestTimeout;

        @Value ("${http.connection.maxconections}")
        private int maxConnections;

        public ClientHttpRequestFactory clientHttpRequestFactory(){
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
            //connectionManager.setMaxTotal(100);
            connectionManager.setMaxTotal(maxConnections);
            connectionManager.setDefaultMaxPerRoute(20);

            // 3000
            RequestConfig requestConfig = RequestConfig
                    .custom()
                    .setConnectionRequestTimeout(requestTimeout)
                    .setSocketTimeout(requestTimeout)
                    .build();

            CloseableHttpClient httpClient = HttpClients
                    .custom()
                    .setConnectionManager(connectionManager)
                    .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                    .setDefaultRequestConfig(requestConfig)
                    .build();

            return new HttpComponentsClientHttpRequestFactory(httpClient);
        }

        @Override
        public void customize(RestTemplate restTemplate) {
            restTemplate.setRequestFactory(this.clientHttpRequestFactory());
        }
}
