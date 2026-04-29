package com.jorge.gestorTorneos.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.jorge.gestorTorneos.model.entity.Torneo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TorneoPageResponse {

    private List<TorneoResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private int numberOfElements;

    public static TorneoPageResponse fromPage(Page<Torneo> page) {
        return TorneoPageResponse.builder()
                .content(page.getContent().stream().map(TorneoResponse::fromEntity).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .build();
    }
}
