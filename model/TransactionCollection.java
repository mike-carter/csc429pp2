// Package Specification
package model;

// System imports
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
public class TransactionCollection extends EntityBase
{
	private static final String myTableName = "Transaction";

	private Vector<Transaction> transactions;

	/**
	 * TransactionCollection class constructor
	 */
	public TransactionCollection()
	{
		super(myTableName);

		transactions = new Vector<Transaction>();
	}

	/** */
	//-----------------------------------------------------------
	public void findMatchingTransactions(String bookId, String patronId, String dateOfTrans)
		throws InvalidPrimaryKeyException
	{
		String query = "SELECT * FROM "+myTableName+" WHERE (";
		if (bookId != null)
		{
			query += "bookId = "+bookId+" ";
		}
		if (patronId != null)
		{
			if (bookId != null)
				query += "AND ";
			query += "patronId = "+patronId+" ";
		}
		if (dateOfTrans != null)
		{
			if ((bookId != null) || (patronId != null))
			{
				query += "AND ";
			}
			query += "dateOfTrans = '"+dateOfTrans+"' ";
		}
		query += ")";

		// DEBUG System.out.println("DEBUG::query = '"+query+"'");

		Vector allDataRetrieved = getSelectQueryResult(query);

		if (allDataRetrieved != null)
		{
			//DEBUG System.out.println("DEBUG::allDataRetrieved.size() = "+allDataRetrieved.size());
					
			transactions = new Vector<Transaction>();

			for(int cnt = 0; cnt < allDataRetrieved.size(); cnt++)
			{
				Properties nextTransData = (Properties)allDataRetrieved.elementAt(cnt);

				Transaction trans = new Transaction(nextTransData);

				if(trans != null)
				{
					addTransaction(trans);
				}

				//DEBUG System.out.println("DEBUG::transactions.size() = "+transactions.size());
			}
		}
		else
		{
			throw new InvalidPrimaryKeyException("No transactions found");
		}
	}

	/** */
	//-----------------------------------------------------------
	private int findIndexToAdd(Transaction t)
	{
		int low = 0;
		int high = transactions.size()-1;
		int middle;

		while (low <= high)
		{
			middle = (low+high)/2;

			Transaction midSession = transactions.elementAt(middle);

			int result = Transaction.compare(t, midSession);

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
		if (key.equals("Transactions"))
			return transactions;
		else if (key.equals("TransactionList"))
			return this;
		return null;
	}

	/** */
	public Transaction retrieve(String transId)
	{
		Transaction retValue = null;
		for (int cnt = 0; cnt < transactions.size(); cnt++)
		{
			Transaction nextTransaction = transactions.elementAt(cnt);
			String nextTransNum = (String)nextTransaction.getState("transId");
			if (nextTransNum.equals(transId) == true)
			{
				retValue = nextTransaction;
				break;
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
	private void addTransaction(Transaction t)
	{
		//transactions.add(a);
		int index = findIndexToAdd(t);
		transactions.insertElementAt(t, index); // To build up a collection sorted on some key
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
