package furniture.shop.order;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import furniture.shop.member.Member;
import furniture.shop.member.QMember;
import furniture.shop.order.contsant.OrdersStatus;
import furniture.shop.order.dto.OrdersListResponseDto;
import furniture.shop.order.dto.QOrdersListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static furniture.shop.order.QOrders.orders;

@Repository
public class OrdersQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public OrdersQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<Orders> getEmptyOrders() {
        List<Orders> content = jpaQueryFactory
                .select(orders)
                .from(orders)
                .where(orders.ordersStatus.eq(OrdersStatus.READY)
                        .and(orders.registerDate.lt(LocalDateTime.now().minusMinutes(30))))
                .fetch();

        return content;
    }

    public Page<OrdersListResponseDto> getOrderList(Member member, Pageable pageable) {
        List<OrdersListResponseDto> content = jpaQueryFactory
                .select(new QOrdersListResponseDto(
                        orders.id,
                        orders.totalPrice,
                        orders.ordersStatus,
                        orders.registerDate,
                        orders.updateDate,
                        orders.address.zipCode,
                        orders.address.city,
                        orders.address.street,
                        orders.receiver
                ))
                .from(orders)
                .where(orders.member.id.eq(member.getId()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(new OrderSpecifier<>(Order.DESC, orders.registerDate))
                .fetch();

        JPAQuery<Long> contentQuery = jpaQueryFactory
                .select(orders.count())
                .from(orders)
                .where(orders.member.id.eq(member.getId()));

        return PageableExecutionUtils.getPage(content, pageable, () -> contentQuery.fetchOne());
    }
}
