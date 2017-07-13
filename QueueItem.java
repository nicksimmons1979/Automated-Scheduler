//*****************************************************************************************************************************
// QueueItem.java - June 22 2017
// Nick Simmons
// Node for priority queue
//*****************************************************************************************************************************

// class for queue node
public class QueueItem
{
	float key;	// key for priority queuing
	Object obj; // object node points to
	QueueItem prev;
	QueueItem next;

	// create queue node
	public QueueItem(Object o, float key)
	{
		obj = o;
		this.key = key;
		prev = null;
		next = null;
	}

	// set node key, used for priority
	public void setKey(float key)
	{
		this.key = key;
	}

	// get node key, used for priority
	public float getKey()
	{
		return key;
	}

	// create link to next node
	public void setNext(QueueItem next)
	{
		this.next = next;
	}

	// return next node in queue
	public QueueItem getNext()
	{
		return next;
	}

	// create link to previous node
	public void setPrev(QueueItem prev)
	{
		this.prev = prev;
	}

	// return previous node in queue
	public QueueItem getPrev()
	{
		return prev;
	}

	// return object node points to
	public Object getObj()
	{
		return obj;
	}
}