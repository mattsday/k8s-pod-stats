package io.pivotal.mday.k8s.podstats.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1ContainerStatus;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.pivotal.mday.k8s.podstats.model.Pod;
import io.pivotal.mday.k8s.podstats.model.PodStats;
import io.pivotal.mday.k8s.podstats.repo.CountRepo;
import io.pivotal.mday.k8s.podstats.repo.PodRepo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class K8sService {
	@NonNull
	private CoreV1Api api;
	@NonNull
	private PodRepo podRepo;
	@NonNull
	private CountRepo statRepo;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${redis.stats.id}")
	private String statId;
	@Value("${redis.date.id}")
	private String dateId;

	@Scheduled(fixedDelay = 60000)
	public PodStats getPods() {
		log.info("Updating k8s stats");

		V1PodList podList;
		try {
			podList = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
		} catch (ApiException e) {
			throw new RuntimeException(e);
		}

		Date seen = new Date();

		int total = 0;
		PodStats stats = new PodStats();
		List<Pod> pods = new ArrayList<>();
		List<String> alive = new ArrayList<>();
		for (V1Pod item : podList.getItems()) {
			Pod p = new Pod();
			p.setAlive(true);
			String name = item.getMetadata().getName();
			p.setNamespace(item.getMetadata().getNamespace());
			alive.add(name);
			p.setName(name);
			p.setCreated(item.getMetadata().getCreationTimestamp().toDate());
			p.setSeen(seen);
			pods.add(p);
			int count = item.getStatus().getContainerStatuses().size();
			total += count;
			p.setCount(count);
			int healthy = 0;
			for (V1ContainerStatus status : item.getStatus().getContainerStatuses()) {
				if (status.isReady() == true) {
					healthy++;
				}
			}
			p.setHealthy(healthy);
			podRepo.save(p);
		}
		int priorMax = total;
		if (statRepo.findById(statId).isPresent()) {
			priorMax = statRepo.findById(statId).get().getMaxPods();
		}
		if (total > priorMax) {
			priorMax = total;
		}

		for (Pod p : podRepo.findAll()) {
			if (!alive.contains(p.getName())) {
				p.setAlive(false);
				podRepo.save(p);
			}
		}

		stats.setMaxPods(priorMax);
		stats.setCurrentPods(total);
		stats.setUpdated(seen);
		stats.setId(statId);
		statRepo.save(stats);
		return stats;
	}
}
