package com.youthlin.example.javafx;

import com.youthlin.example.javafx.bean.CatalogItem;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author youthlin.chen
 * @date 2019-10-08 10:48
 */
@Slf4j
public class App extends Application {
    private Button add = newButton("N新增项目");
    private Button addChild = newButton("C新增子项目");
    private Button edit = newButton("E编辑项目文字");
    private Button delete = newButton("D删除选中项目");
    private Button fromJson = newButton("从Json生成");
    private TreeTableView<CatalogItem> treeTableView = new TreeTableView<>(new TreeItem<>(new CatalogItem("目录", 0)));
    private static final StringConverter<Integer> CONVERTER = new StringConverter<Integer>() {
        @Override
        public String toString(Integer object) {
            return String.valueOf(object);
        }

        @Override
        public Integer fromString(String string) {
            return Integer.parseInt(string);
        }
    };

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("PDF 加目录");

        TreeTableColumn<CatalogItem, String> titleColumn = new TreeTableColumn<>("标题");
        titleColumn.setCellValueFactory(param -> param.getValue().getValue().getTitle());
        TreeTableColumn<CatalogItem, Number> pageColumn = new TreeTableColumn<>("页码");
        pageColumn.setCellValueFactory(param -> param.getValue().getValue().getPage());
        treeTableView.getColumns().add(titleColumn);
        treeTableView.getColumns().add(pageColumn);
        treeTableView.setEditable(true);
        treeTableView.setShowRoot(false);
        treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        BorderPane pane = new BorderPane();
        pane.setLeft(treeTableView);

        TextArea textArea = new TextArea();
        pane.setRight(textArea);

        Label infoLabel = new Label("就绪");
        pane.setBottom(infoLabel);

        HBox topBox = new HBox(add, addChild, delete, edit, fromJson);
        pane.setTop(topBox);

        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button newButton(String name) {
        Button button = new Button(name);
        button.setOnAction(this::handleButton);
        return button;
    }

    private void handleButton(ActionEvent event) {
        Object source = event.getSource();
        TreeItem<CatalogItem> selected = getSelected();
        if (source == add) {
            if (selected == null) {
                treeTableView.getRoot().getChildren().add(newItem());
            } else {
                ObservableList<TreeItem<CatalogItem>> children = treeTableView.getRoot().getChildren();
                int i = 0;
                for (TreeItem<CatalogItem> child : children) {
                    if (child == selected) {
                        break;
                    }
                    i++;
                }
                children.add(i, newItem());
            }
        }
        if (source == addChild) {
            if (selected != null) {
                selected.getChildren().add(newItem());
                selected.setExpanded(true);
            }
        }
    }

    private TreeItem<CatalogItem> getSelected() {
        TreeTableView.TreeTableViewSelectionModel<CatalogItem> selectionModel = treeTableView.getSelectionModel();
        if (selectionModel == null) {
            return null;
        }
        return selectionModel.getSelectedItem();
    }

    private static TreeItem<CatalogItem> newItem() {
        return new TreeItem<>(new CatalogItem("目录", 0));
    }
}
