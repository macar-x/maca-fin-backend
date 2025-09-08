package com.macacloud.fin.model.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Category Creation Inbound Parameters.
 *
 * @author Emmett
 * @since 2025/09/07
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CategoryCreationRequest {

    @JsonProperty(value = "parent_id")
    private Long parentId;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "description")
    private String description;

    @JsonProperty(value = "logo_path")
    private String logoPath;
}
