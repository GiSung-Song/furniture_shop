package furniture.shop.credit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditRepository extends JpaRepository<Credit, Long> {
    Credit findByOrdersId(Long ordersId);
    Credit findByImpUID(String impUID);
}
