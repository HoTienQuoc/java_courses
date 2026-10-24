package different_types_of_classes_abstract_keyword_and_abstract_classes;

public class MasterProduct extends Product {

    private boolean isAvailableForLease;

    @Override
    public boolean isAvailableInStock() {
        return getRemainingAmountInStock() > 0;
    }
}
