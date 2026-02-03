package com.smart.ecommerce.controller.rest;

import com.smart.ecommerce.service.GlobalSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    @Autowired
    private GlobalSearchService globalSearchService;

    @GetMapping("/global")
    public Map<String, List<?>> globalSearch(@RequestParam String keyWord){
        return globalSearchService.searchAll(keyWord);
    }
}
