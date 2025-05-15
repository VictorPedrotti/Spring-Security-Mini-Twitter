package estudos.java.spring.security.dto;

import java.util.List;

public record FeedDto(List<FeedItemDto> feedItens, int page, int totalPages, Long totalElements) {

}
