package com.earlystart.kirichenkovthreatrix.dao;

import com.earlystart.kirichenkovthreatrix.model.User;
import jdk.jfr.Registered;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;


@Registered
public interface UserDao extends CassandraRepository<User, String> {
    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    @Query("SELECT * FROM user WHERE email > ?0 LIMIT ?1 ALLOW FILTERING")
    List<User> findAllWithPaging(String startAfterEmail, int limit);

    @Query("SELECT * FROM user WHERE organization = ?0 AND email > ?1 LIMIT ?2 ALLOW FILTERING")
    List<User> findByOrganizationWithPagination(String organization, String lastEmail, int limit);
}
