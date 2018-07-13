package io.pivotal.mday.k8s.podstats.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@RedisHash("PodStats")
public class PodStats {
	@Id
	String id;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ", locale = "en_GB")
	private Date updated;

	private int maxPods;
	private int currentPods;

}
