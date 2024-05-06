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
    private ListView<Task> taskListView = new ListView<>();
    private TabPane tabPane;
    private Tab allTab, doneTab, undoneTab;

    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 350, 600);

        filteredTasks = new FilteredList<>(tasks);
        taskListView.setItems(filteredTasks);
        taskListView.setCellFactory(param -> new TaskCell());

        root.setTop(buildTop());
        root.setCenter(buildTabs());

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
                    tasks.add(newTask);
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

        tabPane.getSelectionModel().select(allTab);

        allTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                allTab.setContent(createTaskListView(filteredTasks));
            }
        });

        doneTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                doneTab.setContent(createTaskListView(filteredTasks.filtered(task -> task.getCompleted())));
            }
        });

        undoneTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                undoneTab.setContent(createTaskListView(filteredTasks.filtered(task -> !task.getCompleted())));
            }
        });

        tabPane.getTabs().addAll(allTab, doneTab, undoneTab);

        return tabPane;
    }

    private ListView<Task> createTaskListView(ObservableList<Task> list) {
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
                HBox taskNode = new HBox();
                HBox taskLabel;
                Button deleteBtn;
                Pane spacer = new Pane();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                spacer.setMinSize(10, 1);

                taskLabel = buildTaskLabel(task);
                deleteBtn = buildDeleteBtn(task);

                taskNode.getChildren().addAll(taskLabel, spacer, deleteBtn);
//                taskNode.getChildren().addAll(taskLabel, spacer);
                setGraphic(taskNode);
            }
        }

        private Button buildDeleteBtn(Task task) {
            Button deleteBtn = new Button("X");
            deleteBtn.setStyle("-fx-color: #cf3d34");
            deleteBtn.setOnAction(event -> {
                tasks.remove(task);
                filteredTasks.setPredicate(t -> true);
//                tabPane.getSelectionModel().select(tabPane.getSelectionModel().getSelectedItem());
            });
            return deleteBtn;
        }

        private HBox buildTaskLabel(Task task) {
            HBox taskLable = new HBox();
            Text name = new Text(task.getName());
            CheckBox checkBox = new CheckBox();
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

    public class Task {
        private String name;
        private boolean completed;

        public Task(String name, boolean completed) {
            this.name = name;
            this.completed = completed;
        }

        public String getName() {
            return name;
        }

        public boolean getCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
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