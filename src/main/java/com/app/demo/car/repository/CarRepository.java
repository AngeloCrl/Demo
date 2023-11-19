package com.app.demo.car.repository;

import com.app.demo.car.model.Car;
import com.app.demo.car.repository.projection.ProjectionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {
     String PROJECTION_QUERY = "SELECT c.id AS id, c.brand AS brand, c.model AS model, c.creation_date AS creationDate, c.last_mod_date AS lastModDate, m.name AS manufacturer " +
            "FROM car c " +
            "LEFT JOIN manufacturer m ON c.manufacturer_id=m.id " +
            "WHERE c.id IS NOT NULL";

    @Query(value = PROJECTION_QUERY, nativeQuery = true)
    List<ProjectionResult> getResults();

    List<Car> findByManufacturer_Id(Long id);
    List<Car> findByOwner_Id(Long id);
}