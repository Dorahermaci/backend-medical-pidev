package tn.esprit.spring.Material;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/material")

public class MaterialRestController {
    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    IMaterialService materialService;

    @GetMapping("/get-All-Materials")
    public List<Material> getMaterials() {
        List<Material> listMaterials = materialService.getAllMaterials();
        return listMaterials;
    }

    @PostMapping("/add-material")
    public Material addMaterial(@RequestBody Material material) {
        materialService.addMaterial(material);
        return material;
    }

    @PutMapping("/update-material")
    public Material updateMaterial(@RequestBody Material material) {

        return materialService.updateMaterial(material);

    }
    @DeleteMapping("/remove-Material/{codeMaterial}")
    public void removeMaterial(@PathVariable("codeMaterial") Long codeMaterial) {

        materialService.removeMaterial(codeMaterial);
    }

}
