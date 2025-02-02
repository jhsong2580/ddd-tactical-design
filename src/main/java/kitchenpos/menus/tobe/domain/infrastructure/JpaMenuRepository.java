package kitchenpos.menus.tobe.domain.infrastructure;

import java.util.List;
import java.util.UUID;
import kitchenpos.menus.tobe.domain.Menu;
import kitchenpos.menus.tobe.domain.MenuRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaMenuRepository extends MenuRepository, JpaRepository<Menu, UUID> {

    @Query("select m from Menu m join m.menuProducts mp where mp.product.id = :productId")
    List<Menu> findAllByProductId(@Param("productId") UUID productId);
}
