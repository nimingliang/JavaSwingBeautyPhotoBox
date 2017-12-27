package com.niml.photo;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.io.*;
public class MainFrame extends JFrame implements ActionListener,ItemListener
{	
	private String uname=null;//��ǰ�û�������	
	private String perNameBefor=null;//�༭��ʱ��֮ǰ������		
	private String perGroupBefor=null;//�༭��ʱ���޸�֮ǰ�ķ���			
	private boolean searchByName=true;//true��Ĭ��Ϊ����������	
	private boolean isInsert=false;//�Ƿ�Ϊ���Ĭ��Ϊ��	
	Image image=Toolkit.getDefaultToolkit().getImage("img/txl.jpg");//�õ�ͼ�����
	Icon icon = new ImageIcon(image);	
	private JPanel jps=new JPanel();//�����ϰ벿�ֵ�JPanel����		
	private JButton jba=new JButton("���");
	private JButton jbs=new JButton("����");
	private JTextField jtfs=new JTextField();//��������Ϣ������ϵ����Ϣ
	//ѡ����ҷ�ʽ�ĵ�ѡ��ť
	private JRadioButton jrbxm=new JRadioButton("����������",true);
	private JRadioButton jrbbh=new JRadioButton("����Ų���");
	private ButtonGroup bg=new ButtonGroup();//��ѡ��ť��		
	private JPanel jpbr=new JPanel();//��ѡ��ť���		
	//�������µ��� ������ģ�� ָ���ڵ�"��ϵ��"Ϊ���ڵ�
	DefaultMutableTreeNode root=
						new DefaultMutableTreeNode(new NodeValue("��ϵ��",0));
	DefaultTreeModel dtm=new DefaultTreeModel(root);
	private JTree jtz=new JTree();//�����°벿����ߵ�JTree  		
	private JScrollPane jspz=new JScrollPane(jtz);//JTree�Ĺ�����	
	private DefaultTreeCellRenderer dtcr=new DefaultTreeCellRenderer();//���ڵ�Ļ�����		
	private JPanel jpy=new JPanel();//�����°벿���ұ߽��棬���ֹ�����Ϊ��Ƭ����	
	private JPanel jpyInfo=new JPanel();//�Ҳ���ʾ������Ϣ�����	
	//�����°벿���ұߵ�JPanel�����ĸ�����Ϣ��Ŀ��Ŀؼ�	
	private JLabel[] jlInfo={new JLabel("�û����:"),new JLabel("����:"),
							 new JLabel("�Ա�:"),new JLabel("����:"),
							 new JLabel("�绰����:"),new JLabel("Email:"),
							 new JLabel("������:"),new JLabel("������Ƭ:"),
							 new JLabel("�ʱ�:"),new JLabel("��ַ:"),
							 new JLabel("�����Ƭ")};
	private JButton[] jbInfo={new JButton("�༭"),new JButton("����"),
							  new JButton("ɾ��"),new JButton("���"),
							  new JButton("��ӷ���"),new JButton("ɾ������"),
							  new JButton("���"),new JButton("�ϴ�"),
							  new JButton("ɾ��")};
	//��ʼĬ�ϵ�һЩ����
	private String[] str={"����","ͬ��","��ͥ","��Ҫ��ʿ","����"};
	private JComboBox jcb=new JComboBox(str);//���������б�ؼ�
	private JLabel jlPhoto=new JLabel();//��ʾͼ���JLabel�ؼ�
	private JTextField[] jtfInfo=new JTextField[10];	
	private JTextField jtfPhoto=new JTextField();//�����Ƭ������·��	
	private JFileChooser jfcPic=new JFileChooser("f:\\");//�ϴ�ͼ����ļ�ѡ����	
	private JFileChooser jfcPho=new JFileChooser("f:\\");//�ϴ���Ƭ���ļ�ѡ����		
	//�Ա𲿷�
	private JRadioButton jrbMale=new JRadioButton("��",true);
	private JRadioButton jrbFemale=new JRadioButton("Ů");
	private ButtonGroup bgGender=new ButtonGroup();	
	private JPanel jpGender=new JPanel();//��ѡ��ť���	
	private JPanel jpyview=new JPanel();//�Ҳ���ʾ�����Ƭ�����	
	private JScrollPane jspyview=new JScrollPane(jpyview);//������	
	private JLabel jlDetail=new JLabel();//�Ҳ���ʾһ��ͼƬ�ı�ǩ	
	private JScrollPane jspydetail=new JScrollPane(jlDetail);//��ʾһ��ͼƬ��ǩ�Ĺ�����
	private JLabel jlNoPic=new JLabel("û����Ƭ");//û����Ƭ����ʾJLabel	
	//ͼƬ���ؽ���������
	private JLabel jpProgress=new JLabel();//�Ҳ���ʾͼƬ���ؽ��ȵ����
	private JLabel jlProgress=new JLabel("Ԥ��ͼƬ������.....");
	private JProgressBar jpb=new JProgressBar(JProgressBar.HORIZONTAL,0,100);
	//ѡ�в�ͬ���ڵ�ʱ����ʾ��Ϣ����
	private JLabel jlRoot=new JLabel(icon,JLabel.LEFT);
	private JLabel jlGroup=new JLabel();//����ڵ��JLabel
	private CardLayout cl=new CardLayout();//������Ƭ���ֹ�����
	private JLabel[] jla=null;//��Ƭ��������	
	private JSplitPane jspOuter=//���·ָ��JSplitPane
						new JSplitPane(JSplitPane.VERTICAL_SPLIT,true);	
	private JSplitPane jspInner=//�����ҵ�JSplitPane
						new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jspz,jpy);
	//ϵͳ���̲���						
	private PopupMenu popup=new PopupMenu();	
	private SystemTray tray;//����SystemTray��Ա����	
	private TrayIcon trayIcon;//����TrayIcon��Ա����
	private MenuItem exit=new MenuItem("�˳�����");//����˵�
	private MenuItem currently=new MenuItem("��ʾ��ǰ�û�");//����˵�
	public MainFrame(String uname)
	{//���๹����
		this.uname=uname;//�����û���
		this.initJps();//�����ϰ벿�ֵĴ		
		this.initInfo();//��ʼ����Ϣ���	
		this.initJpy();//��ʼ����Ƭ���ֵ����			
		this.initTray();//���ϵͳ����
		this.initTree();//��ʼ����
		jtz.addTreeSelectionListener(//Ϊ���ڵ�����¼�������
		new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{//��дvalueChanged����
				DefaultMutableTreeNode cdmtn=//�õ�ѡ�нڵ����
				(DefaultMutableTreeNode)e.getPath().getLastPathComponent();
				NodeValue cnv=(NodeValue)cdmtn.getUserObject();//�õ��Զ���ڵ����
				if(cnv.classCode==0)
				{//ѡ�нڵ��Ǹ��ڵ�ʱ							
					cl.show(jpy,"root");
				}
				else if(cnv.classCode==1)
				{//ѡ�нڵ��Ƿ���ڵ�ʱ															
					String group=cnv.toString();
					jlGroup.setText(group);
					cl.show(jpy,"group");
				}
				else if(cnv.classCode==2)
				{//ѡ�нڵ���ĳһ��ϵ�˽ڵ�ʱ							
					String sql="select pid,pname,pgender,page,pnumber,pemail,pgroup,ppostalcode,"+
			"padress from lxy where uid='"+MainFrame.this.uname+"'and pname='"+cnv.toString()+"'";														
					setInfo(DButil.getPerInfo(sql));//�����ݿ�õ�����ϵ����Ϣ�����õ���Ϣ���									
					cl.show(jpy,"Info");
				}
				else if(cnv.classCode==3)
				{//���Ԥ��					
					jpyview.removeAll();//������Ԥ�����
					cl.show(jpy,"tpyl");//��ʾ���Ԥ�����																		
					viewPic(cdmtn);//Ԥ�����																				
				}
				else if(cnv.classCode==4)
				{//ͼƬ��ϸ
					cl.show(jpy,"tpmx");//��ʾͼƬ��ϸ���
					NodeValue pnv=//�õ�ѡ����Ƭ���Զ���ڵ����
					(NodeValue)((DefaultMutableTreeNode)cdmtn).getUserObject();	
					detailPic(pnv.value);//���ĳһ��ͼƬ
				}
			}
		}
							);									
		jspOuter.setDividerLocation(46);//���÷ָ��JSplitPane��λ��
		jspOuter.setTopComponent(jps);//���ô����ϰ벿�ֵĿؼ�
		jspOuter.setBottomComponent(jspInner);//�����°벿�ֵĿؼ�
		jspInner.setDividerLocation(200);//���÷ָ��JSplitPane��λ��
		jspOuter.setDividerSize(0);//���ô�ֱ�ָ��JSplitPane�Ŀ��
		jspInner.setDividerSize(4);//����ˮƽ�ָ��JSplitPane�Ŀ��
		this.add(jspOuter);//���ָ����ӵ�������
		//���ô���رհ�ťִ�еĶ���
		this.addWindowListener(
					new WindowAdapter()
					{
						public void WindowClosing(WindowEvent e)
						{
							//����������
							MainFrame.this.hide();
						}
					}
							    );
		//�����������ͼ�ꡢ���⡢��С�Լ��ɼ���
		Image image=Toolkit.getDefaultToolkit().getImage("img/link.png");//�õ�ͼ�����		
		this.setIconImage(image);		
		this.setTitle(uname+"��ͨѶ¼");	
		this.setBounds(250,50,700,650);
		this.setVisible(true);
	}
	public void initJps()
	{//�����ϰ벿�ֵĳ�ʼ��
		jps.setLayout(null);//����jps���ֹ�����Ϊnull
		//���ð�ť��С����ӵ�JPanel�����
		jba.setBounds(5,10,80,26);		
		jba.addActionListener(this);//Ϊ��Ӱ�ťע���¼�������
		jps.add(jba);//��Ӱ�ť��jps�����
		jbs.setBounds(90,10,80,26);
		jbs.addActionListener(this);//Ϊ���Ұ�ťע���¼�������
		jps.add(jbs);//��Ӱ�ť��jps�����
		//����jtfs�ı����С����ӵ�jps�����
		jtfs.setBounds(175,10,120,26);
		jtfs.addActionListener(this);//Ϊ�ı���ע���¼�������
		jps.add(jtfs);
		//���õ�ѡ��ť��С��λ�ò���ӵ�jpbr�����ͬʱ��ӵ�bg��ѡ��ť����
		jrbxm.setBounds(5,3,50,26);
		jrbxm.addItemListener(this);//Ϊ��ѡ��ťע��ItemEvent�¼�������
		bg.add(jrbxm);
		jpbr.add(jrbxm);
		jrbbh.setBounds(60,3,50,26);
		jrbbh.addItemListener(this);//Ϊ��ѡ��ťע��ItemEvent�¼�������
		bg.add(jrbbh);
		jpbr.add(jrbbh);
		jpbr.setBounds(300,5,200,26);
		jps.add(jpbr);
	}
	public void initTree()
	{//��ʼ����
		jtz.setModel(dtm);//������ģ��		
		jtz.setExpandsSelectedPaths(true);//������ExpandsSelectedPaths����		
		jtz.setCellRenderer(dtcr);//�������Ľڵ������		
		ImageIcon icon=new ImageIcon("img/wzk.png");//�õ����ڵ�رյ�ͼ��		
		dtcr.setClosedIcon(icon);//�������ڵ�رյ�ͼ��		
		icon=new ImageIcon("img/zk.png");//�õ����ڵ�չ����ͼ��
		dtcr.setOpenIcon(icon);//�������ڵ�չ����ͼ��
		icon=new ImageIcon("img/mzjd.png");//�õ�����Ҷ�ӽڵ��ͼ��
		dtcr.setLeafIcon(icon);//��������Ҷ�ӽڵ��ͼ��
		Vector<String> group=DButil.getNode(uname,"uid");//�����ݿ���ж��ٸ�����
		for(int i=0;i<group.size();i++)
		{//�����ڵ�
			String s=group.get(i);			
			DefaultMutableTreeNode dmtnGroup=//��������ڵ����
							new DefaultMutableTreeNode(new NodeValue(s,1));
			dtm.insertNodeInto(dmtnGroup,root,i);//������ڵ���ӵ����ڵ�			
			//��������ڵ�
			Vector<String> pnode=DButil.getNode(uname,"pname;"+s);		
			for(String person:pnode)
			{
				dmtnGroup.add(this.initPerNode(person));//�����������µ���ϵ�˽ڵ���ӵ�����ڵ�
			}					
		}
	}
	public void initJpy()
	{//�����ұ�Ϊ��Ƭ���ֵ�JPanel��һЩ�ؼ������
		jpy.setLayout(cl);
		//����ѡ�и��ڵ���ʾ��Ϣ��ʽ����ӵ����		
		jlRoot.setFont(new Font("Courier",Font.PLAIN,22));
		jlRoot.setHorizontalAlignment(JLabel.CENTER);
		jlRoot.setVerticalAlignment(JLabel.CENTER);
		jpy.add("root",jlRoot);//��Ӹ��ڵ���ʾ��Ϣ				
		jpy.add("Info",jpyInfo);//�����ϵ����Ϣ���
		//����ѡ�з���ڵ���ʾ��Ϣ��ʽ����ӵ����
		jlGroup.setFont(new Font("Courier",Font.PLAIN,22));
		jlGroup.setHorizontalAlignment(JLabel.CENTER);
		jlGroup.setVerticalAlignment(JLabel.CENTER);		
		jpy.add("group",jlGroup);//��ӷ���ڵ���ʾ��Ϣ
		//��ʼ��ͼƬԤ�����沢��ӵ����		
		jpyview.setBackground(Color.black);//���ñ���ɫΪ��ɫ
		jpyview.setLayout(new FlowLayout(FlowLayout.LEFT));
		jpy.add("tpyl",jspyview);
		//�������û����Ƭʱ��ʾ��Ϣ��ʽ����ӵ����
		jlNoPic.setFont(new Font("Courier",Font.PLAIN,22));
		jlNoPic.setHorizontalAlignment(JLabel.CENTER);
		jlNoPic.setVerticalAlignment(JLabel.CENTER);
		jpy.add("nopic",jlNoPic);	
		//��ʼ��ͼƬ��ϸ���沢��ӵ����
		jlDetail.setOpaque(true);
	    jlDetail.setBackground(Color.black);//���ñ���ɫΪ��ɫ	
		jlDetail.setVerticalAlignment(JLabel.CENTER);
		jlDetail.setHorizontalAlignment(JLabel.CENTER);
		jpy.add("tpmx",jspydetail);	
		//��ʼ��ͼƬ���ؽ��Ƚ���
		jpy.add("tpjd",jpProgress);
		jpProgress.setLayout(null);
		jlProgress.setBounds(20,20,200,30);//���ô�С��λ��
		jlProgress.setFont(new Font("����_GB2312",Font.PLAIN,20));
		jpProgress.add(jlProgress);//��ӽ�����
		jpb.setBounds(20,50,200,20);//���ô�С��λ��
		jpProgress.add(jpb);
		jpb.setBorderPainted(true);//���ý������߿���ʾ
		jpb.setStringPainted(true);//���ý������ַ���ʾ
	}	
	public void initInfo()
	{//��ʼ����Ϣ����
		jpyInfo.setLayout(null);//���ò��ֹ�����Ϊ��
		jpyInfo.setBounds(50,50,480,460);//������Ϣ���Ĵ�С��λ��		
		jlPhoto.setBounds(220,10,150,170);//������ϵ��ͼ��JLabel�Ĵ�С��λ��
		jlPhoto.setBorder(BorderFactory.createLineBorder(Color.BLACK));//��JLbel�ı߿������ֳ���
		jpyInfo.add(jlPhoto);//����ʾ��ϵ����Ƭ��JLabel��ӵ���Ϣ���
		for(int i=0;i<10;i++)//����ı�����򣬲����ô�С��λ��
		{
			jlInfo[i].setBounds(20,10+i*30,60,26);
			jpyInfo.add(jlInfo[i]);
		}
		//�����Ƭ���ֵĿؼ�
		jlInfo[10].setBounds(20,360,60,26);
		jpyInfo.add(jlInfo[10]);		
		jtfPhoto.setBounds(80,360,200,26);//���õõ���Ƭ·�����ı���Ĵ�С��λ��
		jpyInfo.add(jtfPhoto);//���õ���Ƭ·�����ı�����ӵ���Ϣ���
		jbInfo[6].setBounds(285,360,80,26);
		jbInfo[6].addActionListener(this);//Ϊ�����Ƭ�������ťע���¼�������
		jpyInfo.add(jbInfo[6]);
		//�����ļ�ѡ�����ļ���ѡ���ļ���ʽ		
		jfcPho.addChoosableFileFilter(new FileNameExtensionFilter("GIFͼƬ�ļ�","gif","GIF"));
		jfcPho.addChoosableFileFilter(new FileNameExtensionFilter("PNGͼƬ�ļ�","png","PNG"));
		jfcPho.addChoosableFileFilter(new FileNameExtensionFilter("JPEGͼƬ�ļ�","jpg","jpeg"));
		for(int i=0;i<10;i++)
		{//��ʼ��һЩ�ı���
			jtfInfo[i]=new JTextField();
		}
		for(int i=1;i<7;i++)
		{//����һЩ�����ı����λ��
			if(i!=2&i!=6)
			{
				jtfInfo[i].setBounds(80,10+i*30,135,26);
				jtfInfo[i].addActionListener(this);//Ϊ�ı���ע���¼�������				
				jpyInfo.add(jtfInfo[i]);//���ı�����ӵ���Ϣ���				
			}
		}
		//�Ա𲿷�
		jrbMale.setBounds(5,3,50,26);
		jrbMale.addItemListener(this);		//Ϊ��ѡ��ťע��ItemEvent�¼�������		
		bgGender.add(jrbMale);
		jpGender.add(jrbMale);
		jrbFemale.setBounds(60,3,50,26);		
		jrbFemale.addItemListener(this);	//Ϊ��ѡ��ťע��ItemEvent�¼�������
		bgGender.add(jrbFemale);
		jpGender.add(jrbFemale);
		jpGender.setBounds(60,70,125,26);
		jpyInfo.add(jpGender);				//����ѡ��ť�����jpbr��ӵ�jps��
		//����
		jcb.setBounds(80,190,75,26);		
		jcb.setEditable(false);//���÷����ı�Ϊ���ɱ༭
		this.initGroup();//��ʼ�����������б��
		jcb.setSelectedIndex(4);//Ĭ��ѡ�������������
		jpyInfo.add(jcb);//�����������б����ӵ���Ϣ���		
		for(int i=0;i<2;i++)//��ӷ���ɾ�����鰴ť
		{
			jbInfo[4+i].setBounds(175+100*i,190,90,26);
			jbInfo[4+i].addActionListener(this);//Ϊ��ťע���¼�������
			jpyInfo.add(jbInfo[4+i]);
		}
		//�û����
		jtfInfo[0].setBounds(80,10,135,26);
		jpyInfo.add(jtfInfo[0]);
		//����ͼ��
		jtfInfo[7].setBounds(80,220,200,26);
		jpyInfo.add(jtfInfo[7]);
		jbInfo[3].setBounds(285,220,80,26);
		jbInfo[3].addActionListener(this);//Ϊ��ťע���¼�������
		jpyInfo.add(jbInfo[3]);//�����ͼ��������ť��ӵ���Ϣ���
		//�����ļ�ѡ�����ļ���ѡ���ļ���ʽ	
		jfcPic.addChoosableFileFilter(new FileNameExtensionFilter("GIFͼƬ�ļ�","gif","GIF"));
		jfcPic.addChoosableFileFilter(new FileNameExtensionFilter("PNGͼƬ�ļ�","png","PNG"));
		jfcPic.addChoosableFileFilter(new FileNameExtensionFilter("JPEGͼƬ�ļ�","jpg","jpeg"));		
		//�ʱ��ı�������
		jtfInfo[8].setBounds(80,250,135,26);
		jpyInfo.add(jtfInfo[8]);
		//��ַ�ı�������
		jtfInfo[9].setBounds(80,280,285,26);
		jpyInfo.add(jtfInfo[9]);
		//�༭ ���� ɾ�� �Ȱ�ť
		for(int i=0;i<3;i++)
		{
			jbInfo[i].setBounds(80+i*100,320,80,26);
			jbInfo[i].addActionListener(this);//Ϊ��ťע���¼�������
			jpyInfo.add(jbInfo[i]);
		}
		for(int i=0;i<2;i++)
		{//�ϴ���ɾ����ť
			jbInfo[7+i].setBounds(80+i*100,395,80,26);
			jbInfo[7+i].addActionListener(this);//Ϊ��ťע���¼�������
			jpyInfo.add(jbInfo[7+i]);
		}
	}
	public void initGroup()//��ʼ�����������б�
	{
		Vector<String> v=DButil.getNode(uname,"uid");//�õ����з����б�
		boolean b=false;//��¼�����б����Ƿ�������е�ѡ��
		for(int i=0;i<v.size();i++)
		{
			for(int j=0;j<jcb.getItemCount();j++)
			{
				if(v.get(i).equals(jcb.getItemAt(j)))
				{
					b=true;	break;//�����б���д��ڴ�ѡ��ʱ					
				}			
			}
			if(b==false)
			{//�����б���в����ڴ�ѡ��ʱ ������ӵ����������б��		
				jcb.addItem(v.get(i));
			}
			else
			{b=false;}//��b��Ϊfalse �Դ���һ��ѭ��ʹ��											
		}
	}
	public void initTray()//��ʼ��ϵͳ����
	{		
		exit.addActionListener(this);//Ϊ�˵�ѡ��ע�������
		currently.addActionListener(this);//Ϊ�˵�ѡ��ע�������		
		popup.add(currently);//���˵�ѡ����ӵ��˵�
		popup.add(exit);//���˵�ѡ����ӵ��˵�		
		if(SystemTray.isSupported())//�жϵ�ǰϵͳ�Ƿ�֧��ϵͳ����
		{			
			tray=SystemTray.getSystemTray();//ͨ����̬�����õ�ϵͳ����			
			Image image=Toolkit.getDefaultToolkit().getImage("img/link.png");//����ͼ��			
			trayIcon=new TrayIcon(image,"�ҵ�ͨѶ¼",popup);//����TrayIcon����õ�����ͼ��			
			trayIcon.setImageAutoSize(true);//��������ͼ���Զ����óߴ�
			try
			{//������ͼ�����õ�ϵͳ������
				tray.add(trayIcon);				
			}
			catch(AWTException e)
			{
				e.printStackTrace();
			}
			trayIcon.addActionListener(this);//ΪtrayIconע���¼�������
		}
	}
	public DefaultMutableTreeNode initPerNode(String person)//������ϵ�˽ڵ�
	{
		DefaultMutableTreeNode dmtnPerson=//���ݵõ�����������һ�����ڵ�
				new DefaultMutableTreeNode(new NodeValue(person,2));	
		DefaultMutableTreeNode dmtnPhoto=//����ϵ�˽ڵ���������ڵ�
				new DefaultMutableTreeNode(new NodeValue("���",3));
		dmtnPerson.add(dmtnPhoto);	
		Vector<String> pphoto=DButil.getNode(uname,person);//�õ����û��¸���ϵ�˵������Ƭ�����б�
		for(String photo:pphoto)
		{
			DefaultMutableTreeNode Photo=//������Ƭ�ڵ�
				new DefaultMutableTreeNode(new NodeValue(photo,4));
			dmtnPhoto.add(Photo);//��ӵ����ڵ���
		}	
		return dmtnPerson;//�������ɵĽڵ�		
	}
	public void clearInfo()//�����Ϣ���
	{		
		for(int i=0;i<10;i++)
		{
			jtfInfo[i].setText("");//����ı���
		}
		jlPhoto.setIcon(null);//���ͼ��
		jcb.setSelectedItem("����");//���÷���ѡ���������顱
	}
	public void setInfo(Vector<String> pInfo)//����Ϣ�������õ���Ϣ�����
	{//����Ϣ�������������Ϣ�����
		this.clearInfo();		
		if(pInfo.size()==0)
		{
			JOptionPane.showMessageDialog(this,"�����û������ڣ�����","����",
											JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			for(int i=0;i<2;i++)
			{//��ʾ��ϵ�˱�ź�����
				jtfInfo[i].setText(pInfo.get(i));
			}			
			if(pInfo.get(2).equals("��"))
			{//��ʾ�Ա�
				jrbMale.setSelected(true);
			}
			else
			{//��ʾ�Ա�
				jrbFemale.setSelected(true);
			}
			for(int i=3;i<6;i++)
			{//��ʾ���䡢�绰����͵����ʼ�
				jtfInfo[i].setText(pInfo.get(i));
			}
			for(int i=7;i<9;i++)
			{//��ʾ�ʱ�͵�ַ
				jtfInfo[i+1].setText(pInfo.get(i));
			}			
			jcb.setSelectedItem(pInfo.get(6));  //���÷�����Ϣ
			this.setPic(pInfo.get(0));//����ͼ��Ϊ�����ݿ�õ���ͼ��
			this.setEditable(false);//������岻�ɱ༭
		}	
	}
	public void setPic(String pid)//���ø���ͼ����ʾ
	{
		final int width=150;//��ʾͼ���JLabel�Ŀ��
		final int height=170;//��ʾͼ���JLabel�ĸ߶�
		String sql="select pphoto from lxy where pid='"+pid+"'";
		Image Pic=DButil.getPic(sql);//�����ݿ�õ����˵�ͼ��
		if(Pic!=null)
		{//�������ϵ���ϴ���ͼ��
			int pw=Pic.getWidth(MainFrame.this);//�õ�����ͼ��Ŀ��
			int ph=Pic.getHeight(MainFrame.this);//�õ�����ͼ��ĸ߶�
			if(pw>ph)
			{//��ȴ��ڸ߶ȣ�ͼ���������
				Pic=Pic.getScaledInstance(width,width*ph/pw,Image.SCALE_SMOOTH);			
			}
			else
			{//�߶ȴ��ڿ�ȣ�ͼ���������
				Pic=Pic.getScaledInstance(height*pw/ph,height,Image.SCALE_SMOOTH);				
			}
			jlPhoto.setIcon(new ImageIcon(Pic));//��ͼ����ʾ��JLabel
			jlPhoto.setHorizontalAlignment(JLabel.CENTER);//����ͼƬˮƽ������ʾ
			jlPhoto.setVerticalAlignment(JLabel.CENTER);//����ͼƬ��ֱ���������ʾ
		}
		else
		{//���ͼ��Ϊ�գ�����ʾ
			jlPhoto.setIcon(null);
		}		
	}
	public void setEditable(boolean Editable)//������Ϣ�����Ƿ�ɱ༭
	{
		jrbFemale.setEnabled(Editable);//�����Ա��Ƿ�ɱ༭
		jrbMale.setEnabled(Editable);//�����Ա��Ƿ�ɱ༭
		jcb.setEnabled(Editable);//���������б���Ƿ�ɱ༭		
		for(int i=0;i<10;i++)
		{
			jtfInfo[i].setEditable(Editable);//�����ı����Ƿ�ɱ༭
			if(i>=3&&i<=5)
			{
				jbInfo[i].setEnabled(Editable);//���ò��ְ�ť�Ƿ�ɱ༭						
			}			
		}
		jbInfo[1].setEnabled(Editable);//���ñ��水ť�Ƿ�ɱ༭
	}
	public Vector<String> getInfo()//����Ϣ���õ��û��������Ϣ
	{
		Vector<String> pInfo=new Vector<String>();
		pInfo.add(jtfInfo[0].getText().trim());//���pid
		pInfo.add(jtfInfo[1].getText().trim());//���pname
		String gender=jrbMale.isSelected()?"��":"Ů";
		pInfo.add(gender);//����Ա�
		pInfo.add(jtfInfo[3].getText().trim());//����
		pInfo.add(jtfInfo[4].getText().trim());//�绰����
		pInfo.add(jtfInfo[5].getText().trim());//Email	
		pInfo.add((String)jcb.getSelectedItem());//����
		pInfo.add(jtfInfo[8].getText().trim());//�ʱ�
		pInfo.add(jtfInfo[9].getText().trim());//��ַ
		String photoPath=jtfInfo[7].getText().trim();//�õ���Ƭ·��
		pInfo.add(photoPath);//��Ƭ·��		
		return pInfo;
	}
//************************�������***********************************************	
	public void monitorAddGroupButton()//������ӷ��鰴ť
	{
		String group=JOptionPane.showInputDialog(this,"���������:","��ӷ���",
													JOptionPane.PLAIN_MESSAGE);
		if(group!=null)
		{//group������null�����
			if(group.equals(""))
			{//��������ǿ�ֵ
				JOptionPane.showMessageDialog(this,"���Ʋ���Ϊ��","����",
											JOptionPane.WARNING_MESSAGE);
			}
			else
			{//��������Ϊ��
				int count=jcb.getItemCount();//�õ���������ѡ��ĸ���
				for(int i=0;i<count;i++)
				{
					if(group.equals((String)jcb.getItemAt(i)))
					{//�Ƿ�����ͬ�ķ���
						JOptionPane.showMessageDialog(this,"���ܲ�����ͬ����","����",
												JOptionPane.WARNING_MESSAGE);
						return;//����ͬ�ķ��飬����ӣ�ֱ�ӷ���
					}				
				}							
				jcb.addItem(group);//��ӷ��鵽�����б�				
				jcb.setSelectedItem(group);//���÷���ѡ��Ϊ����ӵķ���				
				this.addGroupNode(group);//��������ӷ���				
			}
		}
	}
	public void addGroupNode(String group)//��������ӷ���ڵ�
	{
		DefaultMutableTreeNode newGroupNode=//����һ���������͵Ľڵ�
								new DefaultMutableTreeNode(new NodeValue(group,1));
		root.add(newGroupNode);//���ڵ���ӵ����ĸ��ڵ���
		((DefaultTreeModel)jtz.getModel()).reload();//������ģ��
	}
	public void monitorDelGroupButton()//ɾ������ļ���
	{
		int i=JOptionPane.showConfirmDialog(this,"ɾ�����齫��ɾ��"+
			"�������������ϵ�ˣ������Ƿ�ɾ����","ȷ��",JOptionPane.YES_NO_OPTION);
		if(i==JOptionPane.YES_OPTION)
		{//ȷ����
			String group=(String)jcb.getSelectedItem();
			this.delGroupNode(group);//���ϵ���Ӧ
			int count=DButil.delGroup(uname,group);//���ݿ����ɾ��
			jcb.removeItem((String)jcb.getSelectedItem());//�����б��ɾ��
			if(count>=0)
			{
				JOptionPane.showMessageDialog(this,"ɾ������ɹ�����ɾ����ϵ��"+
						count+"��������","��Ϣ",JOptionPane.INFORMATION_MESSAGE);
			}
			this.clearInfo();//�����Ϣ���			
		}
	}
	public void delGroupNode(String group)
	{//ɾ������ڵ�
		for(int i=0;i<root.getChildCount();i++)
		{
			DefaultMutableTreeNode groupNode=//�õ�����ڵ�
						(DefaultMutableTreeNode)root.getChildAt(i);			
			NodeValue groupNv=(NodeValue)groupNode.getUserObject();//תΪ�Զ���Ķ���
			if(group.equals(groupNv.getValue()))
			{//�ҵ�Ҫɾ���Ľڵ�
				root.remove(groupNode);//ɾ���˷���ڵ�				
				((DefaultTreeModel)jtz.getModel()).reload(groupNode);//��ģ�͸���
				break;//ɾ���ɹ����˳�ѭ��
			}				
		}
	}
//************************��ϵ�˹���*********************************************
	public void monitorDelButton()//����ɾ����ť�ķ���
	{	
		String personName=jtfInfo[1].getText().trim();//�õ���ϵ�˵�����
		String personGroup=(String)jcb.getSelectedItem();//�õ���������
		String pid=jtfInfo[0].getText().trim();//�õ���ϵ��ID
		//����ɾ���Ի���
		int index=JOptionPane.showConfirmDialog(this,"�Ƿ�ɾ����y/n","ȷ�϶Ի���",
							JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
		if(index==0)
		{//ȷ��ɾ��		
			boolean b=this.delPerNode(personName,personGroup);//�������б�
			if(b==true)
			{
				String sqla="delete from photo where pid='"+pid+"'";
				String sqlb="delete from lxy where pid='"+pid+"'";
				DButil.update(sqla);//ɾ����ϵ�˵������������Ƭ
				int i=DButil.update(sqlb);//ɾ����ϵ����Ϣ
				if(i!=-1)
				{//ɾ���ɹ��Ի���					
					JOptionPane.showMessageDialog(this,"ɾ���ɹ�","ɾ��",
											JOptionPane.INFORMATION_MESSAGE);
					this.clearInfo();						
				}
			}		
		}
	}	
	public void monitorSaveButton()//�������水ť�ķ���
	{
		String pid=jtfInfo[0].getText().trim();//�õ���ϵ�˵ı��
		String pname=jtfInfo[1].getText().trim();//�õ���ϵ�˵�����
		String sqla="select * from lxy where pid='"+pid+"'";//�жϴ˱���Ƿ���ڵ�SQL
		String sqlb="select * from lxy where pname='"+pname+"'";//�жϴ������Ƿ���ڵ�SQL
		boolean isIdExist=DButil.isExist(sqla);//�õ�����Ƿ����
		boolean isNameExist=DButil.isExist(sqlb);//�õ������Ƿ����		
		String perGroupAfter=(String)jcb.getSelectedItem();//�õ��޸�֮��ķ���		
		String perNameAfter=jtfInfo[1].getText().trim();//�õ��޸ĺ���ϵ�˵�����					
		if(!(pid.equals("")||pname.equals("")))
		{
			if(this.isInsert)//����ӵĻ�
			{			
				if(!(isIdExist||isNameExist))
				{					
					//�õ������ͼƬ·���Ƿ�Ϸ����ж��ַ���
					String s=DButil.insertPerson(uname,this.getInfo());
					if(!s.equals("isNull"))
					{//��������ӽڵ� ��Ϊ�Ϸ����ļ�·��
						this.addPerNode(perNameAfter,perGroupAfter);
					}
					else
					{
						JOptionPane.showMessageDialog(this,"ͼ��·�����Ϸ�"+
							"�����ϵ��ʧ��","����",JOptionPane.WARNING_MESSAGE);
					}							
				}
				else
				{//�û�������ID�Ѿ�����
					JOptionPane.showMessageDialog(this,"��ϵ��ID���������Ѿ�����","����",
											JOptionPane.WARNING_MESSAGE);
				}
			}
			else
			{//�༭��ϵ������
				if(isIdExist)
				{//IDû����и���
					String s=DButil.updatePerson(uname,this.getInfo());
					if(!s.equals("isNull"))
					{	
						//��ɾ����ǰ����ϵ�˽ڵ�
						this.delPerNode(this.perNameBefor,this.perGroupBefor);
						//����µĽڵ㵽�޸ĵ���
						this.addPerNode(perNameAfter,perGroupAfter);		
					}
					else
					{
						JOptionPane.showMessageDialog(this,"ͼ��·�����Ϸ�"+
							"�����ϵ��ʧ��","����",JOptionPane.WARNING_MESSAGE);						
					}
				}
				this.perNameBefor=null;//��perNameBefor�ÿ�
				this.perGroupBefor=null;//��perGroupBefor�ÿ�
				if(!isIdExist)
				{//ID����
					if(!isNameExist)
					{//����Ҳ���˾�ִ�в��붯��
						String s=DButil.insertPerson(uname,this.getInfo());
						if(!s.equals("isNull"))
						{//��������ӽڵ� ��Ϊ�Ϸ����ļ�·��
							this.addPerNode(perNameAfter,perGroupAfter);
						}
						else
						{
							JOptionPane.showMessageDialog(this,"ͼ��·�����Ϸ�"+
								"�����ϵ��ʧ��","����",JOptionPane.WARNING_MESSAGE);
						}
					}
					else
					{
						DButil.update("delete from lxy where pname='"+perNameAfter+"'");				
						DButil.insertPerson(uname,this.getInfo());//���²�����˼�¼						
					}
				}										
			}				
		}
		else
		{//Ϊ��
			JOptionPane.showMessageDialog(this,"��ϵ��ID������������Ϊ��","����",
									JOptionPane.WARNING_MESSAGE);
			return;				
		}
		this.isInsert=false;this.setEditable(false);		
	}	
	public void monitorSearchButton()//�������Ұ�ť�ķ���
	{
		String name=jtfs.getText().trim();
		String sql="";//���������ַ���
		if(name.equals(""))
		{
			JOptionPane.showMessageDialog(this,"������������Ϊ�գ�����",
										"����",	JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			if(this.searchByName==true)
			{//����������
	
				sql="select pid,pname,pgender,page,pnumber,pemail,pgroup,ppostalcode,"+
					"padress from lxy where uid='"+uname+"'and pname='"+name+"'";				
				this.setInfo(DButil.getPerInfo(sql));//������Ϣ���Ϊ����ϵ�˵���Ϣ			
			}
			else
			{//����Ų���
				sql="select pid,pname,pgender,page,pnumber,pemail,pgroup,ppostalcode,"+
					"padress from lxy where uid='"+uname+"'and pid='"+name+"'";
				this.setInfo(DButil.getPerInfo(sql));;//������Ϣ���Ϊ����ϵ�˵���Ϣ		
			}
		}
		this.setEditable(false);//������岻�ɱ༭
		cl.show(jpy,"Info");//��Info�����ʾ����		
	}
	public void addPerNode(String personName,String group)//�����ϵ�˽ڵ�
	{
		//�������������ϵ�˽ڵ��һЩ��Ϣ
		boolean flag=false;//����ϵ�˷����������Ƿ���ڵı�־
		DefaultMutableTreeNode perNode=this.initPerNode(personName);//������ϵ�˽ڵ�
		DefaultMutableTreeNode groupNode;//����ڵ�				
		for(int i=0;i<root.getChildCount();i++)//�ڵ������ɣ��������ҵ��ڵ������ĸ��ڵ�
		{//�õ�����ڵ��ֵ
			groupNode=(DefaultMutableTreeNode)root.getChildAt(i);
			NodeValue groupNv=(NodeValue)groupNode.getUserObject();
			if(groupNv.getValue().equals(group))
			{//�õ��ķ���ڵ��ֵ���ڴ����������飬�ҽӽڵ�
				groupNode.add(perNode);//����ϵ�˽ڵ���ӵ��˷���ڵ�
				((DefaultTreeModel)jtz.getModel()).reload(groupNode);//������ģ��
				flag=true; //���ϴ��ڴ˷���ڵ��ñ�־λΪtrue
				break;//�˳�ѭ��
			}
		}
		if(flag==false)
		{//����ڵ������в�����
			DefaultMutableTreeNode dmtGroup=//�����µķ���ڵ�
							new DefaultMutableTreeNode(new NodeValue(group,1));
			dmtGroup.add(perNode);//�½�һ������ڵ㣬������ӵ��½��ķ�����
			root.add(dmtGroup);//����ҵ�����
			((DefaultTreeModel)jtz.getModel()).reload(root);//������ģ��			    	
		}		
	}
	public boolean delPerNode(String personName,String group)//ɾ����ϵ�˽ڵ�
	{
		boolean flag=false;  //�Ƿ�ɾ���ɹ�	
		DefaultMutableTreeNode groupNode;//��������ڵ�
		DefaultMutableTreeNode personNode;//������ϵ�˽ڵ�
		for(int i=0;i<root.getChildCount();i++)
		{//ѭ���õ�Ҫɾ����ϵ�˵ķ���ڵ�
			groupNode=(DefaultMutableTreeNode)root.getChildAt(i);
			NodeValue groupNv=(NodeValue)groupNode.getUserObject();
			if(groupNv.getValue().equals(group))
			{
				for(int j=0;j<groupNode.getChildCount();j++)
				{//�õ�����ڵ�����ϵ�˽ڵ�
					personNode=(DefaultMutableTreeNode)groupNode.getChildAt(j);
					NodeValue personNv=(NodeValue)personNode.getUserObject();
					if(personNv.getValue().equals(personName))
					{//�ҵ����˽ڵ� ���ڴӴ˽ڵ�õ����ڵ㣬Ȼ����ӵ����ڵ�һ����Ƭ�ڵ�
						personNode.removeFromParent();
						((DefaultTreeModel)jtz.getModel()).reload(groupNode);
						flag=true;//ɾ���ɹ�
						break;//�˳�ѭ��
					}
				}
				break;//�˳�ѭ��
			}
		}
		if(flag==false)
		{//ɾ��ʧ�ܣ�������ʾ��Ϣ
			JOptionPane.showMessageDialog(this,"�˷�����û������ˣ�����",
											"����",JOptionPane.WARNING_MESSAGE);	
		}
		return flag;
	}
//***************************��Ƭ����*******************************************	
	public void monitorUploadButton()//�����ϴ���Ƭ�ķ���
	{	
		String path=jtfPhoto.getText();//�õ���Ƭ·��
		if(path.equals("")||jtfInfo[0].getText().equals(""))
		{//·��Ϊ�ջ��߱��Ϊ��
			JOptionPane.showMessageDialog(this,"·��������ϵ�˱�Ų���Ϊ�գ�����","����",
										JOptionPane.WARNING_MESSAGE);
		}
		else
		{//·���ͱ�ž���Ϊ��
			int i=DButil.insertPic(path,jtfInfo[0].getText());
			switch(i)
			{
				case 0://�ϴ��ɹ���������Ƭ�ڵ�
				JOptionPane.showMessageDialog(this,"�ϴ��ɹ�","��ϲ�㣡����",
										JOptionPane.PLAIN_MESSAGE);
				this.addPhoNode(path);
										break;
				case 1://·������
				JOptionPane.showMessageDialog(this,"ϵͳ�Ҳ����ļ�������",
										"����",JOptionPane.WARNING_MESSAGE);
										break;
				case 2://���ļ����Ѵ���
				JOptionPane.showMessageDialog(this,"��Ƭ�ļ��Ѵ��ڻ���û��"+
				"����ϵ�ˣ��������������ϵ��","����",JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	public void addPhoNode(String photoPath)//�����������Ƭ�ڵ�
	{
		String photoName=(new File(photoPath)).getName();	//�õ���Ƭ������
		String personName=jtfInfo[1].getText().trim();//�õ���ϵ�˵�����
		String group=(String)jcb.getSelectedItem();	//�õ����������
		DefaultMutableTreeNode groupNode;//��������ڵ�
		DefaultMutableTreeNode personNode;//������ϵ�˽ڵ�
		DefaultMutableTreeNode photoNode;//������Ƭ�ڵ�
		for(int i=0;i<root.getChildCount();i++)
		{//ѭ���õ������Ƭ��ϵ�˵ķ���ڵ�
			groupNode=(DefaultMutableTreeNode)root.getChildAt(i);
			NodeValue groupNv=(NodeValue)groupNode.getUserObject();
			if(groupNv.getValue().equals(group))
			{//�õ�����ϵ�˵ķ���				
				for(int j=0;j<groupNode.getChildCount();j++)
				{//�õ�����ڵ��µ������Ƭ��ϵ�˵Ľڵ�
					personNode=(DefaultMutableTreeNode)groupNode.getChildAt(j);
					NodeValue personNv=(NodeValue)personNode.getUserObject();
					if(personNv.getValue().equals(personName))
					{//�ҵ����˽ڵ� ���ڴӴ˽ڵ�õ����ڵ�						
						photoNode=(DefaultMutableTreeNode)personNode.getChildAt(0);
						photoNode.add(new DefaultMutableTreeNode(new NodeValue(photoName,4)));
						//֪ͨ��ģ�����Ѹı�
						((DefaultTreeModel)jtz.getModel()).reload(photoNode);												
						break;//�˳��ڲ�ѭ��
					}
				}
			break;//�˳����ѭ��
			}
		}
	}
	public void delPhoNode(String phoName)
	{
		boolean isDel=false;
		if(phoName.equals("")||phoName==null)
		{//����Ϊ���򵯳�������Ϣ
			JOptionPane.showMessageDialog(this,"��Ƭ���Ʋ��ԣ����������Ƭ�ı�����"+
							"������Ƭ���ƣ�����","����",JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			TreePath tp=jtz.getSelectionPath();//�õ���ǰ·��			
			try
			{
				DefaultMutableTreeNode perNode=//�õ���ǰ·��ѡ�еĽڵ�
							(DefaultMutableTreeNode)tp.getLastPathComponent();			
				DefaultMutableTreeNode xc=//�õ�ѡ����ϵ����������ڵ�
								(DefaultMutableTreeNode)perNode.getFirstChild();
				for(int i=0;i<xc.getChildCount();i++)
				{					
					DefaultMutableTreeNode phoNode=//�õ�����µ���Ƭ�ڵ�
								(DefaultMutableTreeNode)xc.getChildAt(i);
					NodeValue zpmc=(NodeValue)phoNode.getUserObject();//�õ��Զ���ڵ����				
					if(zpmc.getValue().equals(phoName))
					{
						isDel=true;//�Ƿ�ɾ������Ϊtrue						
						DButil.update("delete from photo where photoname='"+phoName+"'");//����
												
						xc.remove(i);//�������ɾ������ڵ�
						((DefaultTreeModel)jtz.getModel()).reload(xc);//������ģ��
						jtfPhoto.setText("");//����ı��������Ƭ����
						return;
					}
				}
			}
			catch(NullPointerException npe)
			{
				JOptionPane.showMessageDialog(this,"������ѡ�����ϵ�˶�ɾ����Ƭ������","����",
											JOptionPane.WARNING_MESSAGE);
				jtfPhoto.setText("");//��������Ƭ�ı���
			}
			if(!isDel)
			{
				JOptionPane.showMessageDialog(this,"����û��������Ƭ","����",
											JOptionPane.WARNING_MESSAGE);
				jtfPhoto.setText("");//��������Ƭ�ı���
			}			
		}		
	}
//*******************************��ƬԤ������Ƭ��ϸ********************************
	public void viewPic(final DefaultMutableTreeNode cdmtn) //ͼƬԤ��
	{		
		if(cdmtn.getChildCount()==0)
		{//�����û����Ƭ
			cl.show(jpy,"nopic");
		}
		else
		{
			cl.show(jpy,"tpjd");//��ʾ���ؽ�����
			this.setEnabled(false);//����ͼƬʱ���������б�
		    new Thread()//�½�һ�̼߳���ͼƬ���������س�
		    {
		    	public void run()//ʵ��run����
		    	{			    		
		    		final int width=160;//����Ԥ����Ƭ�Ŀ��
		    		final int height=160;//����Ԥ����Ƭ�ĸ߶�
		    		final int span=10;//������Ƭ֮��ļ��
		    		int pcount=cdmtn.getChildCount();//��ȡͼƬ����			    		
				    NodeValue nv=(NodeValue)cdmtn.getUserObject();//��ȡ����ͼƬ					   
				    if(nv.jla==null||nv.jla.length<pcount)
				    {//��һ�μ��ػ������������Ƭ��				    
					    String[] pname=new String[pcount];//ͼƬ��������						    
					    Image[] photo=new Image[pcount];//ͼƬ��������	    
					    jla=new JLabel[pcount];//����ͼƬ��JLabel����
				    	for(int i=0;i<pcount;i++)
					    {					    	
					    	String picName=cdmtn.getChildAt(i).toString();//��ȡͼƬ����
					    	pname[i]=picName;//��ͼƬ���Ƹ�ֵ������					    	
					    	String sql="select photo from photo where photoname='"+picName+"'";
					    	photo[i]=DButil.getPic(sql);//��ȡͼƬImage����						    	
					    	MediaTracker mt=new MediaTracker(MainFrame.this);
					    	mt.addImage(photo[i],1);
					    	try
					    	{
					    		mt.waitForAll();//��ʼ����ͼ��
					    	}
					    	catch(Exception err){err.printStackTrace();}				    	
					    		//ͼƬ����
					    		int pw=photo[i].getWidth(MainFrame.this);//ͼƬʵ�ʿ��
					    		int ph=photo[i].getHeight(MainFrame.this);//ͼƬʵ�ʸ߶�
					    		if(pw>ph)//��ȴ�
					    		{
					photo[i]=photo[i].getScaledInstance(width,width*ph/pw,Image.SCALE_SMOOTH);
					    		}
					    		else//�߶ȴ�
					    		{
					photo[i]=photo[i].getScaledInstance(height*pw/ph,height,Image.SCALE_SMOOTH);
					    		}
					    		jla[i]=new JLabel(new ImageIcon(photo[i]));//�����ź��ͼƬ��ӵ�JLabel��
					    		jla[i].setPreferredSize(new Dimension(width,height));//����JLabel�ĸ߶ȺͿ��
					    		jla[i].setToolTipText(pname[i]);//������Ƭ���������ı��ַ���					    							    		
					    		MouseAdapter ma=new MouseAdapter()//�����������
					    		{
					    			public void mouseClicked(MouseEvent e)
					    			{//���һ��ͼƬ������ͼƬ��ϸ
					    				JLabel tjl=(JLabel)e.getSource();
					    				detailPic(tjl.getToolTipText());
					    			}
					    			public void mouseEntered(MouseEvent e)
					    			{//�������ͼƬ��������ʾ
					    				JLabel tjl=(JLabel)e.getSource();
					    				tjl.setBorder(new MyBorder());
					    			}
					    			public void mouseExited(MouseEvent e)				    			
					    			{//����Ƴ�������Ч����ʧ
					    				JLabel tjl=(JLabel)e.getSource();
					    				tjl.setBorder(null);
					    			}
					    		};
					    	   jla[i].addMouseListener(ma);//ע���¼�������
					    	   jla[i].addMouseMotionListener(ma);//ע���¼�������					    
					           jpb.setValue(90*i/pcount);//���ý�������ʾ
					           jpb.setString(90*i/pcount+"%");//���ý������ַ�����ʾ				    
					    }
						nv.jla=jla;//��ͼƬJLabel���鸳ֵ��nv��ͼƬ��������
				    }
				    else
				    {//û������Ƭ��ӽ���ʱʹ����ǰ���غõ�
				    	jla=nv.jla;
				    }
			//�õ���ǰjspview��Dimension����
			Dimension tempD=jspyview.getViewportBorderBounds().getBounds().getSize();
			int tw=(int)tempD.getWidth();//�õ���ǰjspview�Ŀ��
			int perLine=tw/(span+width);//����ÿ����ʾ���ٷ���Ƭ
			int rowc=jla.length/perLine+((jla.length%perLine==0)?0:1);//������ʾ��Ƭ��Ҫ������
			int th=rowc*(span+height)+span;//������ʾ������ƬʱԤ������ĸ߶�
			jpyview.setPreferredSize(new Dimension(tw,th));//��������jpyview�Ŀ�Ⱥ͸߶�			
			for(int i=0;i<jla.length;i++)
			{		
				jpb.setValue(90+10*i/jla.length);//���ý�������ʾ
				jpb.setString(90+10*i/jla.length+"%");//���ý������ַ�����ʾ
				jpyview.add(jla[i]);//��������Ƭ��ӵ�����������
			}		
			cl.show(jpy,"tpyl");//��ʾͼƬԤ�����
			MainFrame.this.setEnabled(true);
			    	}
			    }.start();//�����߳�
		}	        	
	}
	public void detailPic(String pname)
	{
		String sql="select photo from photo where photoname='"+pname+"'";
		ImageIcon ii=new ImageIcon(DButil.getPic(sql));
		jlDetail.setIcon(ii);//��ʾһ����Ƭ		
		cl.show(jpy,"tpmx");//��Ƭ���������ʾͼƬ��ϸ
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		if(e.getSource()==jrbxm)
		{//���������� ����flagΪtrue			
			this.searchByName=true;System.out.println("����������");
		}
		else if(e.getSource()==jrbbh)
		{//����Ų��ң�����flagΪfalse			
			this.searchByName=false;System.out.println("����Ų���");
		}
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==jbInfo[4])
		{//���ü�����ӷ���ķ���
			this.monitorAddGroupButton();				
		}
		else if(e.getSource()==jbInfo[5])
		{//���ü���ɾ������ķ���
			this.monitorDelGroupButton();
		}		
		else if(e.getSource()==jbInfo[3])
		{//��ͼ���ļ�·��				
			jfcPic.showOpenDialog(this);
			if(jfcPic.getSelectedFile()!=null)
			{
				jtfInfo[7].setText(""+jfcPic.getSelectedFile());
			}
		}
		else if(e.getSource()==jbInfo[6])
		{//����Ƭ·��
			jfcPho.showOpenDialog(this);
			if(jfcPho.getSelectedFile()!=null)
			{
				jtfPhoto.setText(""+jfcPho.getSelectedFile());
			}
		}		
		else if(e.getSource()==jba)
		{//�����ϵ��
			this.isInsert=true;//������ӱ�־Ϊtrue
			this.clearInfo();//�����Ϣ���
			cl.show(jpy,"Info");//��ʾ��Ϣ���
			this.setEditable(true);//������Ϣ����еĿؼ��ɱ༭
		}
		else if(e.getSource()==jbInfo[2])
		{//ɾ����ϵ�˰�ť�ļ���
			this.monitorDelButton();//���ü���ɾ����ť�ķ���
		}		
		else if(e.getSource()==jbInfo[0])
		{//�༭��ť�ļ���
			this.isInsert=false;//�����Ƿ������ϵ�˱�־Ϊfalse
			this.setEditable(true);//������Ϣ���ɱ༭
			this.perNameBefor=jtfInfo[1].getText().trim();//�õ��༭֮ǰ������
			this.perGroupBefor=(String)jcb.getSelectedItem();//�õ��༭֮ǰ�ķ���		
		}
		else if(e.getSource()==jbInfo[1])
		{//���水ť�ļ���
			this.monitorSaveButton();					
		}
		else if(e.getSource()==jbs||e.getSource()==jtfs)
		{//���ң�����ť�������ı������ûس�
			this.monitorSearchButton();
		}		
		else if(e.getSource()==jbInfo[7])
		{//���ü����ϴ���Ƭ��ť�ķ���
			this.monitorUploadButton();
		}
		else if(e.getSource()==jbInfo[8])
		{//ɾ����Ƭ��ť
			this.delPhoNode(jtfPhoto.getText().trim());
		}
		
		
		else if(e.getSource()==exit)
		{//����˳�����ִ�еĶ�������������ȫ�˳�			
			System.exit(0);
		}
		else if(e.getSource()==currently)
		{//��ʾ��ǰ��½���û���Ϣ			
			trayIcon.displayMessage("��Ϣ","��ǰ��½���û�Ϊ"+uname,
											TrayIcon.MessageType.INFO);
		}
		else if(e.getSource()==trayIcon)
		{//˫�����̣���ʾ����			
			this.show(true);
		}
	
	}
	
	/*public static void main(String []args)
	{
		new MainFrame("niml2017");
	}*/
}
class NodeValue
{
	String value;//�ڵ��ַ�������
	int classCode;// 0 ��  1 ����  2 ��ϵ�� 3 ��Ƭ
	JLabel[] jla;//����ͼƬ
	
	public NodeValue(String value,int classCode)
	{//������
		this.value=value;
		this.classCode=classCode;
	}
	public String getValue()
	{//�õ��ڵ��ַ�������
		return this.value;
	}
	public void setValue(String value)
	{//�ڵ����Ʒ����仯ʱ�޸Ľڵ���
		this.value=value;
	}
	@Override
	public String toString()
	{//��дtoString����
		return value;
	}
}
class MyBorder extends AbstractBorder
{//�Զ���߿���
	public void paintBorder(Component c,
                        Graphics g,
                        int x,
                        int y,
                        int width,
                        int height)
    {    
    	g.setColor(Color.white);//���ñ߿���ɫΪ��ɫ
    	g.drawRect(x,y,width-1,height-1);//�����߿�
    	g.drawRect(x+1,y+1,width-3,height-3);//�ڻ����߿����ٻ�һ���߿�
    }
}