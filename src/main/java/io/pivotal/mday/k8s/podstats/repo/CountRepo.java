package io.pivotal.mday.k8s.podstats.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.pivotal.mday.k8s.podstats.model.PodStats;

@Repository
public interface CountRepo extends CrudRepository<PodStats, String> {

}