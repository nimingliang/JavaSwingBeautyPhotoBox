package com.niml.photo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
/**
 * ��¼����
 * @author Nimingliang
 *
 */
public class Login extends JFrame implements ActionListener
{
	private JFrame frame;
	private JButton jb1;
	private JLabel jlerror = new JLabel();
	private JTextField jtf=new JTextField();//��д�û������ı���
	private JPasswordField jpf=new JPasswordField();//��д������ı���
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
		frame = new JFrame("photo-��¼");
		
		//���ô����ͼ��
		Image icon=Toolkit.getDefaultToolkit().getImage("img/ico.gif");
		frame.setIconImage(icon);
		
		frame.setBounds(400, 200, 516, 367);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		//jp.setLayout(null);//���ò��ֹ�����Ϊ�ղ���
		JLabel jp1=new JLabel("�û���:");//�����������
		jp1.setFont(new Font("��������", Font.PLAIN, 24));
		jp1.setBounds(60, 50, 95, 24);
		frame.getContentPane().add(jp1);
		jtf.setBounds(150, 50, 200, 24);
		frame.getContentPane().add(jtf);
		jtf.addActionListener(this);
		
		JLabel jp2=new JLabel("����:");//�����������
		jp2.setFont(new Font("��������", Font.PLAIN, 24));
		jp2.setBounds(60, 100, 95, 24);
		frame.getContentPane().add(jp2);
		jpf.setBounds(150, 100, 200, 24);
		frame.getContentPane().add(jpf);
		jpf.addActionListener(this);
		
		jb1 = new JButton("��¼");
		jb1.setBounds(185, 150, 60, 24);
		frame.getContentPane().add(jb1);
		jb1.addActionListener(this);//Ϊ��ťע�ᶯ���¼�������

		jlerror.setFont(new Font("����", Font.HANGING_BASELINE, 12));
		jlerror.setBounds(185, 200, 300, 12);
		frame.getContentPane().add(jlerror);
		
	}
	
	@Override//�����˷���Ϊ��д����
	public void actionPerformed(ActionEvent e)
	{/*ʵ�ֵ�½����ҵ���ܵķ���*/
		//�õ��û���������
		String user=jtf.getText().trim();
		String pwd=String.valueOf(jpf.getPassword());
		String sql="";//����SQL���
		if(e.getSource()==jtf)
		{//�¼�ԴΪ�ı��� �л����뽹�㵽�����
			jpf.requestFocus();
		}
		else if(e.getSource()==jb1||e.getSource()==jpf)
		{//�ж��û����������Ƿ�ƥ��  ��ѯ���ݿ�		
			if(DButil.check(user,pwd))
			{//��½�ɹ�
				MainFrame mf=new MainFrame(jtf.getText()); //��½��������					
				frame.dispose();//�ͷŵ�½����
			}
			else
			{//��½ʧ��
				jlerror.setText("�Բ��𣬷Ƿ����û��������룡����");
				//�����������Ϣ;�������뽹�㵽�û�����
				jtf.setText("");
				jpf.setText("");
				jtf.requestFocus();
			}
		}	
	} 
	public static void main(String []args)
	{
		//new Login();//������½����
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
