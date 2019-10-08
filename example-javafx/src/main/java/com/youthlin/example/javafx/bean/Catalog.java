package com.youthlin.example.javafx.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @author youthlin.chen
 * @date 2019-10-08 15:35
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Catalog {
    private CatalogItem item;
    private List<Catalog> children;
}
