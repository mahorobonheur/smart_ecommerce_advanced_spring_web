package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.response.GlobalSearchResponseDTO;

public interface GlobalSearchService {
    public GlobalSearchResponseDTO searchAll(String keyWord);
}
