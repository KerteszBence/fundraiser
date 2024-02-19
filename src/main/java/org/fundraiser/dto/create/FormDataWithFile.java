package org.fundraiser.dto.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormDataWithFile {

    private CommonsMultipartFile file;

    private String title;

    private String category;

    private Long account_id;

    private Long campaign_id;

}