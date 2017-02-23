package facsanadu.gui;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import facsanadu.data.Dataset;
import facsanadu.gates.Gate;
import facsanadu.gates.GatingResult;
import facsanadu.gates.measure.GateMeasure;

/**
 * 
 * Threaded system for calculating gates in the background
 * 
 * @author Johan Henriksson
 *
 */
public abstract class GateCalcThread
	{
	private int numcores=0;
	private HashSet<Task> currentTasks=new HashSet<Task>();
	private LinkedList<Worker> threads=new LinkedList<Worker>();
	private Object lockGetGate=new Object();

	/**
	 * Start the calculation if it happen to not be running yet
	 */
	public void wakeup()
		{
		synchronized (lockGetGate)
			{
			lockGetGate.notifyAll();
			}
		}
	
	/**
	 * Get the project to work on
	 */
	public abstract	FacsanaduProject getProject();
	

	/**
	 * Function to call once work done
	 */
	public abstract void callbackDoneCalc(Dataset dataset, Gate g);
	
	/**
	 * Function to check which datasets are selected. Only need to work on these
	 */
	public abstract Collection<Dataset> getCurrentDatasets();

	/**
	 * Constructor
	 */
	public GateCalcThread()
		{
		setNumCores(4);
		}

	/**
	 * Get the number of cores
	 */
	public int getNumCores()
		{
		return numcores;
		}

	/**
	 * Set the number of cores and ensure they are running
	 */
	public void setNumCores(int th)
		{
		numcores=th;
		synchronized (threads)
			{
			//Start more threads if needed. Old ones will die automatically if value reduced
			for(int i=threads.size();i<numcores;i++)
				{
				Worker w=new Worker(threads.size());
				w.start();
				threads.add(w);
				}
			}
		}

	
	/**
	 * One worker thread
	 */
	public class Worker extends Thread
		{
		private int id;
		public Worker(int id)
			{
			this.id=id;
			}
		public void run()
			{
			while(id<=numcores)
				{
				//and other criteria
				FacsanaduProject proj=getProject();

				//Get a task that needs doing
				Task task=null;
				synchronized (lockGetGate)
					{
					for(Dataset ds:getCurrentDatasets())
					//for(Dataset ds:proj.datasets)
						{
						//Need to lock datasets too! synchronized class?
						//GatingResult gr=proj.gatingResult.get(ds);
						task=getTaskToWorkOn(ds, proj.gateset.getRootGate());
						if(task!=null)
							break;
						}
					//Wait until the thread is needed again if there is nothing to do
					if(task==null)
						try
							{
							lockGetGate.wait();
							}
						catch (InterruptedException e)
							{
							}
					else
						currentTasks.add(task);
					}
				
				//Run task
				if(task!=null)
					{
					task.exec();
					currentTasks.remove(task);
					//Because there might be more than one child, but only one thread currently running, ensure to wake up all threads
					wakeup();
					}
				}
			synchronized (threads)
				{
				threads.remove(this);
				}
			}
		}

	
	/**
	 * Get a task that needs working on in the given dataset
	 */
	public Task getTaskToWorkOn(Dataset ds, Gate g)
		{
		FacsanaduProject proj=getProject();
	
		//TODO only update currently visible dataset!!!!
		//
		
		
		//First checking if there is any work in terms of gating
		GatingResult gr=proj.getCreateGatingResult(ds);
		if(gr.gateNeedsUpdate(g))
			{
			TaskGate task=new TaskGate();
			task.g=g;
			task.ds=ds;
			//Check if already processed. If so, also cannot process children here yet,
			//nor measures, so just give up on this node
			if(currentTasks.contains(task))
				return null;
			else
				{
				/*
				System.out.println("c tasks "+currentTasks);
				System.out.println("scheduling "+task.g+ "   "+task.ds);
				System.out.println();
				*/
				return task;
				}
			}
	
		//After gates, check if there are any measures to be done
		for(GateMeasure calc:g.getMeasures())
			{
			TaskMeasure task=new TaskMeasure();
			task.calc=calc;
			task.ds=ds;
			if(!currentTasks.contains(task))
				return task;
			}
		
		
		for(Gate child:g.children)
			{
			Task ret=getTaskToWorkOn(ds, child);
			if(ret!=null)
				return ret;
			}
		return null;		
		}
	
	


	/**
	 * One task needing execution
	 */
	private interface Task
		{
		public void exec();
		}
	
	
	/**
	 * Task for computing one gate
	 */
	private class TaskGate implements Task
		{
		Gate g;
		Dataset ds;
		public boolean equals(Object obj)
			{
			if(obj instanceof TaskGate)
				{
				TaskGate t=(TaskGate)obj;
				return g==t.g && ds==t.ds;
				}
			else
				return false;
			}
		public int hashCode()
			{
			return g.hashCode()+ds.hashCode();
			}
		
		public void exec()
			{
			GatingResult gr=getProject().getCreateGatingResult(ds);
			gr.doOneGate(g, ds, true); //TODO approximate?
			callbackDoneCalc(ds, g);
			}
		
		@Override
		public String toString()
			{
			return "(calcgate "+g+"  "+ds+")";
			}
		}
		

	/**
	 * Task for computing one measure
	 */
	private class TaskMeasure implements Task
		{
		GateMeasure calc;
		Dataset ds;

		public boolean equals(Object obj)
			{
			if(obj instanceof TaskMeasure)
				{
				TaskMeasure t=(TaskMeasure)obj;
				return calc==t.calc && ds==t.ds;
				}
			else
				return false;
			}
		public int hashCode()
			{
			return calc.hashCode()+ds.hashCode();
			}
		
		public void exec()
			{
			// TODO Auto-generated method stub
			}

		}
	
	
	/**
	 * 
	 * previews: best to have a separate flag in the gatingresult.
	 * 
	 * then in all functions above, a flag to request preview results. if all previews done, proceed with the full thing
	 * 
	 * in addition, there should be some functions to get high priority datasets... and views?
	 * 
	 * ORDER:
	 * preview prio datasets
	 * full prio datasets
	 * full all datasets      <--- note: no need to compute previews unless they are actually being displayed!!
	 * 
	 * 
	 * TODO: also have a callback whenever some data has been computed
	 * 
	 * also a function to check if all work has been done or not.
	 * it is done if there are no current tasks and one cannot get any more tasks. or just checking if any tasks are running
	 * 
	 */
	
	public boolean isCalculationRunning()
		{
		synchronized (currentTasks)
			{
			return !currentTasks.isEmpty();
			}
		}
	
	}
