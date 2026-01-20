package com.proiect.repository;

import com.proiect.entity.EvenimenteSecuritate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EvenimenteSecuritateRepository extends JpaRepository<EvenimenteSecuritate, Long> {

    // Custom query to find events by server ID
    List<EvenimenteSecuritate> findByServer_ServerId(Long serverId);

    // Custom query to find events by actor IP
    List<EvenimenteSecuritate> findByActor_AdresaIp(String adresaIp);

    // Custom query to find recent events (last 24 hours)
    List<EvenimenteSecuritate> findByDataEvenimentAfter(LocalDateTime dateTime);

    // Custom query for top attackers by count
    // (Can be used with @Query for complex aggregation)
}