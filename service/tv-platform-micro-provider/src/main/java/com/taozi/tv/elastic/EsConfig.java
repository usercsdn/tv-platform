package com.taozi.tv.elastic;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

/**
 * @author xulijie
 * @date 2021/06/25
 */
@Configuration
public class EsConfig {
	@Value("${spring.elasticsearch.rest.uris}")
	String url;
	@Value("${spring.elasticsearch.rest.username}")
	String username;
	@Value("${spring.elasticsearch.rest.password}")
	String password;

	// 将RestHighLevelClient 修改成这种方式
	@Bean(destroyMethod = "close")
	public RestHighLevelClient restClient() {

		ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo(url)
				.withBasicAuth(username, password).withConnectTimeout(60 * 1000).withSocketTimeout(60 * 1000).build();

		RestHighLevelClient client = RestClients.create(clientConfiguration).rest();
		return client;
	}
}
