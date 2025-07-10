package basic.service;

import basic.dto.ItemRequest;
import basic.dto.ItemResponse;
import basic.entity.Item;
import basic.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final S3Service s3Service;

    @Value("${cloud.aws.s3.bucket-url}")
    private String bucketUrl;

    public ItemService(ItemRepository itemRepository, S3Service s3Service) {
        this.itemRepository = itemRepository;
        this.s3Service = s3Service;
    }

    @Transactional
    public void saveItem(ItemRequest itemRequest, MultipartFile file) {
        String uploadedFileName = null;

        if (file != null && !file.isEmpty()) {
            try {
                uploadedFileName = s3Service.uploadS3File(file);
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        }
        log.info("uploadFileName = {}", uploadedFileName);
        Item item = Item.of(
                itemRequest.getName(),
                itemRequest.getPrice(),
                itemRequest.getStockQuantity(),
                uploadedFileName // 업로드된 파일명을 저장
        );

        itemRepository.save(item);
    }

    public List<ItemResponse> findItems() {
        List<Item> findItems = itemRepository.findAll();

        return findItems.stream()
                .map(item -> {
                    String fullImageUrl = null;
                    if (item.getImageFileName() != null) {
                        fullImageUrl = bucketUrl + item.getImageFileName();
                    }
                    return new ItemResponse(
                            item.getId(),
                            item.getName(),
                            item.getPrice(),
                            item.getStockQuantity(),
                            item.getImageFileName(),
                            fullImageUrl
                    );
                })
                .collect(Collectors.toList());
    }

    public ItemResponse findOne(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalStateException("존재하지 않는 상품 입니다."));
        return ItemResponse.fromEntity(item, bucketUrl);
    }

}
