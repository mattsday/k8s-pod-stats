package io.pivotal.mday.k8s.podstats.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@RedisHash("DateRecord")
@Data
public class DateRecord {
	@Id
	String id;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ", locale = "en_GB")
	private Date updated;

	private Map<String, List<Pod>> pods;
}
