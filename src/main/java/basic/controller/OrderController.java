package basic.controller;

import basic.dto.ItemResponse;
import basic.entity.Item;
import basic.entity.Member;
import basic.entity.Order;
import basic.service.ItemService;
import basic.service.MemberService;
import basic.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final ItemService itemService;


    public OrderController(OrderService orderService, ItemService itemService) {
        this.orderService = orderService;
        this.itemService = itemService;
    }


    @GetMapping("/new")
    public String createDto(Model model) {
        List<ItemResponse> items = itemService.findItems();
        model.addAttribute("items", items);
        return "orders/createOrderDto";
    }

    @PostMapping("/new")
    public String order(
            HttpSession session,  // 또는 @AuthenticationPrincipal 사용 가능
            @RequestParam Long itemId,
            @RequestParam int count) {

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }

        orderService.order(loginMember.getId(), itemId, count);
        return "redirect:/orders";
    }


    @GetMapping
    public String orderList(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }

        List<Order> orders = orderService.findOrdersByMemberId(loginMember.getId());
        model.addAttribute("orders", orders);
        return "orders/orderList";
    }

    /**
     * 주문 취소
     */
    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
