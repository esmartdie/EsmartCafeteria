package com.esmartdie.EsmartCafeteriaApi.repository.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IClientRepository extends JpaRepository<Client, Long> {

    List <Client> findAll();

    @Query("SELECT c FROM Client c WHERE c.active = true")
    List<Client> findAllActive();

    @Query("SELECT c FROM Client c WHERE c.active = false")
    List<Client> findAllInactive();


}
