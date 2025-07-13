package basic.service;

import basic.entity.*;
import basic.repository.ItemRepository;
import basic.repository.MemberRepository;
import basic.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    public OrderService(MemberRepository memberRepository, OrderRepository orderRepository, ItemRepository itemRepository) {
        this.memberRepository = memberRepository;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
    }

    /** 주문 */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        //엔티티 조회
        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));
        Item findItem = itemRepository.findById(itemId).orElseThrow(() -> new IllegalStateException("존재하지 않는 상품입니다."));

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(findItem, findItem.getPrice(), count);
        //주문 생성
        Order order = Order.createOrder(findMember, orderItem);
        //주문 저장
        orderRepository.save(order);
        return order.getId();
    }

    /** 장바구니 주문 */
    @Transactional
    public void orderFromCart(Long memberId, List<CartItem> cartItems) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    Item item = itemRepository.findById(cartItem.getItem().getId())
                            .orElseThrow(() -> new IllegalStateException("존재하지 않는 상품입니다."));
                    return OrderItem.createOrderItem(item, item.getPrice(), cartItem.getCount());
                })
                .collect(Collectors.toList());


        Order order = Order.createOrder(member, orderItems.toArray(new OrderItem[0]));

        orderRepository.save(order);

    }


    /** 주문 취소 */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("존재하지 않는 주문입니다."));
        order.cancel();
    }

    public List<Order> findOrdersByMemberId(Long memberId) {
        return orderRepository.findByMemberId(memberId);
    }


    /** 주문 검색 */
/*
 public List<Order> findOrders(OrderSearch orderSearch) {
 return orderRepository.findAll(orderSearch);
 }
*/
}