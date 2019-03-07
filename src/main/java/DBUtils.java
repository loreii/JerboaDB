import javafx.scene.Node;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DBUtils {


    public static Database load(Database db) {

        try (Connection connection = DriverManager.getConnection(db.getConnectionUrl());
             Statement statement = connection.createStatement()) {

            DatabaseMetaData databaseMetaData = connection.getMetaData();

            ResultSet resultSet = statement.executeQuery("use " + db.name + "; SELECT TABLE_NAME FROM information_schema.tables;");

            // Print results from select statement
            while (resultSet.next()) {
                //Print
                String tableName = resultSet.getString("TABLE_NAME");

                Table table = new Table(tableName);
                Statement instatement = connection.createStatement();
                //ResultSet columns = databaseMetaData.getColumns(null, null, tableName, null);
                ResultSet columns = instatement.executeQuery("use " + db.name + "; SELECT * FROM information_schema.columns where TABLE_NAME = '"+tableName+"';");
                while (columns.next()) {
                    Column column = new Column(columns);
                    table.addColumn(column);
                }
                db.addTable(table);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return db;
    }

    public static List<double[]> fetch(Database db, Collection<Column> columns) {
        String selectSql = getQuery(columns);
        System.out.println("Query >>\t" + selectSql);
        return fetch(db, columns, selectSql);
    }

    public static List<double[]> fetch(Database db, Collection<Column> columns, String selectSql) {

        List<double[]> results = new ArrayList<>(600_000);

        try (Connection connection = DriverManager.getConnection(db.getConnectionUrl());
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(selectSql);

            // Print results from select statement
            while (resultSet.next()) {

                double[] v = new double[columns.size()];
                for ( int i = 0; i < columns.size(); ++i ) {
                    v[i] = resultSet.getDouble(i + 1);
                }
                results.add(v);

            }
            System.out.println("Query   >>\t" + selectSql);
            System.out.println("Results >>\t" + results.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static String getQuery(Collection<Column> columns) {
        if (columns.size() > 0) {
            String tables = columns.stream().map(c -> c.columnName).collect(Collectors.joining(","));
            return "SELECT  " + tables + " FROM " + ((Table) columns.iterator().next().getParent()).name;
        }
        return "";
    }

}


class Column extends CheckBoxTreeItem<String> {
    String columnName;
    String datatype;
    String columnsize;
    String decimaldigits;
    String isNullable;
    String is_autoIncrment;

    public Column(ResultSet columns) throws SQLException {

        columnName = columns.getString("COLUMN_NAME");

        datatype = columns.getString("DATA_TYPE");
        //SQLSERVER dependency
        columnsize = columns.getString("CHARACTER_MAXIMUM_LENGTH");
        if(columnsize==null) {
            columnsize = columns.getString("NUMERIC_PRECISION");
        }

        //columnsize = columns.getString("COLUMN_SIZE");
        //is_autoIncrment = columns.getString("IS_AUTOINCREMENT");
        //decimaldigits = columns.getString("DECIMAL_DIGITS");

        isNullable = columns.getString("IS_NULLABLE");

        setValue(columnName+"  ("+datatype+":"+columnsize+")");
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Column{");
        sb.append("columnName='").append(columnName).append('\'');
        sb.append(", datatype='").append(datatype).append('\'');
        sb.append(", columnsize='").append(columnsize).append('\'');
        sb.append(", decimaldigits='").append(decimaldigits).append('\'');
        sb.append(", isNullable='").append(isNullable).append('\'');
        sb.append(", is_autoIncrment='").append(is_autoIncrment).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

class Table extends CheckBoxTreeItem<String> {
    private final Node rootIcon = new ImageView(
            new Image(getClass().getResourceAsStream("table.png"))
    );

    String name;
    List<Column> columns = new ArrayList<>();

    public Table(String name) {
        super(name);
        setGraphic(rootIcon);
        setExpanded(false);
        this.name = name;
    }

    public void addColumn(Column c) {
        columns.add(c);
        getChildren().add(c);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Table{");
        sb.append("name='").append(name).append('\'');
        sb.append(", columns=").append(columns);
        sb.append('}');
        return sb.toString();
    }
}

class Database extends CheckBoxTreeItem<String> {

    private final Node rootIcon = new ImageView(
            new Image(getClass().getResourceAsStream("db.png"))
    );

    String username;
    String password;
    String server;
    String name;
    List<Table> tables = new ArrayList<>();

    public Database(String server, String name, String username, String password) {
        super(server + ":" + name);
        setGraphic(rootIcon);
        setExpanded(true);
        this.username = username;
        this.password = password;
        this.server = server;
        this.name = name;
    }

    public String getConnectionUrl() {
        return "jdbc:sqlserver://" + server + ":1433;"
                + "DatabaseName=" + name + ";"
                + "user=" + username + ";"
                + "password=" + password + ";"
                + "encrypt=true;"
                + "trustServerCertificate=true;"
                + "loginTimeout=30;";
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Database{");
        sb.append("username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", server='").append(server).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", tables=").append(tables);
        sb.append('}');
        return sb.toString();
    }

    public void addTable(Table t) {
        tables.add(t);
        getChildren().add(t);
    }
}
