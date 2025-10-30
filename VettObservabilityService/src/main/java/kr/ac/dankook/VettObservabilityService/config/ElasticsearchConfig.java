package kr.ac.dankook.VettObservabilityService.config;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.NonNull;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.URI;


@Configuration
@EnableElasticsearchRepositories
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String host;

    @Override
    @NonNull
    public ClientConfiguration clientConfiguration(){
        return ClientConfiguration.builder()
                .connectedTo(host).build();
    }

    @Bean
    public ElasticsearchClient elasticSearchClient(){
        URI u = URI.create(host.contains("://") ? host : "http://" + host);
        HttpHost httpHost = new HttpHost(
                u.getHost(),
                (u.getPort() == -1 ? 9200 : u.getPort()),
                (u.getScheme() == null ? "http" : u.getScheme())
        );

        RestClient low = RestClient.builder(httpHost).build();
        return new ElasticsearchClient(new RestClientTransport(low,new JacksonJsonpMapper()));
    }
}
