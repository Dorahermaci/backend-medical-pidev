package tn.esprit.spring.Material;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface MaterialRepository extends JpaRepository<Material,Long> {

    @Query("FROM Material m")
    List<Material> getAllMaterialData();

}
