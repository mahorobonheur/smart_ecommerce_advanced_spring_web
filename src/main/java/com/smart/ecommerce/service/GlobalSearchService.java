package com.smart.ecommerce.service;

import java.util.List;
import java.util.Map;

public interface GlobalSearchService {
    Map<String, List<?>> searchAll(String keyWord);
}
