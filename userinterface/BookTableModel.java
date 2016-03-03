package userinterface;

import java.util.Vector;
import javafx.beans.property.SimpleStringProperty;

//===============================================================
public class BookTableModel
{
	private final SimpleStringProperty bookId;
	private final SimpleStringProperty author;
	private final SimpleStringProperty title;
	private final SimpleStringProperty pubYear;
	private final SimpleStringProperty bookStatus;

	//-----------------------------------------------------------
	public BookTableModel(Vector<String> bookData)
	{
		bookId = new SimpleStringProperty(bookData.elementAt(0));
		author = new SimpleStringProperty(bookData.elementAt(1));
		title = new SimpleStringProperty(bookData.elementAt(2));
		pubYear = new SimpleStringProperty(bookData.elementAt(3));
		bookStatus = new SimpleStringProperty(bookData.elementAt(4));
	}

	//-----------------------------------------------------------
	public String getBookId()
	{
		return bookId.get();
	}
	
	//-----------------------------------------------------------
	public String getAuthor()
	{
		return author.get();
	}

	//-----------------------------------------------------------
	public String getTitle()
	{
		return title.get();
	}

	//-----------------------------------------------------------
	public String getPubYear()
	{
		return pubYear.get();
	}

	//-----------------------------------------------------------
	public String getBookStatus()
	{
		return bookStatus.get();
	}
}
