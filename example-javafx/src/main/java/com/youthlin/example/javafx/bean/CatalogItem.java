package com.youthlin.example.javafx.bean;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Data;

/**
 * @author youthlin.chen
 * @date 2019-10-08 14:33
 */
@Data
public class CatalogItem {
    private final SimpleStringProperty title = new SimpleStringProperty();
    private final SimpleIntegerProperty page = new SimpleIntegerProperty();

    public CatalogItem(String title, int page) {
        this.title.set(title);
        this.page.set(page);
    }
}
