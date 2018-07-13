package io.pivotal.mday.k8s.podstats.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.pivotal.mday.k8s.podstats.model.DateRecord;
import io.pivotal.mday.k8s.podstats.model.Pod;
import io.pivotal.mday.k8s.podstats.model.PodStats;
import io.pivotal.mday.k8s.podstats.repo.CountRepo;
import io.pivotal.mday.k8s.podstats.repo.DateRecordRepo;
import io.pivotal.mday.k8s.podstats.repo.PodRepo;
import io.pivotal.mday.k8s.podstats.services.K8sService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ApiController {

	@NonNull
	private PodRepo podRepo;
	@NonNull
	private K8sService k8s;
	@NonNull
	private CountRepo statRepo;
	@NonNull
	private DateRecordRepo dateRepo;

	@Value("${redis.stats.id}")
	private String statId;
	@Value("${redis.date.id}")
	private String dateId;

	@GetMapping("/")
	public Map<String, Object> getPods(@RequestParam(name = "namespace", required = false) String namespace,
			@RequestParam(name = "active", required = false) Boolean active) {
		Map<String, Object> m = new HashMap<>();
		Iterable<Pod> pods = podRepo.findAll();
		List<Pod> podList = new ArrayList<>();
		pods.forEach(podList::add);

		int total = 0;
		int current = 0;

		if (namespace != null) {
			podList = getNamespacePods(podList, namespace);
		}

		for (Pod p : podList) {
			total += p.getCount();
		}

		if (active != null) {
			podList = getActivePods(podList, active);
		}

		for (Pod p : podList) {
			if (p.getAlive())
				current += p.getCount();
		}

		PodStats ps = statRepo.findById(statId).get();

		ps.setCurrentPods(current);
		ps.setMaxPods(total);

		m.put("stats", ps);
		m.put("pods", podList);

		return m;
	}

	private List<Pod> getNamespacePods(List<Pod> pods, String namespace) {

		List<Pod> podList = new ArrayList<>();

		for (Pod p : pods) {
			if (namespace.equals(p.getNamespace())) {
				podList.add(p);
			}
		}

		return podList;
	}

	private List<Pod> getActivePods(List<Pod> pods, Boolean active) {
		List<Pod> podList = new ArrayList<>();

		for (Pod p : pods) {
			if (p.getAlive() == active) {
				podList.add(p);
			}
		}
		return podList;
	}

	@GetMapping("/stats")
	public DateRecord getStats() {
		return dateRepo.findById(dateId).get();
	}

}
