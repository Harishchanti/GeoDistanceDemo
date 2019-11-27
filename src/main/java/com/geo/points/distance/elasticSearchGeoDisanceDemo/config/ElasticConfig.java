package com.geo.points.distance.elasticSearchGeoDisanceDemo.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ElasticConfig {

    @Value("${elastic.host:localhost}")
    private String host;

    @Value("${elastic.cluster.name:elasticsearch}")
    private String clusterName;


    @Bean
    public TransportClient esClient() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .put("client.transport.sniff", true)
                .build();

        return new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(host), 9300));
    }
}
