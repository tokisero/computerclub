package com.tokiserskyy.computerclub.repository;

import com.tokiserskyy.computerclub.model.Computer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface ComputerRepository extends JpaRepository<Computer, Integer>, JpaSpecificationExecutor<Computer> {
    Computer getComputersById(int id);
}
