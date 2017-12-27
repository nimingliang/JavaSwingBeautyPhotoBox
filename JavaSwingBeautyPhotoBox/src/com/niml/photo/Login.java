package com.niml.photo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
/**
 * 登录界面
 * @author Nimingliang
 *
 */
public class Login extends JFrame implements ActionListener
{
	private JFrame frame;
	private JButton jb1;
	private JLabel jlerror = new JLabel();
	private JTextField jtf=new JTextField();//填写用户名的文本框
	private JPasswordField jpf=new JPasswordField();//填写密码的文本框
	public Login()
	{
		JDialog.setDefaultLookAndFeelDecorated(true);
		try {
			BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			UIManager.put("RootPane.setupButtonVisible", false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		frame = new JFrame("photo-登录");
		
		//设置窗体的图标
		Image icon=Toolkit.getDefaultToolkit().getImage("img/ico.gif");
		frame.setIconImage(icon);
		
		frame.setBounds(400, 200, 516, 367);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		//jp.setLayout(null);//设置布局管理器为空布局
		JLabel jp1=new JLabel("用户名:");//创建面板容器
		jp1.setFont(new Font("华文隶书", Font.PLAIN, 24));
		jp1.setBounds(60, 50, 95, 24);
		frame.getContentPane().add(jp1);
		jtf.setBounds(150, 50, 200, 24);
		frame.getContentPane().add(jtf);
		jtf.addActionListener(this);
		
		JLabel jp2=new JLabel("密码:");//创建面板容器
		jp2.setFont(new Font("华文隶书", Font.PLAIN, 24));
		jp2.setBounds(60, 100, 95, 24);
		frame.getContentPane().add(jp2);
		jpf.setBounds(150, 100, 200, 24);
		frame.getContentPane().add(jpf);
		jpf.addActionListener(this);
		
		jb1 = new JButton("登录");
		jb1.setBounds(185, 150, 60, 24);
		frame.getContentPane().add(jb1);
		jb1.addActionListener(this);//为按钮注册动作事件监听器

		jlerror.setFont(new Font("楷书", Font.HANGING_BASELINE, 12));
		jlerror.setBounds(185, 200, 300, 12);
		frame.getContentPane().add(jlerror);
		
	}
	
	@Override//声明此方法为重写方法
	public void actionPerformed(ActionEvent e)
	{/*实现登陆窗体业务功能的方法*/
		//得到用户名和密码
		String user=jtf.getText().trim();
		String pwd=String.valueOf(jpf.getPassword());
		String sql="";//声明SQL语句
		if(e.getSource()==jtf)
		{//事件源为文本框 切换输入焦点到密码框
			jpf.requestFocus();
		}
		else if(e.getSource()==jb1||e.getSource()==jpf)
		{//判断用户名和密码是否匹配  查询数据库		
			if(DButil.check(user,pwd))
			{//登陆成功
				MainFrame mf=new MainFrame(jtf.getText()); //登陆进主窗体					
				frame.dispose();//释放登陆窗体
			}
			else
			{//登陆失败
				jlerror.setText("对不起，非法的用户名和密码！！！");
				//清空输入框的信息;并把输入焦点到用户名框
				jtf.setText("");
				jpf.setText("");
				jtf.requestFocus();
			}
		}	
	} 
	public static void main(String []args)
	{
		//new Login();//创建登陆窗体
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
