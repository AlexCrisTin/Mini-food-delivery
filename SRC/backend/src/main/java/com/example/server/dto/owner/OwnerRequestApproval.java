package com.example.server.dto.owner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerRequestApproval {
    private boolean approved;
    private String adminNote;
}
