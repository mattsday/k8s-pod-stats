package io.pivotal.mday.k8s.podstats.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.util.Config;

@Configuration
public class K8sClientConfig {

	@Value("${k8s.config}")
	private String config;

	@Bean
	public CoreV1Api getClient() throws IOException {
		ApiClient client = Config.fromConfig(new ByteArrayInputStream(config.getBytes()));
		io.kubernetes.client.Configuration.setDefaultApiClient(client);
		CoreV1Api api = new CoreV1Api();
		return api;
	}

}
