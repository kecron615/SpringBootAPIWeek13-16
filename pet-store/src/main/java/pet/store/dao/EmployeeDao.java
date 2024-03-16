package pet.store.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pet.store.entity.Employee;
import pet.store.entity.PetStore;

public interface EmployeeDao extends JpaRepository<Employee, Long> {


}
