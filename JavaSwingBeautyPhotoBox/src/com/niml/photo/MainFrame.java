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
	private String uname=null;//当前用户的名字	
	private String perNameBefor=null;//编辑的时候之前的名字		
	private String perGroupBefor=null;//编辑的时候修改之前的分组			
	private boolean searchByName=true;//true则默认为按姓名查找	
	private boolean isInsert=false;//是否为添加默认为否	
	Image image=Toolkit.getDefaultToolkit().getImage("img/txl.jpg");//得到图标对象
	Icon icon = new ImageIcon(image);	
	private JPanel jps=new JPanel();//界面上半部分的JPanel容器		
	private JButton jba=new JButton("添加");
	private JButton jbs=new JButton("查找");
	private JTextField jtfs=new JTextField();//按给出信息查找联系人信息
	//选择查找方式的单选按钮
	private JRadioButton jrbxm=new JRadioButton("按姓名查找",true);
	private JRadioButton jrbbh=new JRadioButton("按编号查找");
	private ButtonGroup bg=new ButtonGroup();//单选按钮组		
	private JPanel jpbr=new JPanel();//单选按钮面板		
	//界面左下的树 创建树模型 指定节点"联系人"为根节点
	DefaultMutableTreeNode root=
						new DefaultMutableTreeNode(new NodeValue("联系人",0));
	DefaultTreeModel dtm=new DefaultTreeModel(root);
	private JTree jtz=new JTree();//界面下半部分左边的JTree  		
	private JScrollPane jspz=new JScrollPane(jtz);//JTree的滚动条	
	private DefaultTreeCellRenderer dtcr=new DefaultTreeCellRenderer();//树节点的绘制器		
	private JPanel jpy=new JPanel();//界面下半部分右边界面，布局管理器为卡片布局	
	private JPanel jpyInfo=new JPanel();//右侧显示个人信息的面板	
	//界面下半部分右边的JPanel容器的个人信息栏目里的控件	
	private JLabel[] jlInfo={new JLabel("用户编号:"),new JLabel("姓名:"),
							 new JLabel("性别:"),new JLabel("年龄:"),
							 new JLabel("电话号码:"),new JLabel("Email:"),
							 new JLabel("所属组:"),new JLabel("更改照片:"),
							 new JLabel("邮编:"),new JLabel("地址:"),
							 new JLabel("添加相片")};
	private JButton[] jbInfo={new JButton("编辑"),new JButton("保存"),
							  new JButton("删除"),new JButton("浏览"),
							  new JButton("添加分组"),new JButton("删除分组"),
							  new JButton("浏览"),new JButton("上传"),
							  new JButton("删除")};
	//初始默认的一些分组
	private String[] str={"朋友","同事","家庭","重要人士","其他"};
	private JComboBox jcb=new JComboBox(str);//分组下拉列表控件
	private JLabel jlPhoto=new JLabel();//显示图像的JLabel控件
	private JTextField[] jtfInfo=new JTextField[10];	
	private JTextField jtfPhoto=new JTextField();//添加照片到相册的路径	
	private JFileChooser jfcPic=new JFileChooser("f:\\");//上传图像的文件选择器	
	private JFileChooser jfcPho=new JFileChooser("f:\\");//上传照片的文件选择器		
	//性别部分
	private JRadioButton jrbMale=new JRadioButton("男",true);
	private JRadioButton jrbFemale=new JRadioButton("女");
	private ButtonGroup bgGender=new ButtonGroup();	
	private JPanel jpGender=new JPanel();//单选按钮面板	
	private JPanel jpyview=new JPanel();//右侧显示多幅照片的面板	
	private JScrollPane jspyview=new JScrollPane(jpyview);//滚动条	
	private JLabel jlDetail=new JLabel();//右侧显示一幅图片的标签	
	private JScrollPane jspydetail=new JScrollPane(jlDetail);//显示一幅图片标签的滚动条
	private JLabel jlNoPic=new JLabel("没有照片");//没有照片的显示JLabel	
	//图片加载进度条部分
	private JLabel jpProgress=new JLabel();//右侧显示图片加载进度的面板
	private JLabel jlProgress=new JLabel("预览图片加载中.....");
	private JProgressBar jpb=new JProgressBar(JProgressBar.HORIZONTAL,0,100);
	//选中不同树节点时的提示信息部分
	private JLabel jlRoot=new JLabel(icon,JLabel.LEFT);
	private JLabel jlGroup=new JLabel();//分组节点的JLabel
	private CardLayout cl=new CardLayout();//创建卡片布局管理器
	private JLabel[] jla=null;//照片缓冲数组	
	private JSplitPane jspOuter=//上下分割的JSplitPane
						new JSplitPane(JSplitPane.VERTICAL_SPLIT,true);	
	private JSplitPane jspInner=//下面右的JSplitPane
						new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jspz,jpy);
	//系统托盘部分						
	private PopupMenu popup=new PopupMenu();	
	private SystemTray tray;//定义SystemTray成员变量	
	private TrayIcon trayIcon;//定义TrayIcon成员变量
	private MenuItem exit=new MenuItem("退出程序");//定义菜单
	private MenuItem currently=new MenuItem("显示当前用户");//定义菜单
	public MainFrame(String uname)
	{//主类构造器
		this.uname=uname;//设置用户名
		this.initJps();//界面上半部分的搭建		
		this.initInfo();//初始化信息面板	
		this.initJpy();//初始化卡片布局的面板			
		this.initTray();//添加系统托盘
		this.initTree();//初始化树
		jtz.addTreeSelectionListener(//为树节点添加事件监听器
		new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{//重写valueChanged方法
				DefaultMutableTreeNode cdmtn=//得到选中节点对象
				(DefaultMutableTreeNode)e.getPath().getLastPathComponent();
				NodeValue cnv=(NodeValue)cdmtn.getUserObject();//得到自定义节点对象
				if(cnv.classCode==0)
				{//选中节点是根节点时							
					cl.show(jpy,"root");
				}
				else if(cnv.classCode==1)
				{//选中节点是分组节点时															
					String group=cnv.toString();
					jlGroup.setText(group);
					cl.show(jpy,"group");
				}
				else if(cnv.classCode==2)
				{//选中节点是某一联系人节点时							
					String sql="select pid,pname,pgender,page,pnumber,pemail,pgroup,ppostalcode,"+
			"padress from lxy where uid='"+MainFrame.this.uname+"'and pname='"+cnv.toString()+"'";														
					setInfo(DButil.getPerInfo(sql));//从数据库得到此联系人信息并设置到信息面板									
					cl.show(jpy,"Info");
				}
				else if(cnv.classCode==3)
				{//相册预览					
					jpyview.removeAll();//清空相册预览面板
					cl.show(jpy,"tpyl");//显示相册预览面板																		
					viewPic(cdmtn);//预览相册																				
				}
				else if(cnv.classCode==4)
				{//图片明细
					cl.show(jpy,"tpmx");//显示图片明细面板
					NodeValue pnv=//得到选中照片的自定义节点对象
					(NodeValue)((DefaultMutableTreeNode)cdmtn).getUserObject();	
					detailPic(pnv.value);//点击某一张图片
				}
			}
		}
							);									
		jspOuter.setDividerLocation(46);//设置分割窗体JSplitPane的位置
		jspOuter.setTopComponent(jps);//设置窗体上半部分的控件
		jspOuter.setBottomComponent(jspInner);//设置下半部分的控件
		jspInner.setDividerLocation(200);//设置分割窗体JSplitPane的位置
		jspOuter.setDividerSize(0);//设置垂直分割窗体JSplitPane的宽度
		jspInner.setDividerSize(4);//设置水平分割窗体JSplitPane的宽度
		this.add(jspOuter);//将分割窗体添加到主窗体
		//设置窗体关闭按钮执行的动作
		this.addWindowListener(
					new WindowAdapter()
					{
						public void WindowClosing(WindowEvent e)
						{
							//将窗体隐藏
							MainFrame.this.hide();
						}
					}
							    );
		//设置主窗体的图标、标题、大小以及可见性
		Image image=Toolkit.getDefaultToolkit().getImage("img/link.png");//得到图标对象		
		this.setIconImage(image);		
		this.setTitle(uname+"的通讯录");	
		this.setBounds(250,50,700,650);
		this.setVisible(true);
	}
	public void initJps()
	{//界面上半部分的初始化
		jps.setLayout(null);//设置jps布局管理器为null
		//设置按钮大小并添加到JPanel面板里
		jba.setBounds(5,10,80,26);		
		jba.addActionListener(this);//为添加按钮注册事件监听器
		jps.add(jba);//添加按钮到jps面板里
		jbs.setBounds(90,10,80,26);
		jbs.addActionListener(this);//为查找按钮注册事件监听器
		jps.add(jbs);//添加按钮到jps面板里
		//设置jtfs文本框大小并添加到jps面板里
		jtfs.setBounds(175,10,120,26);
		jtfs.addActionListener(this);//为文本框注册事件监听器
		jps.add(jtfs);
		//设置单选按钮大小和位置并添加到jpbr面板里同时添加到bg单选按钮组里
		jrbxm.setBounds(5,3,50,26);
		jrbxm.addItemListener(this);//为单选按钮注册ItemEvent事件监听器
		bg.add(jrbxm);
		jpbr.add(jrbxm);
		jrbbh.setBounds(60,3,50,26);
		jrbbh.addItemListener(this);//为单选按钮注册ItemEvent事件监听器
		bg.add(jrbbh);
		jpbr.add(jrbbh);
		jpbr.setBounds(300,5,200,26);
		jps.add(jpbr);
	}
	public void initTree()
	{//初始化树
		jtz.setModel(dtm);//设置树模型		
		jtz.setExpandsSelectedPaths(true);//设置树ExpandsSelectedPaths属性		
		jtz.setCellRenderer(dtcr);//设置树的节点绘制器		
		ImageIcon icon=new ImageIcon("img/wzk.png");//得到树节点关闭的图标		
		dtcr.setClosedIcon(icon);//设置树节点关闭的图标		
		icon=new ImageIcon("img/zk.png");//得到树节点展开的图标
		dtcr.setOpenIcon(icon);//设置树节点展开的图标
		icon=new ImageIcon("img/mzjd.png");//得到树的叶子节点的图标
		dtcr.setLeafIcon(icon);//设置树的叶子节点的图标
		Vector<String> group=DButil.getNode(uname,"uid");//从数据库得有多少个分组
		for(int i=0;i<group.size();i++)
		{//添加组节点
			String s=group.get(i);			
			DefaultMutableTreeNode dmtnGroup=//创建分组节点对象
							new DefaultMutableTreeNode(new NodeValue(s,1));
			dtm.insertNodeInto(dmtnGroup,root,i);//将分组节点添加到根节点			
			//添加人名节点
			Vector<String> pnode=DButil.getNode(uname,"pname;"+s);		
			for(String person:pnode)
			{
				dmtnGroup.add(this.initPerNode(person));//将各个分组下的联系人节点添加到分组节点
			}					
		}
	}
	public void initJpy()
	{//界面右边为卡片布局的JPanel里一些控件的添加
		jpy.setLayout(cl);
		//设置选中根节点显示信息格式并添加到面板		
		jlRoot.setFont(new Font("Courier",Font.PLAIN,22));
		jlRoot.setHorizontalAlignment(JLabel.CENTER);
		jlRoot.setVerticalAlignment(JLabel.CENTER);
		jpy.add("root",jlRoot);//添加根节点显示信息				
		jpy.add("Info",jpyInfo);//添加联系人信息面板
		//设置选中分组节点显示信息格式并添加到面板
		jlGroup.setFont(new Font("Courier",Font.PLAIN,22));
		jlGroup.setHorizontalAlignment(JLabel.CENTER);
		jlGroup.setVerticalAlignment(JLabel.CENTER);		
		jpy.add("group",jlGroup);//添加分组节点显示信息
		//初始化图片预览界面并添加到面板		
		jpyview.setBackground(Color.black);//设置背景色为黑色
		jpyview.setLayout(new FlowLayout(FlowLayout.LEFT));
		jpy.add("tpyl",jspyview);
		//设置相册没有照片时提示信息格式并添加到面板
		jlNoPic.setFont(new Font("Courier",Font.PLAIN,22));
		jlNoPic.setHorizontalAlignment(JLabel.CENTER);
		jlNoPic.setVerticalAlignment(JLabel.CENTER);
		jpy.add("nopic",jlNoPic);	
		//初始化图片明细界面并添加到面板
		jlDetail.setOpaque(true);
	    jlDetail.setBackground(Color.black);//设置背景色为黑色	
		jlDetail.setVerticalAlignment(JLabel.CENTER);
		jlDetail.setHorizontalAlignment(JLabel.CENTER);
		jpy.add("tpmx",jspydetail);	
		//初始化图片加载进度界面
		jpy.add("tpjd",jpProgress);
		jpProgress.setLayout(null);
		jlProgress.setBounds(20,20,200,30);//设置大小和位置
		jlProgress.setFont(new Font("楷体_GB2312",Font.PLAIN,20));
		jpProgress.add(jlProgress);//添加进度条
		jpb.setBounds(20,50,200,20);//设置大小和位置
		jpProgress.add(jpb);
		jpb.setBorderPainted(true);//设置进度条边框显示
		jpb.setStringPainted(true);//设置进度条字符显示
	}	
	public void initInfo()
	{//初始化信息界面
		jpyInfo.setLayout(null);//设置布局管理器为空
		jpyInfo.setBounds(50,50,480,460);//设置信息面板的大小和位置		
		jlPhoto.setBounds(220,10,150,170);//设置联系人图像JLabel的大小和位置
		jlPhoto.setBorder(BorderFactory.createLineBorder(Color.BLACK));//将JLbel的边框线显现出来
		jpyInfo.add(jlPhoto);//将显示联系人照片的JLabel添加到信息面板
		for(int i=0;i<10;i++)//添加文本输入框，并设置大小和位置
		{
			jlInfo[i].setBounds(20,10+i*30,60,26);
			jpyInfo.add(jlInfo[i]);
		}
		//添加相片部分的控件
		jlInfo[10].setBounds(20,360,60,26);
		jpyInfo.add(jlInfo[10]);		
		jtfPhoto.setBounds(80,360,200,26);//设置得到照片路径的文本框的大小和位置
		jpyInfo.add(jtfPhoto);//将得到照片路径的文本框添加到信息面板
		jbInfo[6].setBounds(285,360,80,26);
		jbInfo[6].addActionListener(this);//为添加照片的浏览按钮注册事件监听器
		jpyInfo.add(jbInfo[6]);
		//设置文件选择器的几种选择文件格式		
		jfcPho.addChoosableFileFilter(new FileNameExtensionFilter("GIF图片文件","gif","GIF"));
		jfcPho.addChoosableFileFilter(new FileNameExtensionFilter("PNG图片文件","png","PNG"));
		jfcPho.addChoosableFileFilter(new FileNameExtensionFilter("JPEG图片文件","jpg","jpeg"));
		for(int i=0;i<10;i++)
		{//初始化一些文本框
			jtfInfo[i]=new JTextField();
		}
		for(int i=1;i<7;i++)
		{//设置一些类似文本框的位置
			if(i!=2&i!=6)
			{
				jtfInfo[i].setBounds(80,10+i*30,135,26);
				jtfInfo[i].addActionListener(this);//为文本框注册事件监听器				
				jpyInfo.add(jtfInfo[i]);//将文本框添加到信息面板				
			}
		}
		//性别部分
		jrbMale.setBounds(5,3,50,26);
		jrbMale.addItemListener(this);		//为单选按钮注册ItemEvent事件监听器		
		bgGender.add(jrbMale);
		jpGender.add(jrbMale);
		jrbFemale.setBounds(60,3,50,26);		
		jrbFemale.addItemListener(this);	//为单选按钮注册ItemEvent事件监听器
		bgGender.add(jrbFemale);
		jpGender.add(jrbFemale);
		jpGender.setBounds(60,70,125,26);
		jpyInfo.add(jpGender);				//将单选按钮的面板jpbr添加到jps里
		//分组
		jcb.setBounds(80,190,75,26);		
		jcb.setEditable(false);//设置分组文本为不可编辑
		this.initGroup();//初始话分组下拉列表框
		jcb.setSelectedIndex(4);//默认选择的是其他分组
		jpyInfo.add(jcb);//将分组下拉列表框添加到信息面板		
		for(int i=0;i<2;i++)//添加分组删除分组按钮
		{
			jbInfo[4+i].setBounds(175+100*i,190,90,26);
			jbInfo[4+i].addActionListener(this);//为按钮注册事件监听器
			jpyInfo.add(jbInfo[4+i]);
		}
		//用户编号
		jtfInfo[0].setBounds(80,10,135,26);
		jpyInfo.add(jtfInfo[0]);
		//更改图像
		jtfInfo[7].setBounds(80,220,200,26);
		jpyInfo.add(jtfInfo[7]);
		jbInfo[3].setBounds(285,220,80,26);
		jbInfo[3].addActionListener(this);//为按钮注册事件监听器
		jpyInfo.add(jbInfo[3]);//将添加图像的浏览按钮添加到信息面板
		//设置文件选择器的几种选择文件格式	
		jfcPic.addChoosableFileFilter(new FileNameExtensionFilter("GIF图片文件","gif","GIF"));
		jfcPic.addChoosableFileFilter(new FileNameExtensionFilter("PNG图片文件","png","PNG"));
		jfcPic.addChoosableFileFilter(new FileNameExtensionFilter("JPEG图片文件","jpg","jpeg"));		
		//邮编文本框的添加
		jtfInfo[8].setBounds(80,250,135,26);
		jpyInfo.add(jtfInfo[8]);
		//地址文本框的添加
		jtfInfo[9].setBounds(80,280,285,26);
		jpyInfo.add(jtfInfo[9]);
		//编辑 保存 删除 等按钮
		for(int i=0;i<3;i++)
		{
			jbInfo[i].setBounds(80+i*100,320,80,26);
			jbInfo[i].addActionListener(this);//为按钮注册事件监听器
			jpyInfo.add(jbInfo[i]);
		}
		for(int i=0;i<2;i++)
		{//上传和删除按钮
			jbInfo[7+i].setBounds(80+i*100,395,80,26);
			jbInfo[7+i].addActionListener(this);//为按钮注册事件监听器
			jpyInfo.add(jbInfo[7+i]);
		}
	}
	public void initGroup()//初始化分组下拉列表
	{
		Vector<String> v=DButil.getNode(uname,"uid");//得到所有分组列表
		boolean b=false;//记录下拉列表中是否存在已有的选项
		for(int i=0;i<v.size();i++)
		{
			for(int j=0;j<jcb.getItemCount();j++)
			{
				if(v.get(i).equals(jcb.getItemAt(j)))
				{
					b=true;	break;//下拉列表框中存在此选项时					
				}			
			}
			if(b==false)
			{//下拉列表框中不存在此选项时 将其添加到分组下拉列表框		
				jcb.addItem(v.get(i));
			}
			else
			{b=false;}//将b置为false 以待下一次循环使用											
		}
	}
	public void initTray()//初始化系统托盘
	{		
		exit.addActionListener(this);//为菜单选项注册监听器
		currently.addActionListener(this);//为菜单选项注册监听器		
		popup.add(currently);//将菜单选项添加到菜单
		popup.add(exit);//将菜单选项添加到菜单		
		if(SystemTray.isSupported())//判断当前系统是否支持系统托盘
		{			
			tray=SystemTray.getSystemTray();//通过静态方法得到系统托盘			
			Image image=Toolkit.getDefaultToolkit().getImage("img/link.png");//加载图像			
			trayIcon=new TrayIcon(image,"我的通讯录",popup);//创建TrayIcon对象得到托盘图标			
			trayIcon.setImageAutoSize(true);//设置托盘图标自动设置尺寸
			try
			{//将托盘图标设置到系统托盘中
				tray.add(trayIcon);				
			}
			catch(AWTException e)
			{
				e.printStackTrace();
			}
			trayIcon.addActionListener(this);//为trayIcon注册事件监听器
		}
	}
	public DefaultMutableTreeNode initPerNode(String person)//生成联系人节点
	{
		DefaultMutableTreeNode dmtnPerson=//根据得到的人名生成一个树节点
				new DefaultMutableTreeNode(new NodeValue(person,2));	
		DefaultMutableTreeNode dmtnPhoto=//在联系人节点下添加相册节点
				new DefaultMutableTreeNode(new NodeValue("相册",3));
		dmtnPerson.add(dmtnPhoto);	
		Vector<String> pphoto=DButil.getNode(uname,person);//得到该用户下该联系人的相册照片名称列表
		for(String photo:pphoto)
		{
			DefaultMutableTreeNode Photo=//生成照片节点
				new DefaultMutableTreeNode(new NodeValue(photo,4));
			dmtnPhoto.add(Photo);//添加到相册节点上
		}	
		return dmtnPerson;//返回生成的节点		
	}
	public void clearInfo()//清空信息面板
	{		
		for(int i=0;i<10;i++)
		{
			jtfInfo[i].setText("");//清空文本框
		}
		jlPhoto.setIcon(null);//清空图像
		jcb.setSelectedItem("其他");//设置分组选择“其它分组”
	}
	public void setInfo(Vector<String> pInfo)//将信息向量设置到信息面板中
	{//将信息向量按规则填到信息面板里
		this.clearInfo();		
		if(pInfo.size()==0)
		{
			JOptionPane.showMessageDialog(this,"所查用户不存在！！！","错误",
											JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			for(int i=0;i<2;i++)
			{//显示联系人编号和姓名
				jtfInfo[i].setText(pInfo.get(i));
			}			
			if(pInfo.get(2).equals("男"))
			{//显示性别
				jrbMale.setSelected(true);
			}
			else
			{//显示性别
				jrbFemale.setSelected(true);
			}
			for(int i=3;i<6;i++)
			{//显示年龄、电话号码和电子邮件
				jtfInfo[i].setText(pInfo.get(i));
			}
			for(int i=7;i<9;i++)
			{//显示邮编和地址
				jtfInfo[i+1].setText(pInfo.get(i));
			}			
			jcb.setSelectedItem(pInfo.get(6));  //设置分组信息
			this.setPic(pInfo.get(0));//设置图像为从数据库得到的图像
			this.setEditable(false);//设置面板不可编辑
		}	
	}
	public void setPic(String pid)//设置个人图像显示
	{
		final int width=150;//显示图像的JLabel的宽度
		final int height=170;//显示图像的JLabel的高度
		String sql="select pphoto from lxy where pid='"+pid+"'";
		Image Pic=DButil.getPic(sql);//从数据库得到此人的图像
		if(Pic!=null)
		{//如果此联系人上传了图像
			int pw=Pic.getWidth(MainFrame.this);//得到此人图像的宽度
			int ph=Pic.getHeight(MainFrame.this);//得到此人图像的高度
			if(pw>ph)
			{//宽度大于高度，图像进行缩放
				Pic=Pic.getScaledInstance(width,width*ph/pw,Image.SCALE_SMOOTH);			
			}
			else
			{//高度大于宽度，图像进行缩放
				Pic=Pic.getScaledInstance(height*pw/ph,height,Image.SCALE_SMOOTH);				
			}
			jlPhoto.setIcon(new ImageIcon(Pic));//将图像显示到JLabel
			jlPhoto.setHorizontalAlignment(JLabel.CENTER);//设置图片水平居中显示
			jlPhoto.setVerticalAlignment(JLabel.CENTER);//设置图片垂直方向居中显示
		}
		else
		{//如果图像为空，则不显示
			jlPhoto.setIcon(null);
		}		
	}
	public void setEditable(boolean Editable)//设置信息窗口是否可编辑
	{
		jrbFemale.setEnabled(Editable);//设置性别是否可编辑
		jrbMale.setEnabled(Editable);//设置性别是否可编辑
		jcb.setEnabled(Editable);//设置下拉列表框是否可编辑		
		for(int i=0;i<10;i++)
		{
			jtfInfo[i].setEditable(Editable);//设置文本框是否可编辑
			if(i>=3&&i<=5)
			{
				jbInfo[i].setEnabled(Editable);//设置部分按钮是否可编辑						
			}			
		}
		jbInfo[1].setEnabled(Editable);//设置保存按钮是否可编辑
	}
	public Vector<String> getInfo()//从信息面板得到用户输入的信息
	{
		Vector<String> pInfo=new Vector<String>();
		pInfo.add(jtfInfo[0].getText().trim());//添加pid
		pInfo.add(jtfInfo[1].getText().trim());//添加pname
		String gender=jrbMale.isSelected()?"男":"女";
		pInfo.add(gender);//添加性别
		pInfo.add(jtfInfo[3].getText().trim());//年龄
		pInfo.add(jtfInfo[4].getText().trim());//电话号码
		pInfo.add(jtfInfo[5].getText().trim());//Email	
		pInfo.add((String)jcb.getSelectedItem());//分组
		pInfo.add(jtfInfo[8].getText().trim());//邮编
		pInfo.add(jtfInfo[9].getText().trim());//地址
		String photoPath=jtfInfo[7].getText().trim();//得到照片路径
		pInfo.add(photoPath);//照片路径		
		return pInfo;
	}
//************************分组管理***********************************************	
	public void monitorAddGroupButton()//监听添加分组按钮
	{
		String group=JOptionPane.showInputDialog(this,"请输入分组:","添加分组",
													JOptionPane.PLAIN_MESSAGE);
		if(group!=null)
		{//group不等于null的情况
			if(group.equals(""))
			{//输入分组是空值
				JOptionPane.showMessageDialog(this,"名称不能为空","错误",
											JOptionPane.WARNING_MESSAGE);
			}
			else
			{//分组名不为空
				int count=jcb.getItemCount();//得到分组下拉选项的个数
				for(int i=0;i<count;i++)
				{
					if(group.equals((String)jcb.getItemAt(i)))
					{//是否有相同的分组
						JOptionPane.showMessageDialog(this,"不能插入相同的组","错误",
												JOptionPane.WARNING_MESSAGE);
						return;//有相同的分组，不添加，直接返回
					}				
				}							
				jcb.addItem(group);//添加分组到下拉列表				
				jcb.setSelectedItem(group);//设置分组选中为刚添加的分组				
				this.addGroupNode(group);//在树上添加分组				
			}
		}
	}
	public void addGroupNode(String group)//在树上添加分组节点
	{
		DefaultMutableTreeNode newGroupNode=//生成一个分组类型的节点
								new DefaultMutableTreeNode(new NodeValue(group,1));
		root.add(newGroupNode);//将节点添加到树的根节点上
		((DefaultTreeModel)jtz.getModel()).reload();//更新树模型
	}
	public void monitorDelGroupButton()//删除分组的监听
	{
		int i=JOptionPane.showConfirmDialog(this,"删除分组将会删除"+
			"分组里的所有联系人！！！是否删除？","确认",JOptionPane.YES_NO_OPTION);
		if(i==JOptionPane.YES_OPTION)
		{//确认了
			String group=(String)jcb.getSelectedItem();
			this.delGroupNode(group);//树上的响应
			int count=DButil.delGroup(uname,group);//数据库里的删除
			jcb.removeItem((String)jcb.getSelectedItem());//下拉列表的删除
			if(count>=0)
			{
				JOptionPane.showMessageDialog(this,"删除分组成功，共删除联系人"+
						count+"个！！！","消息",JOptionPane.INFORMATION_MESSAGE);
			}
			this.clearInfo();//清空信息面板			
		}
	}
	public void delGroupNode(String group)
	{//删除分组节点
		for(int i=0;i<root.getChildCount();i++)
		{
			DefaultMutableTreeNode groupNode=//得到分组节点
						(DefaultMutableTreeNode)root.getChildAt(i);			
			NodeValue groupNv=(NodeValue)groupNode.getUserObject();//转为自定义的对象
			if(group.equals(groupNv.getValue()))
			{//找到要删除的节点
				root.remove(groupNode);//删除此分组节点				
				((DefaultTreeModel)jtz.getModel()).reload(groupNode);//树模型更新
				break;//删除成功，退出循环
			}				
		}
	}
//************************联系人管理*********************************************
	public void monitorDelButton()//监听删除按钮的方法
	{	
		String personName=jtfInfo[1].getText().trim();//得到联系人的名字
		String personGroup=(String)jcb.getSelectedItem();//得到分组名字
		String pid=jtfInfo[0].getText().trim();//得到联系人ID
		//弹出删除对话框
		int index=JOptionPane.showConfirmDialog(this,"是否删除？y/n","确认对话框",
							JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
		if(index==0)
		{//确认删除		
			boolean b=this.delPerNode(personName,personGroup);//更新树列表
			if(b==true)
			{
				String sqla="delete from photo where pid='"+pid+"'";
				String sqlb="delete from lxy where pid='"+pid+"'";
				DButil.update(sqla);//删除联系人的相册中所有照片
				int i=DButil.update(sqlb);//删除联系人信息
				if(i!=-1)
				{//删除成功对话框					
					JOptionPane.showMessageDialog(this,"删除成功","删除",
											JOptionPane.INFORMATION_MESSAGE);
					this.clearInfo();						
				}
			}		
		}
	}	
	public void monitorSaveButton()//监听保存按钮的方法
	{
		String pid=jtfInfo[0].getText().trim();//得到联系人的编号
		String pname=jtfInfo[1].getText().trim();//得到联系人的姓名
		String sqla="select * from lxy where pid='"+pid+"'";//判断此编号是否存在的SQL
		String sqlb="select * from lxy where pname='"+pname+"'";//判断此姓名是否存在的SQL
		boolean isIdExist=DButil.isExist(sqla);//得到编号是否存在
		boolean isNameExist=DButil.isExist(sqlb);//得到姓名是否存在		
		String perGroupAfter=(String)jcb.getSelectedItem();//得到修改之后的分组		
		String perNameAfter=jtfInfo[1].getText().trim();//得到修改后联系人的名字					
		if(!(pid.equals("")||pname.equals("")))
		{
			if(this.isInsert)//是添加的话
			{			
				if(!(isIdExist||isNameExist))
				{					
					//得到插入的图片路径是否合法的判断字符串
					String s=DButil.insertPerson(uname,this.getInfo());
					if(!s.equals("isNull"))
					{//在树上添加节点 此为合法的文件路径
						this.addPerNode(perNameAfter,perGroupAfter);
					}
					else
					{
						JOptionPane.showMessageDialog(this,"图像路径不合法"+
							"添加联系人失败","错误",JOptionPane.WARNING_MESSAGE);
					}							
				}
				else
				{//用户名或者ID已经存在
					JOptionPane.showMessageDialog(this,"联系人ID或者姓名已经存在","错误",
											JOptionPane.WARNING_MESSAGE);
				}
			}
			else
			{//编辑联系人资料
				if(isIdExist)
				{//ID没变进行更新
					String s=DButil.updatePerson(uname,this.getInfo());
					if(!s.equals("isNull"))
					{	
						//先删除以前的联系人节点
						this.delPerNode(this.perNameBefor,this.perGroupBefor);
						//添加新的节点到修改的组
						this.addPerNode(perNameAfter,perGroupAfter);		
					}
					else
					{
						JOptionPane.showMessageDialog(this,"图像路径不合法"+
							"添加联系人失败","错误",JOptionPane.WARNING_MESSAGE);						
					}
				}
				this.perNameBefor=null;//将perNameBefor置空
				this.perGroupBefor=null;//将perGroupBefor置空
				if(!isIdExist)
				{//ID变了
					if(!isNameExist)
					{//名字也变了就执行插入动作
						String s=DButil.insertPerson(uname,this.getInfo());
						if(!s.equals("isNull"))
						{//在树上添加节点 此为合法的文件路径
							this.addPerNode(perNameAfter,perGroupAfter);
						}
						else
						{
							JOptionPane.showMessageDialog(this,"图像路径不合法"+
								"添加联系人失败","错误",JOptionPane.WARNING_MESSAGE);
						}
					}
					else
					{
						DButil.update("delete from lxy where pname='"+perNameAfter+"'");				
						DButil.insertPerson(uname,this.getInfo());//重新插入此人记录						
					}
				}										
			}				
		}
		else
		{//为空
			JOptionPane.showMessageDialog(this,"联系人ID或者姓名不能为空","错误",
									JOptionPane.WARNING_MESSAGE);
			return;				
		}
		this.isInsert=false;this.setEditable(false);		
	}	
	public void monitorSearchButton()//监听查找按钮的方法
	{
		String name=jtfs.getText().trim();
		String sql="";//声明查找字符串
		if(name.equals(""))
		{
			JOptionPane.showMessageDialog(this,"查找条件不能为空！！！",
										"错误",	JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			if(this.searchByName==true)
			{//按姓名查找
	
				sql="select pid,pname,pgender,page,pnumber,pemail,pgroup,ppostalcode,"+
					"padress from lxy where uid='"+uname+"'and pname='"+name+"'";				
				this.setInfo(DButil.getPerInfo(sql));//设置信息面板为该联系人的信息			
			}
			else
			{//按编号查找
				sql="select pid,pname,pgender,page,pnumber,pemail,pgroup,ppostalcode,"+
					"padress from lxy where uid='"+uname+"'and pid='"+name+"'";
				this.setInfo(DButil.getPerInfo(sql));;//设置信息面板为该联系人的信息		
			}
		}
		this.setEditable(false);//设置面板不可编辑
		cl.show(jpy,"Info");//将Info面板显示出来		
	}
	public void addPerNode(String personName,String group)//添加联系人节点
	{
		//添加首先生成联系人节点的一些信息
		boolean flag=false;//此联系人分组在树上是否存在的标志
		DefaultMutableTreeNode perNode=this.initPerNode(personName);//生成联系人节点
		DefaultMutableTreeNode groupNode;//分组节点				
		for(int i=0;i<root.getChildCount();i++)//节点已生成，在树上找到节点所属的父节点
		{//得到分组节点的值
			groupNode=(DefaultMutableTreeNode)root.getChildAt(i);
			NodeValue groupNv=(NodeValue)groupNode.getUserObject();
			if(groupNv.getValue().equals(group))
			{//得到的分组节点的值等于此人所属分组，挂接节点
				groupNode.add(perNode);//将联系人节点添加到此分组节点
				((DefaultTreeModel)jtz.getModel()).reload(groupNode);//更新树模型
				flag=true; //树上存在此分组节点置标志位为true
				break;//退出循环
			}
		}
		if(flag==false)
		{//分组节点在树中不存在
			DefaultMutableTreeNode dmtGroup=//生成新的分组节点
							new DefaultMutableTreeNode(new NodeValue(group,1));
			dmtGroup.add(perNode);//新建一个分组节点，将其添加到新建的分组上
			root.add(dmtGroup);//将其挂到树上
			((DefaultTreeModel)jtz.getModel()).reload(root);//更新树模型			    	
		}		
	}
	public boolean delPerNode(String personName,String group)//删除联系人节点
	{
		boolean flag=false;  //是否删除成功	
		DefaultMutableTreeNode groupNode;//声明分组节点
		DefaultMutableTreeNode personNode;//声明联系人节点
		for(int i=0;i<root.getChildCount();i++)
		{//循环得到要删除联系人的分组节点
			groupNode=(DefaultMutableTreeNode)root.getChildAt(i);
			NodeValue groupNv=(NodeValue)groupNode.getUserObject();
			if(groupNv.getValue().equals(group))
			{
				for(int j=0;j<groupNode.getChildCount();j++)
				{//得到分组节点下联系人节点
					personNode=(DefaultMutableTreeNode)groupNode.getChildAt(j);
					NodeValue personNv=(NodeValue)personNode.getUserObject();
					if(personNv.getValue().equals(personName))
					{//找到此人节点 现在从此节点得到相册节点，然后添加到相册节点一个照片节点
						personNode.removeFromParent();
						((DefaultTreeModel)jtz.getModel()).reload(groupNode);
						flag=true;//删除成功
						break;//退出循环
					}
				}
				break;//退出循环
			}
		}
		if(flag==false)
		{//删除失败，弹出提示消息
			JOptionPane.showMessageDialog(this,"此分组下没有这个人！！！",
											"错误",JOptionPane.WARNING_MESSAGE);	
		}
		return flag;
	}
//***************************照片管理*******************************************	
	public void monitorUploadButton()//监听上传照片的方法
	{	
		String path=jtfPhoto.getText();//得到照片路径
		if(path.equals("")||jtfInfo[0].getText().equals(""))
		{//路径为空或者编号为空
			JOptionPane.showMessageDialog(this,"路径或者联系人编号不得为空！！！","错误",
										JOptionPane.WARNING_MESSAGE);
		}
		else
		{//路径和编号均不为空
			int i=DButil.insertPic(path,jtfInfo[0].getText());
			switch(i)
			{
				case 0://上传成功，更新照片节点
				JOptionPane.showMessageDialog(this,"上传成功","恭喜你！！！",
										JOptionPane.PLAIN_MESSAGE);
				this.addPhoNode(path);
										break;
				case 1://路径错误
				JOptionPane.showMessageDialog(this,"系统找不到文件！！！",
										"错误",JOptionPane.WARNING_MESSAGE);
										break;
				case 2://该文件名已存在
				JOptionPane.showMessageDialog(this,"照片文件已存在或者没有"+
				"此联系人！！！请先添加联系人","错误",JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	public void addPhoNode(String photoPath)//在树上添加照片节点
	{
		String photoName=(new File(photoPath)).getName();	//得到相片的名字
		String personName=jtfInfo[1].getText().trim();//得到联系人的名字
		String group=(String)jcb.getSelectedItem();	//得到分组的名字
		DefaultMutableTreeNode groupNode;//声明分组节点
		DefaultMutableTreeNode personNode;//声明联系人节点
		DefaultMutableTreeNode photoNode;//声明照片节点
		for(int i=0;i<root.getChildCount();i++)
		{//循环得到添加照片联系人的分组节点
			groupNode=(DefaultMutableTreeNode)root.getChildAt(i);
			NodeValue groupNv=(NodeValue)groupNode.getUserObject();
			if(groupNv.getValue().equals(group))
			{//得到此联系人的分组				
				for(int j=0;j<groupNode.getChildCount();j++)
				{//得到分组节点下的添加照片联系人的节点
					personNode=(DefaultMutableTreeNode)groupNode.getChildAt(j);
					NodeValue personNv=(NodeValue)personNode.getUserObject();
					if(personNv.getValue().equals(personName))
					{//找到此人节点 现在从此节点得到相册节点						
						photoNode=(DefaultMutableTreeNode)personNode.getChildAt(0);
						photoNode.add(new DefaultMutableTreeNode(new NodeValue(photoName,4)));
						//通知树模型树已改变
						((DefaultTreeModel)jtz.getModel()).reload(photoNode);												
						break;//退出内层循环
					}
				}
			break;//退出外层循环
			}
		}
	}
	public void delPhoNode(String phoName)
	{
		boolean isDel=false;
		if(phoName.equals("")||phoName==null)
		{//名称为空则弹出错误消息
			JOptionPane.showMessageDialog(this,"照片名称不对，请在添加照片文本框里"+
							"输入照片名称！！！","错误",JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			TreePath tp=jtz.getSelectionPath();//得到当前路径			
			try
			{
				DefaultMutableTreeNode perNode=//得到当前路径选中的节点
							(DefaultMutableTreeNode)tp.getLastPathComponent();			
				DefaultMutableTreeNode xc=//得到选中联系人下面的相册节点
								(DefaultMutableTreeNode)perNode.getFirstChild();
				for(int i=0;i<xc.getChildCount();i++)
				{					
					DefaultMutableTreeNode phoNode=//得到相册下的照片节点
								(DefaultMutableTreeNode)xc.getChildAt(i);
					NodeValue zpmc=(NodeValue)phoNode.getUserObject();//得到自定义节点对象				
					if(zpmc.getValue().equals(phoName))
					{
						isDel=true;//是否删除设置为true						
						DButil.update("delete from photo where photoname='"+phoName+"'");//更新
												
						xc.remove(i);//从相册中删除这个节点
						((DefaultTreeModel)jtz.getModel()).reload(xc);//更新树模型
						jtfPhoto.setText("");//清空文本框里的相片名称
						return;
					}
				}
			}
			catch(NullPointerException npe)
			{
				JOptionPane.showMessageDialog(this,"必须在选择该联系人而删除照片！！！","错误",
											JOptionPane.WARNING_MESSAGE);
				jtfPhoto.setText("");//清空添加照片文本框
			}
			if(!isDel)
			{
				JOptionPane.showMessageDialog(this,"此人没有这张照片","错误",
											JOptionPane.WARNING_MESSAGE);
				jtfPhoto.setText("");//清空添加照片文本框
			}			
		}		
	}
//*******************************照片预览和照片明细********************************
	public void viewPic(final DefaultMutableTreeNode cdmtn) //图片预览
	{		
		if(cdmtn.getChildCount()==0)
		{//相册下没有照片
			cl.show(jpy,"nopic");
		}
		else
		{
			cl.show(jpy,"tpjd");//显示加载进度条
			this.setEnabled(false);//加载图片时禁用树形列表
		    new Thread()//新建一线程加载图片并启动此县城
		    {
		    	public void run()//实现run方法
		    	{			    		
		    		final int width=160;//定义预览照片的宽度
		    		final int height=160;//定义预览照片的高度
		    		final int span=10;//定义照片之间的间隔
		    		int pcount=cdmtn.getChildCount();//获取图片个数			    		
				    NodeValue nv=(NodeValue)cdmtn.getUserObject();//获取所有图片					   
				    if(nv.jla==null||nv.jla.length<pcount)
				    {//第一次加载或者添加了新照片后				    
					    String[] pname=new String[pcount];//图片名称数据						    
					    Image[] photo=new Image[pcount];//图片对象数组	    
					    jla=new JLabel[pcount];//放置图片的JLabel数组
				    	for(int i=0;i<pcount;i++)
					    {					    	
					    	String picName=cdmtn.getChildAt(i).toString();//获取图片名称
					    	pname[i]=picName;//将图片名称赋值给数组					    	
					    	String sql="select photo from photo where photoname='"+picName+"'";
					    	photo[i]=DButil.getPic(sql);//获取图片Image对象						    	
					    	MediaTracker mt=new MediaTracker(MainFrame.this);
					    	mt.addImage(photo[i],1);
					    	try
					    	{
					    		mt.waitForAll();//开始加载图像
					    	}
					    	catch(Exception err){err.printStackTrace();}				    	
					    		//图片缩放
					    		int pw=photo[i].getWidth(MainFrame.this);//图片实际宽度
					    		int ph=photo[i].getHeight(MainFrame.this);//图片实际高度
					    		if(pw>ph)//宽度大
					    		{
					photo[i]=photo[i].getScaledInstance(width,width*ph/pw,Image.SCALE_SMOOTH);
					    		}
					    		else//高度大
					    		{
					photo[i]=photo[i].getScaledInstance(height*pw/ph,height,Image.SCALE_SMOOTH);
					    		}
					    		jla[i]=new JLabel(new ImageIcon(photo[i]));//将缩放后的图片添加到JLabel中
					    		jla[i].setPreferredSize(new Dimension(width,height));//设置JLabel的高度和宽度
					    		jla[i].setToolTipText(pname[i]);//设置照片弹出气球文本字符串					    							    		
					    		MouseAdapter ma=new MouseAdapter()//添加鼠标监听器
					    		{
					    			public void mouseClicked(MouseEvent e)
					    			{//点击一张图片，就是图片明细
					    				JLabel tjl=(JLabel)e.getSource();
					    				detailPic(tjl.getToolTipText());
					    			}
					    			public void mouseEntered(MouseEvent e)
					    			{//鼠标移上图片，高亮显示
					    				JLabel tjl=(JLabel)e.getSource();
					    				tjl.setBorder(new MyBorder());
					    			}
					    			public void mouseExited(MouseEvent e)				    			
					    			{//鼠标移出，高亮效果消失
					    				JLabel tjl=(JLabel)e.getSource();
					    				tjl.setBorder(null);
					    			}
					    		};
					    	   jla[i].addMouseListener(ma);//注册事件监听器
					    	   jla[i].addMouseMotionListener(ma);//注册事件监听器					    
					           jpb.setValue(90*i/pcount);//设置进度条显示
					           jpb.setString(90*i/pcount+"%");//设置进度条字符串显示				    
					    }
						nv.jla=jla;//将图片JLabel数组赋值给nv的图片缓冲数组
				    }
				    else
				    {//没有新照片添加进来时使用以前加载好的
				    	jla=nv.jla;
				    }
			//得到当前jspview的Dimension对象
			Dimension tempD=jspyview.getViewportBorderBounds().getBounds().getSize();
			int tw=(int)tempD.getWidth();//得到当前jspview的宽度
			int perLine=tw/(span+width);//计算每行显示多少幅照片
			int rowc=jla.length/perLine+((jla.length%perLine==0)?0:1);//计算显示照片需要多少行
			int th=rowc*(span+height)+span;//计算显示所有照片时预览窗体的高度
			jpyview.setPreferredSize(new Dimension(tw,th));//重新设置jpyview的宽度和高度			
			for(int i=0;i<jla.length;i++)
			{		
				jpb.setValue(90+10*i/jla.length);//设置进度条显示
				jpb.setString(90+10*i/jla.length+"%");//设置进度条字符串显示
				jpyview.add(jla[i]);//将所有照片添加到滚动窗体中
			}		
			cl.show(jpy,"tpyl");//显示图片预览面板
			MainFrame.this.setEnabled(true);
			    	}
			    }.start();//启动线程
		}	        	
	}
	public void detailPic(String pname)
	{
		String sql="select photo from photo where photoname='"+pname+"'";
		ImageIcon ii=new ImageIcon(DButil.getPic(sql));
		jlDetail.setIcon(ii);//显示一张照片		
		cl.show(jpy,"tpmx");//卡片布局面板显示图片明细
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		if(e.getSource()==jrbxm)
		{//按姓名查找 设置flag为true			
			this.searchByName=true;System.out.println("按姓名查找");
		}
		else if(e.getSource()==jrbbh)
		{//按编号查找，设置flag为false			
			this.searchByName=false;System.out.println("按编号查找");
		}
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==jbInfo[4])
		{//调用监听添加分组的方法
			this.monitorAddGroupButton();				
		}
		else if(e.getSource()==jbInfo[5])
		{//调用监听删除分组的方法
			this.monitorDelGroupButton();
		}		
		else if(e.getSource()==jbInfo[3])
		{//打开图像文件路径				
			jfcPic.showOpenDialog(this);
			if(jfcPic.getSelectedFile()!=null)
			{
				jtfInfo[7].setText(""+jfcPic.getSelectedFile());
			}
		}
		else if(e.getSource()==jbInfo[6])
		{//打开照片路径
			jfcPho.showOpenDialog(this);
			if(jfcPho.getSelectedFile()!=null)
			{
				jtfPhoto.setText(""+jfcPho.getSelectedFile());
			}
		}		
		else if(e.getSource()==jba)
		{//添加联系人
			this.isInsert=true;//设置添加标志为true
			this.clearInfo();//清空信息面板
			cl.show(jpy,"Info");//显示信息面板
			this.setEditable(true);//设置信息面板中的控件可编辑
		}
		else if(e.getSource()==jbInfo[2])
		{//删除联系人按钮的监听
			this.monitorDelButton();//调用监听删除按钮的方法
		}		
		else if(e.getSource()==jbInfo[0])
		{//编辑按钮的监听
			this.isInsert=false;//设置是否添加联系人标志为false
			this.setEditable(true);//设置信息面板可编辑
			this.perNameBefor=jtfInfo[1].getText().trim();//得到编辑之前的名字
			this.perGroupBefor=(String)jcb.getSelectedItem();//得到编辑之前的分组		
		}
		else if(e.getSource()==jbInfo[1])
		{//保存按钮的监听
			this.monitorSaveButton();					
		}
		else if(e.getSource()==jbs||e.getSource()==jtfs)
		{//查找，按按钮或者在文本框里敲回车
			this.monitorSearchButton();
		}		
		else if(e.getSource()==jbInfo[7])
		{//调用监听上传照片按钮的方法
			this.monitorUploadButton();
		}
		else if(e.getSource()==jbInfo[8])
		{//删除照片按钮
			this.delPhoNode(jtfPhoto.getText().trim());
		}
		
		
		else if(e.getSource()==exit)
		{//点击退出程序执行的动作，结束程序安全退出			
			System.exit(0);
		}
		else if(e.getSource()==currently)
		{//显示当前登陆的用户信息			
			trayIcon.displayMessage("信息","当前登陆的用户为"+uname,
											TrayIcon.MessageType.INFO);
		}
		else if(e.getSource()==trayIcon)
		{//双击托盘，显示窗口			
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
	String value;//节点字符串名称
	int classCode;// 0 根  1 分组  2 联系人 3 照片
	JLabel[] jla;//缓存图片
	
	public NodeValue(String value,int classCode)
	{//构造器
		this.value=value;
		this.classCode=classCode;
	}
	public String getValue()
	{//得到节点字符串名称
		return this.value;
	}
	public void setValue(String value)
	{//节点名称发生变化时修改节点名
		this.value=value;
	}
	@Override
	public String toString()
	{//重写toString方法
		return value;
	}
}
class MyBorder extends AbstractBorder
{//自定义边框类
	public void paintBorder(Component c,
                        Graphics g,
                        int x,
                        int y,
                        int width,
                        int height)
    {    
    	g.setColor(Color.white);//设置边框颜色为白色
    	g.drawRect(x,y,width-1,height-1);//画出边框
    	g.drawRect(x+1,y+1,width-3,height-3);//在画出边框里再画一个边框
    }
}