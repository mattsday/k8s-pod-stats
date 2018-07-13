package io.pivotal.mday.k8s.podstats.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.pivotal.mday.k8s.podstats.model.Pod;

@Repository
public interface PodRepo extends CrudRepository<Pod, String> {

	public List<Pod> findByNamespace(@Param(value = "namespace") String namespace);

}