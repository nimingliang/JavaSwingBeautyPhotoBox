package com.niml.photo;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
/**
 * ���ݲ�����
 * @author Nimingliang
 *
 */
public class DButil
{
	private static String driver="sun.jdbc.odbc.JdbcOdbcDriver";//�����������ַ���
	//�������ݿ������ַ���
	private static String url="jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ=db/JavaSwingBeautyPhotoBox.mdb";
	private static Connection con=null;//�������ݿ����Ӷ�������
	private static Statement stat=null;//��������������
	private static PreparedStatement psInsert=null;//����Ԥ��������������
	private static ResultSet rs=null;//�����������������
	public static Connection getConnection()//�õ����ݿ����ӵķ���
	{
		try
		{
			Class.forName(driver);//����������
			con=DriverManager.getConnection(url);//�õ�����
		}
		catch(Exception e){e.printStackTrace();}
		return con;//��������
	}
	public static void closeCon()//�ر����ݿ����ӵķ���
	{
		try
		{
			if(rs!=null){rs.close(); rs=null;}//����������Ϊ�չرս��������ֵnull
			if(stat!=null){stat.close(); stat=null;}//���������Ϊ�չر������󲢸�ֵnull
			if(con!=null){con.close(); con=null;}//������Ӳ�Ϊ�չر����Ӳ���ֵnull				
		}
		catch(Exception e){e.printStackTrace();}
	}
	public static boolean check(String user,String pwd)//��½��֤
	{
		boolean flag=false;
		try
		{   
			con=DButil.getConnection();//�õ����ݿ�����
			stat=con.createStatement();//����������
			System.out.println("select pwd from user where uid='"+user+"'");
			rs=stat.executeQuery("select pwd from user where uid='"+user+"'");
			
			rs.next();
			String spwd=rs.getString(1);//�õ�����
			//System.out.println(spwd);
			
			//System.out.println(pwd);
			if(spwd.equals(pwd))
			{
				flag=true;//����ƥ�䣬��½�ɹ�
			}			
		}
		catch(Exception e)
		{	
			flag=false;//���κ��쳣��������½ʧ��
		}
		finally{DButil.closeCon();}//�ر����ݿ�����
		return flag;
	}
	public static int update(String sql)//�������ݿ�
	{
		int count=0;//��������ֵ
		try
		{
			con=DButil.getConnection();//�õ����ݿ�����
			stat=con.createStatement();//����������
			count=stat.executeUpdate(sql);//ִ�и���		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			count=-1;//����ʧ�ܷ���ֵΪ-1
		}
		finally{DButil.closeCon();}//�ر����ݿ�����	
		return count;//���ؽ��
	}
	public static boolean isExist(String sql)//ĳ����¼�Ƿ����
	{
		boolean flag=false;//���÷���ֵ
		try
		{   
			con=DButil.getConnection();//�õ����ݿ�����
			stat=con.createStatement();//����������
			rs=stat.executeQuery(sql);//ִ�в�ѯ
			if(rs.next())
			{
				flag=true;//���ڣ����÷���ֵΪtrue
			}		
		}
		catch(Exception e)
		{
			e.printStackTrace();		
			flag=false;//�����κ��쳣���÷��ؽ��Ϊfalse
		}
		finally{DButil.closeCon();}//�ر����ݿ�����
		return flag;//���ؽ��
	}
	public static int delUser(String uid)//ɾ���û�
	{
		int count=0;//���÷���ֵ
		Vector<String> vpid=new Vector<String>();//���pid�ļ��� һ���û���Ӧ�����ϵ��
		try
		{
			con=DButil.getConnection();//�õ����ݿ�����
			stat=con.createStatement();//����������
			rs=stat.executeQuery("select pid from lxy where uid='"+uid+"'");//�õ�ÿ����ϵ�˵�ID
			while(rs.next())
			{
				String pid=rs.getString(1);//�õ��û��µ�pid				
				vpid.add(pid);//��ӽ���ϵ�˼���
			}
			stat=con.createStatement();//���´���������
			for(String s:vpid)
			{//ѭ��ɾ��ÿ����ϵ�˵����
				stat.executeUpdate("delete from photo where pid='"+s+"'");
			}
			//����ϵ��lxy����ɾ��ÿ����ϵ��			
			count=stat.executeUpdate("delete from lxy where uid='"+uid+"'");
			//���û�����ɾ���û�
			stat.executeUpdate("delete from user where uid='"+uid+"'");			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{DButil.closeCon();}//�ر����ݿ�����
		return count;//����ɾ���˶��ٸ���ϵ��
	}	
	public static Vector<String> getNode(String user,String condition)//���������õ��ڵ������б�
	{//���ݵ�½�û��������õ��ڵ�����
		Vector<String> node=new Vector<String>();
		String patternStr=";";//�������������ʽ
		String[] scon=condition.split(patternStr);//���������
		try
		{
			con=getConnection();//�õ����ݿ�����
			stat=con.createStatement();//����������
			if(scon.length==1&&scon[0].equals("uid"))
			{//�õ���ǰ�û����ж��ٸ�����
				rs=stat.executeQuery("select distinct pgroup from lxy where uid='"+user.trim()+"'");
			}
			else if(scon.length==1)
			{//�õ���ǰ��ϵ�����������Ƭ���б�
				rs=stat.executeQuery("select photoname from photo where pid = "+
				"(select pid from lxy where uid='"+user.trim()+"'and pname='"+scon[0].trim()+"')");
			}
			else if(scon.length==2)
			{//�õ����������ϵ�������б� 			
				rs=stat.executeQuery("select pname from lxy where uid='"
												+user.trim()+"'and pgroup='"+scon[1].trim()+"'");
			}
			while(rs.next())
			{//��֯��Vector����
			    String s=rs.getString(1);
			    node.add(s);
			} 				
		}
		catch(Exception e)
		{
			e.printStackTrace();//��ӡ�쳣��Ϣ
		}
		finally
		{
			DButil.closeCon();//�ر����ݿ�����
		}
		return node;//���ؽ���б�
	}
//**************************����******************************************
	public static int delGroup(String user,String group)
	{
		int count=0;		
		Vector<String> vpid=new Vector<String>();//һ�������Ӧ�����ϵ��
		try
		{
			con=getConnection();//�õ����ݿ�����
			stat=con.createStatement();//����������
			rs=stat.executeQuery("select pid from lxy where pgroup='"+group+"'"
									+"and uid='"+user+"'");//�����ݿ���������group�����pid
			while(rs.next())
			{
				String pid=rs.getString(1);//�õ��û��µ�pidѭ��ɾ��photo����pid�µ���Ƭ			
				vpid.add(pid);//��Ӹ÷�������ϵ�����Ƶ�����
			}
			stat=con.createStatement();//����������
			for(String s:vpid)
			{//ѭ��ɾ��ÿ����ϵ�˵����
				stat.executeUpdate("delete from photo where pid='"+s+"'");
			}
			//����ϵ��lxy����ɾ��ÿ����ϵ��
			count=stat.executeUpdate("delete from lxy where pgroup='"+group+"'");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return count;//����ɾ����ϵ����Ŀ
	}
//****************************��ϵ��**************************************
	public static String insertPerson(String uid,Vector<String> pInfo)
	{
		String isPathNull="isNotNull";//��������ͼ���ǲ��ǺϷ���Ĭ�ϲ�Ϊ��
		try{
			con=getConnection();//�õ����ݿ�����
			if(pInfo.get(9).equals("")||pInfo.get(9)==null)
			{//��Ƭ·��Ϊ�գ��򲻲���ͼ��
				psInsert=con.prepareStatement("insert into lxy(pid,pname,pgender,page,pnumber,"+
	 											"pemail,pgroup,ppostalcode,padress,uid)"+
	 										"values(?,?,?,?,?,?,?,?,?,?)");			
	 		}
			else
			{//��Ƭ·����Ϊ�գ������ͼ��
				psInsert=con.prepareStatement("insert into lxy(pid,pname,pgender,page,pnumber,"+
	 											"pemail,pgroup,ppostalcode,padress,uid,pphoto)"+
	 										"values(?,?,?,?,?,?,?,?,?,?,?)" );
	 			File f=new File(pInfo.get(9));//��ȡѡȡ��ͼƬ�ļ�
	 			byte[] b=new byte[(int)f.length()];//�����洢ͼƬ���ݵ�����
	 			FileInputStream fin=new FileInputStream(f);
				fin.read(b);fin.close();//��ȡ�ļ�����byte�����в��ر�������
	 			psInsert.setBytes(11,b);//����pphoto����������	 				 			
			}
			for(int i=0;i<9;i++)
			{//���ù�����Ϣ
				psInsert.setString(i+1,pInfo.get(i));
			}
			psInsert.setString(10,uid);//�����û�			
			psInsert.execute();psInsert.close();//ִ�и��²��ر����
		}
		catch(FileNotFoundException fnfe){isPathNull="isNull";}//ͼƬ·������
		catch(Exception e){e.printStackTrace();}
		finally{DButil.closeCon();}//�ر����ݿ�����
		return isPathNull;
	}
	public static String updatePerson(String uid,Vector<String> pInfo){
		String isPathNull="isNotNull";//��������path�ǲ��ǺϷ�
		try{
			con=getConnection();
			if(pInfo.get(9).equals("")||pInfo.get(9)==null)
			{//����ʱ�������Ƭ·��Ϊ�գ��򲻸���ͼ��
				psInsert=con.prepareStatement("update lxy set pname=?,pgender=?,page=?,pnumber=?,"+
				"pemail=?,pgroup=?,ppostalcode=?,padress=?,uid=? where pid='"+pInfo.get(0).trim()+"'");
			}
			else
			{//�����Ƭ·����Ϊ�գ������ͼ��
				psInsert=con.prepareStatement("update lxy set pname=?,pgender=?,page=?,pnumber=?,"+
				"pemail=?,pgroup=?,ppostalcode=?,padress=?,uid=?,pphoto=? where pid='"+pInfo.get(0).trim()+"'");
				File f=new File(pInfo.get(9));//��ȡѡȡ��ͼƬ�ļ�
	 			byte[] b=new byte[(int)f.length()];//�����洢ͼƬ���ݵ�����
	 			FileInputStream fin=new FileInputStream(f);
				fin.read(b);fin.close();//��ȡ�ļ�����byte�����в��ر�������					
	 			psInsert.setBytes(10,b);	 			
			}
			for(int i=1;i<9;i++){//���ù�������Ϣ����
				psInsert.setString(i,pInfo.get(i));
			}			
			psInsert.setString(9,uid);//�����û�	 			
			psInsert.execute();psInsert.close();//ִ�и��²��ر����
		}
		catch(FileNotFoundException fnfe){isPathNull="isNull";}//·�����Ϸ�	
		catch(Exception e){e.printStackTrace();}
		finally{DButil.closeCon();}//�ر�����
		return isPathNull;
	}
	public static Vector<String> getPerInfo(String sql)//�õ���ϵ����Ϣ
	{
		Vector<String> pInfo=new Vector<String>();
		try
		{
			con=getConnection();//�õ����ݿ�����
			stat=con.createStatement();//����������
			rs=stat.executeQuery(sql);//ִ�в�ѯ 			
			while(rs.next())
			{	
				for(int i=1;i<10;i++)
				{ 	
			    	pInfo.add(rs.getString(i));//����ϵ����Ϣ��ӵ���������
				} 			    
			} 					
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{DButil.closeCon();}//�ر����ݿ�����
		return pInfo;//������Ϣ����
	}
	public static Image getPic(String sql)
	{
		Image i=null;//����Image��������
		try
		{
			con=getConnection();//�õ����ݿ�����
			stat=con.createStatement();//����������
			rs=stat.executeQuery(sql);//ִ��SQL���
			while(rs.next())
			{
				byte[] buff=rs.getBytes(1);//�õ�ͼ������
				if(buff!=null)//������ݴ���
				{
					i=(new ImageIcon(buff)).getImage();//ת����ImageIcon����
				} 				
			}		
		}
		catch(Exception e)
		{
			e.printStackTrace();//��ӡ�쳣��Ϣ
		}
		finally{DButil.closeCon();}//�ر����ݿ�����
		return i;
	}
//***********************************��Ƭ*********************************
	public static int insertPic(String path,String pid)
	{//flag=0��ʾ�ϴ��ɹ� 1��ʾ�Ҳ����ļ� 2��ʾ�ļ��Ѿ�����
		int flag=0;
		File f=new File(path);//��ȡѡȡ��ͼƬ�ļ�	
		try
		{
			con=getConnection();//�õ����ݿ�����
			psInsert=con.prepareStatement("insert into photo values(?,?,?)");
			byte[] b=new byte[(int)f.length()];//�����洢��Ƭ���ݵ�����
			FileInputStream fin=new FileInputStream(f);//
			fin.read(b);fin.close();//��ȡ�ļ�����byte�����в��ر�������			
			psInsert.setString(1,pid);//���ô���Ƭ������ϵ��
			psInsert.setString(2,f.getName());//���ô���Ƭ����
			psInsert.setBytes(3,b);//������Ƭ����
			psInsert.executeUpdate();psInsert.close();//ִ�и��²��ر����							
		}
		catch(FileNotFoundException fnfe){flag=1;}//�Ҳ�����Ƭ�ļ�
		catch(SQLException sqle){flag=2;}//�ļ��Ѿ�����
		catch(Exception e){e.printStackTrace();}
		finally{DButil.closeCon();}//�ر����ݿ�����
		return flag;
	}
	
	public static void main(String[] args)
	{
		System.out.println(DButil.delUser("aa"));
	}
}
