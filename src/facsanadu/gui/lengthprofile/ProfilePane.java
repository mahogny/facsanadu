package facsanadu.gui.lengthprofile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.trolltech.qt.core.Qt.Orientation;
import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QIntValidator;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSlider;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

import facsanadu.data.Dataset;
import facsanadu.data.ProfChannel;
import facsanadu.gates.Gate;
import facsanadu.gates.GatingResult;
import facsanadu.gates.IntArray;
import facsanadu.gui.MainWindow;
import facsanadu.gui.resource.ImgResource;

/**
 * 
 * Pane showing length profiles
 * 
 * @author Johan Henriksson
 *
 */
public class ProfilePane extends QWidget
	{
	private QPushButton bPrevProf=new QPushButton(new QIcon(ImgResource.moveLeft), "");
	private QPushButton bNextProf=new QPushButton(new QIcon(ImgResource.moveRight), "");
	private QLineEdit tfID=new QLineEdit("0");
	private QCheckBox cbNormalizeLength=new QCheckBox();
	private QCheckBox cbShowAll=new QCheckBox();
	
	private ProfileView view;
	private MainWindow mw;
	
	private QGridLayout laychans=new QGridLayout();
	private LinkedList<QCheckBox> cbShowChannel=new LinkedList<QCheckBox>();
	private LinkedList<QSlider> sScaleChannel=new LinkedList<QSlider>();
	
	public ProfilePane(MainWindow mw)
		{
		this.mw=mw;
		view=new ProfileView(mw);
		
		tfID.setValidator(new QIntValidator(this));
		tfID.setMaximumWidth(100);

		
		for(int i=0;i<10;i++)
			{
			QCheckBox cb=new QCheckBox();
			cb.stateChanged.connect(this,"updateViews()");
			cb.setChecked(true);
			cbShowChannel.add(cb);
			laychans.addWidget(cb,i,0);
			
			QSlider s=new QSlider();
			s.setOrientation(Orientation.Horizontal);
			s.setMaximum(10000);
			s.setValue(2000);
			laychans.addWidget(s, i, 1);
			sScaleChannel.add(s);
			s.sliderMoved.connect(this,"updateViews()");
			}

		QHBoxLayout blay=new QHBoxLayout();
		blay.addWidget(new QLabel(tr("Show all")));
		blay.addWidget(cbShowAll);
		blay.addWidget(new QLabel(tr("Normalize length")));
		blay.addWidget(cbNormalizeLength);
		blay.addStretch();
		blay.addWidget(new QLabel(tr("Event ID:")));
		blay.addWidget(tfID);
		blay.addWidget(bPrevProf);
		blay.addWidget(bNextProf);
		
		QVBoxLayout lay=new QVBoxLayout();
		lay.addLayout(blay);
		lay.addWidget(view);
		lay.addLayout(laychans);
		
		tfID.editingFinished.connect(this,"updateViews()");
		bNextProf.clicked.connect(this,"actionNextProf()");
		bPrevProf.clicked.connect(this,"actionPrevProf()");
		cbNormalizeLength.stateChanged.connect(this,"cbNormalizeLength()");
		cbShowAll.stateChanged.connect(this,"updateViews()");
		
		setLayout(lay);
		}

	public void cbNormalizeLength()
		{
		if(view.curchannel!=null)
			view.curchannel.forNormalized=cbNormalizeLength.isChecked();
		updateViews();
		}
	
	
	public Gate getCurrentGate()
		{
		List<Gate> gates=mw.getSelectedGates();
		Gate g;
		if(gates.isEmpty())
			g=mw.project.gateset.getRootGate();
		else
			g=gates.get(0);
		return g;
		}
	
	public void actionPrevProf()
		{
		int id=getCurrentID();
		Dataset ds=getCurrentDataset();
		if(ds!=null)
			{
			GatingResult gres=mw.project.getGatingResult(ds);
			IntArray arr=gres.getAcceptedFromGate(getCurrentGate());
			for(int i=arr.size()-1;i>=0;i--)
				if(arr.get(i)<id)
					{
					setEventID(arr.get(i));
					break;
					}
			}
		}
	public void actionNextProf()
		{
		int id=getCurrentID();
		Dataset ds=getCurrentDataset();
		if(ds!=null)
			{
			GatingResult gres=mw.project.getGatingResult(ds);
			IntArray arr=gres.getAcceptedFromGate(getCurrentGate());
			for(int i=0;i<arr.size();i++)
				if(arr.get(i)>id)
					{
					setEventID(arr.get(i));
					break;
					}
			}
		}

	public List<Integer> getAllFromGate()
		{
		Dataset ds=getCurrentDataset();
		if(ds!=null)
			{
			GatingResult gres=mw.project.getGatingResult(ds);
			IntArray arr=gres.getAcceptedFromGate(getCurrentGate());
			ArrayList<Integer> list=new ArrayList<Integer>(arr.size());
			for(int i=0;i<arr.size();i++)
				list.add(arr.get(i));
			return list;
			}
		else
			return new ArrayList<Integer>();
		}
	
	public void setEventID(int id)
		{
		tfID.setText(""+id);
		updateViews();
		}
	
	public Dataset getCurrentDataset()
		{
		List<Dataset> listds=mw.getSelectedDatasets();
		if(!listds.isEmpty())
			return listds.get(0);
		else
			return null;
		}

	public int getCurrentID()
		{
		int id=-1;
		try
			{
			id=Integer.parseInt(tfID.text());
			}
		catch (NumberFormatException e)
			{
			e.printStackTrace();
			}
		return id;
		}
	
	public void updateViews()
		{
		Dataset ds=getCurrentDataset();
		if(ds!=null)
			{
			ArrayList<Boolean> showchan=new ArrayList<Boolean>();
			ArrayList<Double> scale=new ArrayList<Double>();
			for(int i=0;i<ds.getNumLengthProfiles();i++)
				{
				showchan.add(cbShowChannel.get(i).isChecked());
				double pos=sScaleChannel.get(i).value()/1000.0;
				scale.add(pos/400000.0);
				}
			view.showchan=showchan;
			view.scale=scale;
			view.normalizeLength=cbNormalizeLength.isChecked();
			if(cbShowAll.isChecked())
				{
				view.setevent(ds, getAllFromGate());
				}
			else
				{
				int id=getCurrentID();
				view.setevent(ds, id);
				}
			}
		else
			{
			view.setevent(null, 0);
			ds=new Dataset();
			}
		
		//Update name and visibility of checkboxes for channels
		for(int i=0;i<cbShowChannel.size();i++)
			{
			QCheckBox cb=cbShowChannel.get(i);
			QSlider s=sScaleChannel.get(i);
			boolean visible=i<ds.lengthprofsInfo.size();
			cb.setVisible(visible);
			s.setVisible(visible);
			if(visible)
				cb.setText(ds.lengthprofsInfo.get(i).name);
			}
			

		}
	
	
	public ArrayList<Integer> getSelChans()
		{
		Dataset ds=getCurrentDataset();
		ArrayList<Integer> showchan=new ArrayList<Integer>();
		if(ds!=null)
			for(int i=0;i<ds.getNumLengthProfiles();i++)
				{
				if(cbShowChannel.get(i).isChecked())
					showchan.add(i);
				}
		return showchan;
		}
	
	
	public void setCurChan(ProfChannel pc)
		{
		view.curchannel=pc;
		view.repaint();
		}
	}
