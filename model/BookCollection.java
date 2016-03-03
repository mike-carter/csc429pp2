// Package Specification
package model;

// System imports
import java.util.Properties;
import java.util.Vector;
import java.util.Hashtable;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Project imports
import exception.InvalidPrimaryKeyException;
import event.Event;
import database.*;
import impresario.IView;
import userinterface.MainStageContainer;
import userinterface.View;
import userinterface.ViewFactory;
import userinterface.WindowPosition;

/**
 * @author Caleb Butcher, Michael Carter
 * @since 2016-02-02
 */
//=========================================================
public class BookCollection extends EntityBase implements IView
{
	private static final String myTableName = "Book";

	private Vector<Book> books;

	protected Librarian myLibrarian;

	/**
	 * BookCollection class constructor
	 */
	public BookCollection()
	{
		super(myTableName);
		books = new Vector<Book>();
	}

	public BookCollection(Librarian lib)
	{
		super(myTableName);
		books = new Vector<Book>();

		myStage = MainStageContainer.getInstance();
		myViews = new Hashtable<String, Scene>();
		myLibrarian = lib;
	}

	/** */
	//-----------------------------------------------------------
	public void findBooksOlderThanDate(String year) throws InvalidPrimaryKeyException
	{
		String query = String.format("SELECT * FROM %s WHERE pubYear > '%s'", myTableName, year);
		
		Vector allDataRetrieved = getSelectQueryResult(query);

		if (allDataRetrieved != null)
		{
			books = new Vector<Book>();

			for(int cnt = 0; cnt < allDataRetrieved.size(); cnt++)
			{
				Properties nextBookData = (Properties)allDataRetrieved.elementAt(cnt);

				Book book = new Book(nextBookData);

				if(book != null)
				{
					addBook(book);
				}
			}
		}
		else
		{
			throw new InvalidPrimaryKeyException("No books older than: " + year + " found");
		}
	}

	/** */
	//-----------------------------------------------------------
	public void findBooksNewerThanDate(String year) throws InvalidPrimaryKeyException
	{
		String query = String.format("SELECT * FROM %s WHERE pubYear < '%s'", myTableName, year);
		
		Vector allDataRetrieved = getSelectQueryResult(query);

		if (allDataRetrieved != null)
		{
			books = new Vector<Book>();

			for(int cnt = 0; cnt < allDataRetrieved.size(); cnt++)
			{
				Properties nextBookData = (Properties)allDataRetrieved.elementAt(cnt);

				Book book = new Book(nextBookData);

				if(book != null)
				{
					addBook(book);
				}
			}
		}
		else
		{
			throw new InvalidPrimaryKeyException("No books newer than: " + year + " found");
		}
		
	}

	/** */
	//-----------------------------------------------------------
	public void findBooksWithTitleLike(String title) throws InvalidPrimaryKeyException
	{
		String query = "SELECT * FROM "+myTableName+" WHERE title LIKE '%"+title+"%'";
		
		Vector allDataRetrieved = getSelectQueryResult(query);

		if (allDataRetrieved != null)
		{
			books = new Vector<Book>();

			for(int cnt = 0; cnt < allDataRetrieved.size(); cnt++)
			{
				Properties nextBookData = (Properties)allDataRetrieved.elementAt(cnt);

				Book book = new Book(nextBookData);

				if(book != null)
				{
					addBook(book);
				}
			}
		}
		else
		{
			throw new InvalidPrimaryKeyException("No books with similar title to: " + title + " found");
		}
	}
	
	/** */
	//-----------------------------------------------------------
	public void findBooksWithAuthorLike(String author) throws InvalidPrimaryKeyException
	{
		String query = "SELECT * FROM "+myTableName+" WHERE author LIKE '%"+author+"%'";
		Vector allDataRetrieved = getSelectQueryResult(query);

		if (allDataRetrieved != null)
		{
			books = new Vector<Book>();

			for(int cnt = 0; cnt < allDataRetrieved.size(); cnt++)
			{
				Properties nextBookData = (Properties)allDataRetrieved.elementAt(cnt);

				Book book = new Book(nextBookData);

				if(book != null)
				{
					addBook(book);
				}
			}
		}
		else
		{
			throw new InvalidPrimaryKeyException("No books with Author: " + author + " found");
		}
	}

	/** */
	//-----------------------------------------------------------
	private int findIndexToAdd(Book a)
	{
		//users.add(u);
		int low = 0;
		int high = books.size()-1;
		int middle;

		while (low <= high)
		{
			middle = (low+high)/2;

			Book midSession = books.elementAt(middle);

			int result = Book.compare(a,midSession);

			if (result == 0)
			{
				return middle;
			}
			else if (result < 0)
			{
				high = middle-1;
			}
			else
			{
				low = middle+1;
			}
		}
		return low;
	}

	/** */
	//-----------------------------------------------------------
	public Object getState(String key)
	{
		if (key.equals("Books"))
			return books;
		else
		if (key.equals("BookList"))
			return this;
		return null;
	}

	/** */
	//-----------------------------------------------------------
	public Book retrieve(String bookId)
	{
		Book retValue = null;
		for (int cnt = 0; cnt < books.size(); cnt++)
		{
			Book nextBook = books.elementAt(cnt);
			String nextBookNum = (String)nextBook.getState("BookId");
			if (nextBookNum.equals(bookId) == true)
			{
				retValue = nextBook;
				return retValue; // we should say 'break;' here
			}
		}

		return retValue;
	}

	/** */
	//-----------------------------------------------------------
	public void updateState(String key, Object value)
	{
		stateChangeRequest(key, value);
	}

	/** */
	//-----------------------------------------------------------
	public void stateChangeRequest(String key, Object value)
	{
		myRegistry.updateSubscribers(key, this);
	}
	
	/** */
	//-----------------------------------------------------------
	private void addBook(Book a)
	{
		//books.add(a);
		int index = findIndexToAdd(a);
		books.insertElementAt(a,index); // To build up a collection sorted on some key
	}

	/** */
	//-----------------------------------------------------------
	protected void initializeSchema(String tableName)
	{
		if (mySchema == null)
		{
			mySchema = getSchemaInfo(tableName);
		}
	}

	public void createAndShowView()
	{
		Scene localScene = myViews.get("BookCollectionView");

		if (localScene == null)
		{
			View newView = ViewFactory.createView("BookCollectionView", this);
			localScene = new Scene(newView);
			myViews.put("BookCollectionView", localScene);
		}

		myStage.setScene(localScene);
		myStage.sizeToScene();

		WindowPosition.placeCenter(myStage);
	}

	public void done()
	{
		myLibrarian.createAndShowSearchBooksView();
	}
}
