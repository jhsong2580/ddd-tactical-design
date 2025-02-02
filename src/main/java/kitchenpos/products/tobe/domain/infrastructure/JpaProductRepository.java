package kitchenpos.products.tobe.domain.infrastructure;

import java.util.UUID;
import kitchenpos.products.tobe.domain.Product;
import kitchenpos.products.tobe.domain.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<Product, UUID>, ProductRepository {

}
