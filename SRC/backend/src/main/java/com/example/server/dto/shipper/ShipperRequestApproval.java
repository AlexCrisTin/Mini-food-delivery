package com.example.server.dto.shipper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipperRequestApproval {
    private boolean approved;
    private String adminNote;
}
