package userinterface;

// system imports
import java.util.Vector;
import java.util.Enumeration;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

// project imports
import impresario.IModel;
import model.Book;
import model.BookCollection; 

//===============================================================
public class BookCollectionView extends View
{
	protected TableView<BookTableModel> tableOfBooks;
	protected Button doneButton;

	public BookCollectionView(IModel model)
	{
		super(model, "BookCollectionView");

		VBox container = new VBox(10);
		container.setPadding(new Insets(15, 5, 5, 5));
		container.setPrefWidth(600);

		// create our GUI components, add them to this panel
		container.getChildren().add(createTitle());
		container.getChildren().add(createFormContent());

		getChildren().add(container);
		
		populateFields();
	}
	
	protected void populateFields()
	{
		getEntryTableModelValues();
	}

	protected void getEntryTableModelValues()
	{
		ObservableList<BookTableModel> tableData = FXCollections.observableArrayList();
		try
		{
			Vector entryList = (Vector)myModel.getState("Books");
			Enumeration entries = entryList.elements();

			while (entries.hasMoreElements())
			{
				Book nextBook = (Book)entries.nextElement();
				Vector<String> view = nextBook.getEntryListView();

				BookTableModel nextTableRowData = new BookTableModel(view);
				tableData.add(nextTableRowData);
				
			}

			tableOfBooks.setItems(tableData);
		}
		catch (Exception e)
		{
			// Do nothing for now
		}
	}

	private Node createTitle()
	{
		Text titleText = new Text("                            "+
								  "LIBRARY SYSTEM"+
								  "                            ");
		titleText.setFont(Font.font("Garamond", FontWeight.BOLD, 20));
		titleText.setTextAlignment(TextAlignment.CENTER);
		titleText.setFill(Color.BLACK);
		
		return titleText;
	}

	// Create the main form contents
	//-------------------------------------------------------------
	private VBox createFormContent()
	{
		VBox vbox = new VBox(10);
		
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text prompt = new Text("SEARCH RESULTS");
		prompt.setWrappingWidth(350);
		prompt.setTextAlignment(TextAlignment.CENTER);
        prompt.setFill(Color.BLACK);
        grid.add(prompt, 0, 0, 2, 1);

		tableOfBooks = new TableView<BookTableModel>();
		tableOfBooks.setEditable(false);
		tableOfBooks.setPrefWidth(600);

		TableColumn bookIdColumn = new TableColumn("Book Id");
		bookIdColumn.setMinWidth(100);
		bookIdColumn.setCellValueFactory(
	                new PropertyValueFactory<BookTableModel, String>("bookId"));

		TableColumn authorColumn = new TableColumn("Author");
		authorColumn.setMinWidth(100);
		authorColumn.setCellValueFactory(
	                new PropertyValueFactory<BookTableModel, String>("author"));

		TableColumn titleColumn = new TableColumn("Title");
		titleColumn.setMinWidth(100);
		titleColumn.setCellValueFactory(
	                new PropertyValueFactory<BookTableModel, String>("title"));

		TableColumn pubYearColumn = new TableColumn("Publication Year");
		pubYearColumn.setMinWidth(100);
		pubYearColumn.setCellValueFactory(
	                new PropertyValueFactory<BookTableModel, String>("pubYear"));

		TableColumn statusColumn = new TableColumn("Status");
		statusColumn.setMinWidth(100);
		statusColumn.setCellValueFactory(
	                new PropertyValueFactory<BookTableModel, String>("bookStatus"));
		
		tableOfBooks.getColumns().addAll(bookIdColumn,
										 authorColumn,
										 titleColumn,
										 pubYearColumn,
										 statusColumn);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setPrefSize(115, 150);
		scrollPane.setContent(tableOfBooks);

		
		doneButton = new Button("Done");
 		doneButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					((BookCollection)myModel).done();
				}
			});
		HBox btnContainer = new HBox(100);
		btnContainer.setAlignment(Pos.CENTER);
		btnContainer.getChildren().add(doneButton);

		vbox.getChildren().add(grid);
		vbox.getChildren().add(scrollPane);
		vbox.getChildren().add(btnContainer);

		return vbox;
	}

	public void updateState(String key, Object value)
	{
		
	}
}
