package dev.alnat.simpleratelimiter.repository;

import dev.alnat.simpleratelimiter.model.ClientSetting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientSettingRepository extends CrudRepository<ClientSetting, Integer> {

    Optional<ClientSetting> findClientSettingByApiKey(String apiKey);

}
