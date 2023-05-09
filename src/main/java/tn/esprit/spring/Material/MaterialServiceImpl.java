package tn.esprit.spring.Material;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class MaterialServiceImpl implements IMaterialService {

    @Autowired
    MaterialRepository materialRepository;

    @Override
    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    @Override
    public Material updateMaterial(Material material) { return materialRepository.save(material);}

    @Override
    public Material addMaterial(Material material) {
        return materialRepository.save(material);
    }

    @Override
    public Material getMaterialById(Long codeMaterial) {
        return null;
    }

    @Override
    public void removeMaterial(Long codeMaterial) { materialRepository.deleteById(codeMaterial);}
}
