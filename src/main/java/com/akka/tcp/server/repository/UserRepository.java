package com.akka.tcp.server.repository;



import com.akka.tcp.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	//	JPQL 예제
	@Query("SELECT u from User u where u.userId = :userId")
	User findByUserId(@Param("userId") String userId);
	User findByUserName(String userName);
}