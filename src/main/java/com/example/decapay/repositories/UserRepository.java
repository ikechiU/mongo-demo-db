package com.example.decapay.repositories;
import com.example.decapay.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
   Optional<User> findByUuid(String uuid);
   Optional<User> findByEmailAddress(String email);

   boolean existsByEmailAddress(String email);
}
