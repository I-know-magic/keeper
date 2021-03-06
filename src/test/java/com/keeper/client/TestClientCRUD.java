package com.keeper.client;

import java.util.Date;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.keeper.server.KeeperSimpleServer;

/**
 *@author huangdou
 *@at 2016年12月3日下午2:36:58
 *@version 0.0.1
 */
public class TestClientCRUD {

	static KeeperClient client ;
	
	static String testPath =  "/test1";
	static String testData  = "hello world";
	
	static KeeperSimpleServer server ;
	@BeforeClass 
	public static void createClient(){
		System.out.println("before");
		server = new KeeperSimpleServer("d:\\zktmp\\snap", "d:\\zktmp\\datalog");
		server.startZkServer();
		client = new KeeperClient("127.0.0.1:2181",100000);
	}
	
	@AfterClass
	public static void closeClient(){
		if (client != null){
			client.closeClient();
			client = null;
		}
		if (server!=null){
			server.shutdown();
		}
	}
	
	@Before
	public void before(){
		if (client.exist(testPath)){
			client.deleteRecurse(testPath);
		}
	}
	
	@After
	public void after(){
		if (client.exist(testPath)){
			client.deleteRecurse(testPath);
		}
	}
	
	@Test
	public void testCreate(){
		KeeperClient client;
		client = new KeeperClient("127.0.0.1:2181",100000);
		client.create(testPath, testData.getBytes());
		Assert.assertTrue(testData .equals( new String(client.read(testPath))));
	}
	
	@Test
	public void testCreateStr(){
		String str = "hello World! \\n ****";
		client.createStr(testPath, str,CreateMode.PERSISTENT);
		Assert.assertTrue(str .equals(client.readStr(testPath)));
	}
	
	@Test
	public void testCreateObject(){
		Person person = new Person();
		person.setAge(20);
		person.setJoinDate(new Date());
		person.setJoinTheTeam(true);
		person.setJoinTime(System.currentTimeMillis());
		person.setName("张三");
		client.createObject(testPath, person,CreateMode.PERSISTENT);
		Person p = client.readObject(testPath, Person.class);
		System.out.println(p);
		Assert.assertTrue(person.getName().equals(p.getName()));
		Assert.assertTrue(person.getAge()==p.getAge());
		Assert.assertTrue(person.getJoinDate().getTime()==p.getJoinDate().getTime());
		Assert.assertTrue(person.getJoinTime()==p.getJoinTime());
		Assert.assertTrue(p.isJoinTheTeam());
		
	}
	
	@Test
	public void testRead(){
		client.create(testPath, testData.getBytes());
		Assert.assertTrue(testData .equals( new String(client.read(testPath))));
	}
	
	@Test
	public void testGetChildren(){
		client.createWtihParent("/A/B/C");
		List<String> children = client.getChildren("/A");
		Assert.assertArrayEquals(children.toArray(), new Object[]{"B"});
	}
	
	@Test
	public void testUpdate(){
		client.create(testPath, testData.getBytes());
		client.update(testPath, "A".getBytes());
		Assert.assertEquals("A", new String(client.read(testPath)));
	}
	
	@Test
	public void testDelete(){
		client.create(testPath, testData.getBytes());
		client.delete(testPath);
		Assert.assertTrue(!client.exist(testPath));
	}
}
