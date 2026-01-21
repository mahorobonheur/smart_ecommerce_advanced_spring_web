package com.smart.ecommerce.dto.graphql;

import com.smart.ecommerce.dto.response.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class UserPageDTO {
    private List<UserResponseDTO> users;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
}
