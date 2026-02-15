package com.akshansh.taskmanagementplatform.dto.response;

import com.akshansh.taskmanagementplatform.entity.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabelResponse {
    private Long id;
    private String name;
    private String color;

    public static LabelResponse convertToDto(Label label){
        return new LabelResponse(
            label.getId(),
            label.getName(),
            label.getColor()
        );
    }
}
