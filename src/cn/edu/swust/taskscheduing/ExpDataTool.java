package cn.edu.swust.taskscheduing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ExpDataTool {
	public static void main(String[] args) throws Exception {
		int dataCnt = 10;//产生多少组数据
		int taskNum=5;
		int vmNum=2;
		produceData(dataCnt,taskNum, vmNum, 300000, 3600000, 500, 1500); 
		//get("E:/works/CloudSim/Ant/bin/cn/edu/cn/swust/chahaying/130906011241.json", 0);
	}
	
	public static void produceData(int dataCnt,int taskNum,int vmNum,int miMin,int miMax,int mipMin,int mipMax) throws Exception{
		List<ExpData> list= new ArrayList<ExpData>();
		for(int i=0;i<dataCnt;i++){
			ExpData expdata = new ExpData();
			expdata.init(taskNum, vmNum, miMin, miMax, mipMin, mipMax);
			list.add(expdata);
		}
		JSONArray json = JSONArray.fromObject(list);
		String path = ExpDataTool.class.getResource("").getPath().substring(1); 
	//	String fileName =path+new SimpleDateFormat("yyMMddHHmmss").format(new Date())+".json"; 
		String fileName =path+taskNum+"task-"+vmNum+"vm"+".json"; 
		File file = new File(fileName); 
		PrintWriter pw = new PrintWriter(file); 
		pw.write(json.toString());
		pw.close();
		System.out.println("==============Data Produce Sucess============");
		System.out.println(fileName);
	}
	//
	public static ExpData get(String fileName,int index) throws Exception{
		FileReader reader = new FileReader(fileName);
		BufferedReader re = new BufferedReader(reader);
		String str = re.readLine();  
		JSONArray array = JSONArray.fromObject(str);
		JSONObject obj = array.getJSONObject(index);
		ExpData data = new ExpData(); 
		data.taskNum=obj.getInt("taskNum");
		data.vmNum=obj.getInt("vmNum");
		data.mi =new Integer[data.taskNum];
		data.mip=new Integer[data.vmNum];  
		
		JSONArray miArray = obj.getJSONArray("mi");
		JSONArray mipArray = obj.getJSONArray("mip");
		JSONArray etcArray = (JSONArray) obj.get("etc");
		data.etc =new Float[data.taskNum][data.vmNum];
		for(int i=0;i<data.vmNum;i++){
			data.mip[i]=mipArray.getInt(i);
		}
		
		for(int i=0;i<data.taskNum;i++){
			data.mi[i]=miArray.getInt(i);
			JSONArray etcItem =etcArray.getJSONArray(i);
			for(int j=0;j<data.vmNum;j++){
				data.etc[i][j]=(float) etcItem.getDouble(j);
			}			
		}  
		return data;
	}
}
