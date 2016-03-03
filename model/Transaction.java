// Package Specification
package model;

// System imports
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

// Project imports
import exception.InvalidPrimaryKeyException;
import database.*;


/**
 * @author Caleb Butcher, Michael Carter
 * @since 2016-02-02
 */
//=========================================================
public class Transaction extends EntityBase
{
	private static final String myTableName = "Transaction";

	protected Properties dependencies;

	private String updateStatusMessage = "";

	/**
	 * Transaction class constructor: Primary key instantiation
	 */
	//-----------------------------------------------------------
	@SuppressWarnings("unchecked")
	public Transaction(String transId) throws InvalidPrimaryKeyException
	{
		super(myTableName);

		setDependencies();

		String query = String.format("SELECT * FROM %s WHERE (transId = '%s')", myTableName, transId);

		Vector<Properties> allDataRetrieved = getSelectQueryResult(query);

		//Must get at least one transId
		if (allDataRetrieved != null)
		{
			int size = allDataRetrieved.size();

			//There should be exactly one trans. Any more will be an error
			if (size != 1)
			{
				throw new InvalidPrimaryKeyException(String.format("Multiple Transactions matching ID: %s found", transId));
			}
			else
			{
				//Copy all retrived data into persistent state
				Properties retrivedTransData = allDataRetrieved.elementAt(0);
				persistentState = new Properties();

				Enumeration allKeys = retrivedTransData.propertyNames();
				while (allKeys.hasMoreElements())
				{
					String nextKey = (String)allKeys.nextElement();
					String nextValue = retrivedTransData.getProperty(nextKey);

					if (nextValue != null)
					{
						persistentState.setProperty(nextKey, nextValue);
					}
				}
			}
		}
		else //If no trans was found at this ID throw an exception
		{
			throw new InvalidPrimaryKeyException(String.format("No Transaction matching ID: %s found", transId));
		}
	}

	/**
	 * Transaction class constructor: Create new instance
	 */
	//-----------------------------------------------------------
	public Transaction(Properties props)
	{
		super(myTableName);

		setDependencies();

		persistentState = new Properties();

		Enumeration allKeys = props.propertyNames();
		while (allKeys.hasMoreElements())
		{
			String nextKey = (String)allKeys.nextElement();
			String nextValue = props.getProperty(nextKey);

			if (nextValue != null)
			{
				persistentState.setProperty(nextKey, nextValue);
			}
		}
	}

	//-----------------------------------------------------------------------------------
	public static int compare(Transaction a, Transaction b)
	{
		String aNum = (String)a.getState("title");
		String bNum = (String)b.getState("title");

		return aNum.compareTo(bNum);
	}

	/** */
	//-----------------------------------------------------------
	private void setDependencies()
	{
		dependencies = new Properties();

		myRegistry.setDependencies(dependencies);
	}

	/** */
	//-----------------------------------------------------------
	public void update()
	{
		updateStateInDatabase();
	}
	
	/** */
	//-----------------------------------------------------------
	private void updateStateInDatabase()
	{
		try
		{
			if(persistentState.getProperty("transId") != null)
			{
				Properties whereClause = new Properties();

				whereClause.setProperty("transId", persistentState.getProperty("transId"));

				updatePersistentState(mySchema, persistentState, whereClause);

				updateStatusMessage = String.format("Data for Transaction ID: %s updated successfully in database!", persistentState.getProperty("transId"));
			}
			else
			{
				Integer transId = insertAutoIncrementalPersistentState(mySchema, persistentState);
				persistentState.setProperty("transId", transId.toString());
				updateStatusMessage = String.format("Data for new Transaction: %s added to database successfullt!", persistentState.getProperty("transId"));
			}
		}
		catch (SQLException ex)
		{
			updateStatusMessage = "Error adding Transaction data to database!";
		}
	}
	
	/** */
	//-----------------------------------------------------------
	public void stateChangeRequest(String key, Object value)
	{
		myRegistry.updateSubscribers(key, this);
	}
	
	/**
	 * Gets the value of a specified property
	 */
	//----------------------------------------------------------
	public Object getState(String key)
	{
		if (key.equals("UpdateStatusMessage") == true)
			return updateStatusMessage;

		return persistentState.getProperty(key);
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
}
