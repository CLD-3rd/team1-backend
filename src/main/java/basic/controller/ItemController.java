package basic.controller;

import basic.cachedto.LpSalesDto;
import basic.dto.ItemRequest;
import basic.dto.ItemResponse;
import basic.service.CounterService;
import basic.service.ItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/items")
@Slf4j
public class ItemController {

	private final ItemService itemService;
	private final CounterService counterService;
	public ItemController(ItemService itemService, CounterService counterService) {
		this.itemService = itemService;
		this.counterService = counterService;
	}
	@GetMapping("/list")
	public String getAllItems(Model model) {
		List<ItemResponse> items = itemService.findItems();
		List<LpSalesDto> topSales = itemService.getTopSales(5);
		List<Long> topItemIds = topSales.stream().map(LpSalesDto::getId).toList();
		model.addAttribute("popularItems", topSales);
		model.addAttribute("popularItemIds", topItemIds); // 추가
		model.addAttribute("items", items);
		return "items/list";
	}
	@GetMapping("/new")
	public String createItemDto(Model model) {
		model.addAttribute("item", new ItemRequest());
		return "items/createItemDto";
	}
	// 상품 등록 처리
	@PostMapping("/new")
	public String create(@ModelAttribute("item") ItemRequest itemRequest, @RequestPart("file") MultipartFile file,
			HttpServletRequest request) {
		log.debug("Request content type: {}", request.getContentType());
		log.debug("File present: {}", file != null);
		itemService.saveItem(itemRequest, file);
		return "redirect:/home";
	}
	// 상품 상세 (또는 수정폼으로 활용 가능)
	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		// 조회수 +1
		Long viewCount = counterService.incrementViewCount(id);
		ItemResponse item = itemService.findOne(id);
		model.addAttribute("item", item);
		model.addAttribute("viewCount", viewCount);
		return "items/detail";
	}
}
