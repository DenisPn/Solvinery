package Persistence.Repositories;

import Persistence.Entities.UserEntity;
import User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;
@Validated
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

}
