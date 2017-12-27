package com.niml.photo;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
/**
 * 数据操作类
 * @author Nimingliang
 *
 */
public class DButil
{
	private static String driver="sun.jdbc.odbc.JdbcOdbcDriver";//声明驱动类字符串
	//声明数据库连接字符串
	private static String url="jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ=db/JavaSwingBeautyPhotoBox.mdb";
	private static Connection con=null;//声明数据库连接对象引用
	private static Statement stat=null;//声明语句对象引用
	private static PreparedStatement psInsert=null;//声明预编译语句对象引用
	private static ResultSet rs=null;//声明结果集对象引用
	public static Connection getConnection()//得到数据库连接的方法
	{
		try
		{
			Class.forName(driver);//加载驱动类
			con=DriverManager.getConnection(url);//得到连接
		}
		catch(Exception e){e.printStackTrace();}
		return con;//返回连接
	}
	public static void closeCon()//关闭数据库连接的方法
	{
		try
		{
			if(rs!=null){rs.close(); rs=null;}//如果结果集不为空关闭结果集并赋值null
			if(stat!=null){stat.close(); stat=null;}//如果语句对象不为空关闭语句对象并赋值null
			if(con!=null){con.close(); con=null;}//如果连接不为空关闭连接并赋值null				
		}
		catch(Exception e){e.printStackTrace();}
	}
	public static boolean check(String user,String pwd)//登陆验证
	{
		boolean flag=false;
		try
		{   
			con=DButil.getConnection();//得到数据库连接
			stat=con.createStatement();//创建语句对象
			System.out.println("select pwd from user where uid='"+user+"'");
			rs=stat.executeQuery("select pwd from user where uid='"+user+"'");
			
			rs.next();
			String spwd=rs.getString(1);//得到密码
			//System.out.println(spwd);
			
			//System.out.println(pwd);
			if(spwd.equals(pwd))
			{
				flag=true;//密码匹配，登陆成功
			}			
		}
		catch(Exception e)
		{	
			flag=false;//有任何异常发生，登陆失败
		}
		finally{DButil.closeCon();}//关闭数据库连接
		return flag;
	}
	public static int update(String sql)//更新数据库
	{
		int count=0;//声明返回值
		try
		{
			con=DButil.getConnection();//得到数据库连接
			stat=con.createStatement();//创建语句对象
			count=stat.executeUpdate(sql);//执行更新		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			count=-1;//更新失败返回值为-1
		}
		finally{DButil.closeCon();}//关闭数据库连接	
		return count;//返回结果
	}
	public static boolean isExist(String sql)//某条记录是否存在
	{
		boolean flag=false;//设置返回值
		try
		{   
			con=DButil.getConnection();//得到数据库连接
			stat=con.createStatement();//创建语句对象
			rs=stat.executeQuery(sql);//执行查询
			if(rs.next())
			{
				flag=true;//存在，设置返回值为true
			}		
		}
		catch(Exception e)
		{
			e.printStackTrace();		
			flag=false;//发生任何异常，置返回结果为false
		}
		finally{DButil.closeCon();}//关闭数据库连接
		return flag;//返回结果
	}
	public static int delUser(String uid)//删除用户
	{
		int count=0;//设置返回值
		Vector<String> vpid=new Vector<String>();//存放pid的集合 一个用户对应多个联系人
		try
		{
			con=DButil.getConnection();//得到数据库连接
			stat=con.createStatement();//创建语句对象
			rs=stat.executeQuery("select pid from lxy where uid='"+uid+"'");//得到每个联系人的ID
			while(rs.next())
			{
				String pid=rs.getString(1);//得到用户下的pid				
				vpid.add(pid);//添加进联系人集合
			}
			stat=con.createStatement();//重新创建语句对象
			for(String s:vpid)
			{//循环删除每个联系人的相册
				stat.executeUpdate("delete from photo where pid='"+s+"'");
			}
			//在联系人lxy表中删除每个联系人			
			count=stat.executeUpdate("delete from lxy where uid='"+uid+"'");
			//在用户表中删除用户
			stat.executeUpdate("delete from user where uid='"+uid+"'");			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{DButil.closeCon();}//关闭数据库连接
		return count;//返回删除了多少个联系人
	}	
	public static Vector<String> getNode(String user,String condition)//根据条件得到节点名称列表
	{//根据登陆用户和条件得到节点名称
		Vector<String> node=new Vector<String>();
		String patternStr=";";//拆分条件的正则式
		String[] scon=condition.split(patternStr);//将条件拆分
		try
		{
			con=getConnection();//得到数据库连接
			stat=con.createStatement();//创建语句对象
			if(scon.length==1&&scon[0].equals("uid"))
			{//得到当前用户下有多少个分组
				rs=stat.executeQuery("select distinct pgroup from lxy where uid='"+user.trim()+"'");
			}
			else if(scon.length==1)
			{//得到当前联系人下相册里照片名列表
				rs=stat.executeQuery("select photoname from photo where pid = "+
				"(select pid from lxy where uid='"+user.trim()+"'and pname='"+scon[0].trim()+"')");
			}
			else if(scon.length==2)
			{//得到分组里的联系人姓名列表 			
				rs=stat.executeQuery("select pname from lxy where uid='"
												+user.trim()+"'and pgroup='"+scon[1].trim()+"'");
			}
			while(rs.next())
			{//组织成Vector返回
			    String s=rs.getString(1);
			    node.add(s);
			} 				
		}
		catch(Exception e)
		{
			e.printStackTrace();//打印异常信息
		}
		finally
		{
			DButil.closeCon();//关闭数据库连接
		}
		return node;//返回结果列表
	}
//**************************分组******************************************
	public static int delGroup(String user,String group)
	{
		int count=0;		
		Vector<String> vpid=new Vector<String>();//一个分组对应多个联系人
		try
		{
			con=getConnection();//得到数据库连接
			stat=con.createStatement();//创建语句对象
			rs=stat.executeQuery("select pid from lxy where pgroup='"+group+"'"
									+"and uid='"+user+"'");//从数据库中搜索到group组里的pid
			while(rs.next())
			{
				String pid=rs.getString(1);//得到用户下的pid循环删除photo表中pid下的照片			
				vpid.add(pid);//添加该分组下联系人名称到集合
			}
			stat=con.createStatement();//创建语句对象
			for(String s:vpid)
			{//循环删除每个联系人的相册
				stat.executeUpdate("delete from photo where pid='"+s+"'");
			}
			//在联系人lxy表中删除每个联系人
			count=stat.executeUpdate("delete from lxy where pgroup='"+group+"'");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return count;//返回删除联系人数目
	}
//****************************联系人**************************************
	public static String insertPerson(String uid,Vector<String> pInfo)
	{
		String isPathNull="isNotNull";//传过来的图像是不是合法，默认不为空
		try{
			con=getConnection();//得到数据库连接
			if(pInfo.get(9).equals("")||pInfo.get(9)==null)
			{//照片路径为空，则不插入图像
				psInsert=con.prepareStatement("insert into lxy(pid,pname,pgender,page,pnumber,"+
	 											"pemail,pgroup,ppostalcode,padress,uid)"+
	 										"values(?,?,?,?,?,?,?,?,?,?)");			
	 		}
			else
			{//照片路径不为空，则插入图像
				psInsert=con.prepareStatement("insert into lxy(pid,pname,pgender,page,pnumber,"+
	 											"pemail,pgroup,ppostalcode,padress,uid,pphoto)"+
	 										"values(?,?,?,?,?,?,?,?,?,?,?)" );
	 			File f=new File(pInfo.get(9));//获取选取的图片文件
	 			byte[] b=new byte[(int)f.length()];//创建存储图片数据的数组
	 			FileInputStream fin=new FileInputStream(f);
				fin.read(b);fin.close();//读取文件存于byte数组中并关闭输入流
	 			psInsert.setBytes(11,b);//设置pphoto参数的数据	 				 			
			}
			for(int i=0;i<9;i++)
			{//设置公共信息
				psInsert.setString(i+1,pInfo.get(i));
			}
			psInsert.setString(10,uid);//所属用户			
			psInsert.execute();psInsert.close();//执行更新并关闭语句
		}
		catch(FileNotFoundException fnfe){isPathNull="isNull";}//图片路径不对
		catch(Exception e){e.printStackTrace();}
		finally{DButil.closeCon();}//关闭数据库连接
		return isPathNull;
	}
	public static String updatePerson(String uid,Vector<String> pInfo){
		String isPathNull="isNotNull";//传过来的path是不是合法
		try{
			con=getConnection();
			if(pInfo.get(9).equals("")||pInfo.get(9)==null)
			{//更新时候，如果照片路径为空，则不更新图像
				psInsert=con.prepareStatement("update lxy set pname=?,pgender=?,page=?,pnumber=?,"+
				"pemail=?,pgroup=?,ppostalcode=?,padress=?,uid=? where pid='"+pInfo.get(0).trim()+"'");
			}
			else
			{//如果照片路径不为空，则更新图像
				psInsert=con.prepareStatement("update lxy set pname=?,pgender=?,page=?,pnumber=?,"+
				"pemail=?,pgroup=?,ppostalcode=?,padress=?,uid=?,pphoto=? where pid='"+pInfo.get(0).trim()+"'");
				File f=new File(pInfo.get(9));//获取选取的图片文件
	 			byte[] b=new byte[(int)f.length()];//创建存储图片数据的数组
	 			FileInputStream fin=new FileInputStream(f);
				fin.read(b);fin.close();//读取文件存于byte数组中并关闭输入流					
	 			psInsert.setBytes(10,b);	 			
			}
			for(int i=1;i<9;i++){//设置公共的信息部分
				psInsert.setString(i,pInfo.get(i));
			}			
			psInsert.setString(9,uid);//所属用户	 			
			psInsert.execute();psInsert.close();//执行更新并关闭语句
		}
		catch(FileNotFoundException fnfe){isPathNull="isNull";}//路径不合法	
		catch(Exception e){e.printStackTrace();}
		finally{DButil.closeCon();}//关闭连接
		return isPathNull;
	}
	public static Vector<String> getPerInfo(String sql)//得到联系人信息
	{
		Vector<String> pInfo=new Vector<String>();
		try
		{
			con=getConnection();//得到数据库连接
			stat=con.createStatement();//创建语句对象
			rs=stat.executeQuery(sql);//执行查询 			
			while(rs.next())
			{	
				for(int i=1;i<10;i++)
				{ 	
			    	pInfo.add(rs.getString(i));//将联系人信息添加到返回向量
				} 			    
			} 					
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{DButil.closeCon();}//关闭数据库连接
		return pInfo;//返回信息集合
	}
	public static Image getPic(String sql)
	{
		Image i=null;//声明Image对象引用
		try
		{
			con=getConnection();//得到数据库连接
			stat=con.createStatement();//创建语句对象
			rs=stat.executeQuery(sql);//执行SQL语句
			while(rs.next())
			{
				byte[] buff=rs.getBytes(1);//得到图像数据
				if(buff!=null)//如果数据存在
				{
					i=(new ImageIcon(buff)).getImage();//转换成ImageIcon对象
				} 				
			}		
		}
		catch(Exception e)
		{
			e.printStackTrace();//打印异常信息
		}
		finally{DButil.closeCon();}//关闭数据库连接
		return i;
	}
//***********************************照片*********************************
	public static int insertPic(String path,String pid)
	{//flag=0表示上传成功 1表示找不到文件 2表示文件已经存在
		int flag=0;
		File f=new File(path);//获取选取的图片文件	
		try
		{
			con=getConnection();//得到数据库连接
			psInsert=con.prepareStatement("insert into photo values(?,?,?)");
			byte[] b=new byte[(int)f.length()];//创建存储照片数据的数组
			FileInputStream fin=new FileInputStream(f);//
			fin.read(b);fin.close();//读取文件存于byte数组中并关闭输入流			
			psInsert.setString(1,pid);//设置此照片所属联系人
			psInsert.setString(2,f.getName());//设置此照片名称
			psInsert.setBytes(3,b);//设置照片数据
			psInsert.executeUpdate();psInsert.close();//执行更新并关闭语句							
		}
		catch(FileNotFoundException fnfe){flag=1;}//找不到照片文件
		catch(SQLException sqle){flag=2;}//文件已经存在
		catch(Exception e){e.printStackTrace();}
		finally{DButil.closeCon();}//关闭数据库连接
		return flag;
	}
	
	public static void main(String[] args)
	{
		System.out.println(DButil.delUser("aa"));
	}
}
