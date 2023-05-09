package tn.esprit.spring.Material;

import java.util.List;

public interface IMaterialService {

    public List<Material> getAllMaterials();

    public Material updateMaterial (Material material);

    public Material addMaterial (Material material);

    public Material getMaterialById (Long  codeMaterial);

    public void removeMaterial(Long codeMaterial);

}
