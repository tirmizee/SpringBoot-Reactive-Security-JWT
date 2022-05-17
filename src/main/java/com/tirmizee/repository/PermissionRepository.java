package com.tirmizee.repository;

import com.tirmizee.repository.entity.PermissionEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends ReactiveSortingRepository<PermissionEntity, Integer> {

    

}
