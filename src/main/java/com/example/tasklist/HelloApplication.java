package com.example.tasklist;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.event.Event;
import javafx.event.EventHandler;
import java.io.IOException;
import java.lang.Exception;

public class HelloApplication extends Application {

    private ObservableList<Task> tasks = FXCollections.observableArrayList();
    private FilteredList<Task> filteredTasks;
    private TabPane tabPane;
    private Tab allTab, doneTab, undoneTab;

    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 350, 600);

        filteredTasks = new FilteredList<>(tasks);

        root.setTop(buildTop());
        root.setCenter(buildTabs());

        // select the default tab - allTab
        tabPane.getSelectionModel().select(allTab);
        refreshTabContent();

        stage.setTitle("Todo List");
        stage.setScene(scene);
        stage.show();
    }

    public HBox buildTop() {
        TextField taskInput = new TextField();
        Button submitBtn = new Button("Add Task");

        submitBtn.setOnAction(e -> {
            String taskName = taskInput.getText().trim();
            if (!taskName.isEmpty()) {
                if (isTaskDuplicate(taskName)) {
                    showAlert("Task is duplicated!");
                } else {
                    Task newTask = new Task(taskName, false);
                    tasks.addFirst(newTask);
                    tabPane.getSelectionModel().select(allTab); // when add tab, re-initialize the default tab - addTab
                    refreshTabContent();
                    taskInput.clear();
                }
            }
        });

        HBox hBox = new HBox(10, taskInput, submitBtn);
        hBox.setPadding(new Insets(10));
        return hBox;
    }

    public TabPane buildTabs() {
        tabPane = new TabPane();
        allTab = new Tab("All");
        doneTab = new Tab("Done");
        undoneTab = new Tab("Undone");

        allTab.setClosable(false);
        doneTab.setClosable(false);
        undoneTab.setClosable(false);

        EventHandler<Event> tabEventHandler = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                refreshTabContent();
            }
        };

        allTab.setOnSelectionChanged(tabEventHandler);
        doneTab.setOnSelectionChanged(tabEventHandler);
        undoneTab.setOnSelectionChanged(tabEventHandler);

        tabPane.getTabs().addAll(allTab, doneTab, undoneTab);

        return tabPane;
    }

    private ListView<Task> createTaskListView(FilteredList<Task> list) {
        ListView<Task> listView = new ListView<>();
        listView.setItems(list);
        listView.setCellFactory(param -> new TaskCell());
        return listView;
    }

    private boolean isTaskDuplicate(String taskName) {
        for (Task task : tasks)
            return task.getName().equalsIgnoreCase(taskName);
        return false;
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Oops!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private class TaskCell extends ListCell<Task> {
        @Override
        protected void updateItem(Task task, boolean empty) {
            super.updateItem(task, empty);
            if (empty || task == null) {
                setText(null);
            } else {
                setGraphic(generateTaskNode(task));
            }
        }

        private HBox generateTaskNode (Task task) {
            HBox taskNode = new HBox();
            HBox taskLabel;
            Button deleteBtn;
            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            spacer.setMinSize(10, 1);

            taskLabel = buildTaskLabel(task);
            deleteBtn = buildDeleteBtn(task);

            taskNode.getChildren().addAll(taskLabel, spacer, deleteBtn);
            return taskNode;
        }

        private Button buildDeleteBtn(Task task) {
            Button deleteBtn = new Button("X");
            deleteBtn.setStyle("-fx-color: #cf3d34");
            deleteBtn.setOnAction(event -> {
                tasks.remove(task);
                refreshTabContent();
            });
            return deleteBtn;
        }

        private HBox buildTaskLabel(Task task) {
            HBox taskLable = new HBox();
            Text name = new Text(task.getName());
            CheckBox checkBox = new CheckBox();

            name.setStrikethrough(task.getCompleted());
            checkBox.setSelected(task.getCompleted());

            checkBox.setOnAction(event -> {
                task.setCompleted(!task.getCompleted());
                name.setStrikethrough(task.getCompleted());
            });

            taskLable.setSpacing(5);
            taskLable.getChildren().addAll(checkBox, name);
            taskLable.setAlignment(Pos.CENTER);
            return taskLable;
        }
    }

    private void refreshTabContent() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == allTab) {
            allTab.setContent(createTaskListView(filteredTasks.filtered(task -> true)));
        } else if (selectedTab == doneTab) {
            doneTab.setContent(createTaskListView(filteredTasks.filtered(task -> task.getCompleted())));
        } else if (selectedTab == undoneTab) {
            undoneTab.setContent(createTaskListView(filteredTasks.filtered(task -> !task.getCompleted())));
        }
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch(Exception err) {
            showAlert("Error! Connect with IT department.");
        }

    }
}