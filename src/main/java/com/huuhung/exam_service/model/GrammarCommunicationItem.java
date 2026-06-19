/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.model;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrammarCommunicationItem implements Serializable {
    private String structure;    // Ví dụ: "V + (으)ㄹ 수 있다/없다"
    private String explanation;  // Ví dụ: "Có thể / Không thể làm gì"
}
