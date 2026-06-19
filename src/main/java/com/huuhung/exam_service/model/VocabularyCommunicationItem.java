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
public class VocabularyCommunicationItem implements Serializable {
    private String key;      // Ví dụ: "잠시", "고민 끝에"
    private String meaning;  // Ví dụ: "Một lát, một chút", "Sau khi suy nghĩ kỹ"
}
