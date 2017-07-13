//*****************************************************************************************************************************
// PriorityQueue.java - June 22 2017
// Nick Simmons
// Creates a linked list priority queue, priority: float; smaller float value is higher priority
// A new QueueItem will be added right after all other same priority QueueItems.
//*****************************************************************************************************************************

// class to create priority queue
public class PriorityQueue
{
	QueueItem head; // first item to be released from queue
	int noItems;

	// create empty queue
	public PriorityQueue()
	{
		head = null;
		noItems = 0;
	}

	// check if queue is empty
	public boolean isEmpty()
	{
		if (noItems == 0)
			return true;
		else
			return false;
	}

	// add job object to priority queue
	public void putQueue(Object o, float key)
	{
		QueueItem n, p, q;

		n = new QueueItem(o, key);

		if (head == null)
		{
			head = n;
			n.setPrev(null);
			n.setNext(null);
		}

		else
		{
			p = head;
			q = head;
		
			while (p != null)
			{
				if (p.getKey() <= key)
				{
					q = p;
					p = p.getNext();
				}
				
				else
				{
					break;
				}
			}
			
			if (p != null)
			{
				// at the beginning
				if (p.getPrev() == null)
				{  
					head = n;
					n.setPrev(null);
					n.setNext(p);
					p.setPrev(n);
				}
				
				// in the middle
				else
				{  
					(p.getPrev()).setNext(n);
					n.setPrev(p.getPrev());
					n.setNext(p);
					p.setPrev(n);
				}
			}

			// at the end
			else
			{  
				q.setNext(n);
				n.setPrev(q);
				n.setNext(null);
			}
		}

		noItems++;
	}

	// dequeue and return job from queue
	public Object getQueue()
	{
		if (noItems == 0)
		{
			return null;
		}

		else
		{
			QueueItem o;
			o = head;
			head = head.getNext();
			
			if (head != null)
			{
				head.setPrev(null);
			}
			
			noItems--;
			return o.getObj();
		}
	}

	// return key of first queue item
	public float getHighestPriority()
	{
		if (noItems == 0)
		{
			return -1;
		}

		else
		{
			return head.getKey();
		}
	}
}