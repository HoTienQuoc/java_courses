package different_types_of_classes_abstract_keyword_and_abstract_classes;

public class VariantProduct extends Product {

    private MasterProduct[] variants;

    @Override
    public boolean isAvailableInStock() {
        for (MasterProduct masterProduct : variants) {
            if (masterProduct.isAvailableInStock()) {
                return true;
            }
        }
        return false;
    }

}
