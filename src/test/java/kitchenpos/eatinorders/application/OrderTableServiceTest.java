package kitchenpos.eatinorders.application;

import static kitchenpos.eatinorders.OrderTableFixture.createRequestBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import kitchenpos.eatinorders.OrderTableFixture;
import kitchenpos.eatinorders.domain.OrderRepository;
import kitchenpos.eatinorders.domain.OrderTable;
import kitchenpos.eatinorders.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class OrderTableServiceTest {

    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableRepository = new InMemoryOrderTableRepository();
        orderRepository = new InMemoryOrderRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("테이블을 등록할수 있다. 생성된 테이블은 '사용중이 아님'이며, 앉아있는 손님이 없다")
    @Test
    void test1() {
        //given
        OrderTable createRequest = createRequestBuilder()
            .name("table1")
            .build();

        //when
        OrderTable result = orderTableService.create(createRequest);

        //then
        assertAll(
            () -> assertThat(result.getId()).isNotNull(),
            () -> assertThat(result.getNumberOfGuests()).isEqualTo(0),
            () -> assertThat(result.isOccupied()).isFalse()
        );
    }

    @DisplayName("테이블의 상태를 '사용중'으로 변경할수 있다")
    @Test
    void test2() {
        //given
        OrderTable createRequest = createRequestBuilder()
            .name("table1")
            .build();
        OrderTable createdTable = orderTableService.create(createRequest);

        //when
        OrderTable result = orderTableService.sit(createdTable.getId());

        //then
        assertAll(
            () -> assertThat(result.getNumberOfGuests()).isEqualTo(0),
            () -> assertThat(result.isOccupied()).isTrue()
        );
    }

    @DisplayName("테이블에 손님을 앉힐수 있다")
    @ParameterizedTest
    @ValueSource(ints = {0, 10, 100, 1_000, 10_000, 100_000})
    void test3(int numberOfGuest) {
        //given
        OrderTable createRequest = createRequestBuilder()
            .name("table1")
            .build();
        OrderTable createdTable = orderTableService.create(createRequest);
        orderTableService.sit(createdTable.getId());
        OrderTable updateRequest = OrderTableFixture.updateRequestBuilder()
            .numberOfGuests(numberOfGuest)
            .build();

        //when
        OrderTable result = orderTableService.changeNumberOfGuests(createdTable.getId(), updateRequest);

        //then
        assertThat(result.getNumberOfGuests())
            .isEqualTo(numberOfGuest);
    }

    @DisplayName("사용중이 아닌 테이블엔 손님을 앉힐수 없다.")
    @Test
    void test4() {
        //given
        OrderTable createRequest = createRequestBuilder()
            .name("table1")
            .build();
        OrderTable createdTable = orderTableService.create(createRequest);
        OrderTable updateRequest = OrderTableFixture.updateRequestBuilder()
            .numberOfGuests(1)
            .build();

        //when && then
        assertThatThrownBy(
            () -> orderTableService.changeNumberOfGuests(createdTable.getId(), updateRequest)
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블에 손님은 음수가 될수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -100, -1_000, -10_000, -100_000})
    void test5(int numberOfGuest) {
        //given
        OrderTable createRequest = createRequestBuilder()
            .name("table1")
            .build();
        OrderTable createdTable = orderTableService.create(createRequest);
        orderTableService.sit(createdTable.getId());
        OrderTable updateRequest = OrderTableFixture.updateRequestBuilder()
            .numberOfGuests(numberOfGuest)
            .build();

        //when && then
        assertThatThrownBy(
            () -> orderTableService.changeNumberOfGuests(createdTable.getId(), updateRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블의 모든 정보를 조회할수 있다")
    @Test
    void test6() {
        //given
        OrderTable createdTable1 = orderTableService.create(createRequestBuilder().name("table2").build());
        OrderTable createdTable2 = orderTableService.create(createRequestBuilder().name("table1").build());

        //when
        List<OrderTable> result = orderTableService.findAll();

        //then
        assertThat(result).extracting("id")
            .containsExactlyInAnyOrder(createdTable1.getId(), createdTable2.getId());

    }
}
