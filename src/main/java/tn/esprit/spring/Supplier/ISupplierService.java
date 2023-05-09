package tn.esprit.spring.Supplier;

import java.util.List;

public interface ISupplierService {

    public List<Supplier> retrieveAllSuppliers();

    public Supplier updateSupplier (Supplier  supplier);

    public  Supplier addSupplier (Supplier supplier);

    public Supplier retrieveSupplier (Integer  idSupplier);

    public void removeSupplier(Integer idSupplier);

}
