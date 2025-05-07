//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.phase3;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class YelpController {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String JDBC_URL;
    private static final String JDBC_USER;
    private static final String JDBC_PASS;
    private Connection connection;
    @FXML
    private Label searchText;
    @FXML
    private ComboBox<String> stateComboBox;
    @FXML
    private Button filterButton;
    @FXML
    private ListView<String> categoryList;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<Business> businessTable;
    @FXML
    private TableColumn<Business, String> nameColumn;
    @FXML
    private TableColumn<Business, String> addressColumn;
    @FXML
    private TableColumn<Business, String> cityColumn;
    @FXML
    private ComboBox<String> cityselectbox;

    @FXML
    void initialize() {
        this.updateStates();
        this.nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        this.addressColumn.setCellValueFactory(new PropertyValueFactory("address"));
        this.cityColumn.setCellValueFactory(new PropertyValueFactory("city"));
        this.categoryList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.categoryList.setItems(FXCollections.observableArrayList());
        this.stateComboBox.getSelectionModel().selectedItemProperty().addListener((observable, olsState, newState) -> {
            if (newState != null) {
                this.updateCategories(newState);
                this.updateCities(newState);
            }

        });
        this.filterButton.setOnAction((event) -> this.updateCategories((String)this.stateComboBox.getSelectionModel().getSelectedItem()));
        this.searchButton.setOnAction((event) -> this.searchBusinesses());
        this.businessTable.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                Business selected = (Business)this.businessTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    this.loadBusinessPage(selected);
                }
            }

        });
    }

    private void updateCities(String state) {
        ObservableList<String> cities = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT city FROM business WHERE state = ? ORDER BY city";

        try {
            this.connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);

            try {
                PreparedStatement ps = this.connection.prepareStatement(query);

                try {
                    ps.setString(1, state);
                    ResultSet rs = ps.executeQuery();

                    while(rs.next()) {
                        cities.add(rs.getString("city"));
                    }
                } catch (Throwable var19) {
                    if (ps != null) {
                        try {
                            ps.close();
                        } catch (Throwable var18) {
                            var19.addSuppressed(var18);
                        }
                    }

                    throw var19;
                }

                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (this.connection != null) {
                    this.connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        this.cityselectbox.setItems(cities);
    }

    private void loadBusinessPage(Business selected) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(YelpApplication.class.getResource("businessDetails.fxml"));
            Parent root = (Parent)fxmlLoader.load();
            BusinessDetailsController controller = (BusinessDetailsController)fxmlLoader.getController();
            ObservableList<Business> businesses = FXCollections.observableArrayList(this.getSimilarBusinesses(selected));
            controller.initData(selected.getName(), businesses);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(this.businessTable.getScene().getWindow());
            dialog.setTitle("Business Details");
            Scene scene = new Scene(root, (double)695.0F, (double)700.0F);
            scene.getStylesheets().add(YelpApplication.class.getResource("styles/styles.css").toExternalForm());
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void updateStates() {
        ObservableList<String> states = FXCollections.observableArrayList();
        String stateQuery = "\n\nSELECT DISTINCT state\nFROM business\nORDER BY state\n\n\n";

        try {
            this.connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try (PreparedStatement ps = this.connection.prepareStatement(stateQuery)) {
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                states.add(rs.getString("state"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        this.stateComboBox.setItems(states);

        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void updateCategories(String state) {
        if (state != null) {
            ObservableList<String> categories = FXCollections.observableArrayList();
            String stateQuery = "SELECT DISTINCT businesscategories.category_name\nFROM businesscategories\nJOIN business ON business.business_id = businesscategories.business_id\nWHERE business.state = ?\nORDER BY businesscategories.category_name\n";

            try {
                this.connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            try (PreparedStatement ps = this.connection.prepareStatement(stateQuery)) {
                ps.setString(1, state);
                ResultSet rs = ps.executeQuery();

                while(rs.next()) {
                    categories.add(rs.getString("category_name"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            this.categoryList.setItems(categories);

            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    private void searchBusinesses() {
        String state = (String)this.stateComboBox.getSelectionModel().getSelectedItem();
        List<String> cats = new ArrayList(this.categoryList.getSelectionModel().getSelectedItems());
        List<Business> results = this.querybusinesses(state, cats);
        this.businessTable.setItems(FXCollections.observableArrayList(results));
    }

    private List<Business> querybusinesses(String state, List<String> categories) {
        List<Business> res = new ArrayList();
        String businessQuery = "SELECT business_id, business_name, street_address, city, latitude, longitude, star_rating, num_tips \nFROM business\nWHERE business.state = ?\n";
        businessQuery = businessQuery.concat("ORDER BY business_name");

        try {
            this.connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try (PreparedStatement ps = this.connection.prepareStatement(businessQuery)) {
            int count = 1;
            ps.setString(count, state);
            ++count;
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                res.add(new Business(rs.getString("business_id"), rs.getString("business_name"), rs.getString("street_address"), rs.getString("city")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    private List<Business> getSimilarBusinesses(Business selected) {
        List<Business> res = new ArrayList();
        String stateQuery = "SELECT DISTINCT businesscategories.category_name\nFROM businesscategories\nJOIN business ON business.business_id = businesscategories.business_id\nWHERE business.state = ?\nORDER BY businesscategories.category_name\n";

        try {
            this.connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try (PreparedStatement ps = this.connection.prepareStatement(stateQuery)) {
            ps.setString(1, selected.getId());
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                res.add(new Business(rs.getString("business_id"), rs.getString("business_name"), rs.getString("street_address"), rs.getString("city")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    static {
        JDBC_URL = dotenv.get("JDBC_URL");
        JDBC_USER = dotenv.get("JDBC_USER");
        JDBC_PASS = dotenv.get("JDBC_PASS");
    }
}

