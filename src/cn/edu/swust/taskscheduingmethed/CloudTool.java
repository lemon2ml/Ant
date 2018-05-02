package cn.edu.swust.taskscheduingmethed;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;

public class CloudTool {
	//创建任务
	public static List<Cloudlet> createCloudlet(int userId, Integer[]mi ){
		// Creates a container to store Cloudlets
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>(); 
		long fileSize = 10;
		long outputSize = 10;
		int pesNumber = 1;
		UtilizationModel utilizationModel = new UtilizationModelFull();
		int cloudlets = mi.length;
		Cloudlet[] cloudlet = new Cloudlet[cloudlets];

		for(int i=0;i<cloudlets;i++){
			cloudlet[i] = new Cloudlet(i, mi[i], pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			// setting the owner of these Cloudlets
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);
		} 
		return list;
	}
	//创建虚拟机
	public static List<Vm> createVM(int userId, Integer[] mip) {
		//Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>(); 
		//VM Parameters
		long size = 10000; //image size (MB)
		int ram = 128; //vm memory (MB)
		//int mips = 250;
		long bw = 100;
		int pesNumber = 1; //number of cpus
		String vmm = "Xen"; //VMM name

		//create VMs
		int vms = mip.length;
		Vm[] vm = new Vm[vms];

		for(int i=0;i<vms;i++){
			vm[i] = new Vm(i, userId, mip[i], pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
			list.add(vm[i]);
		}

		return list;
	}
	//输出结果
	public static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time" + indent
				+ "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		double  actualCPUTimeTotal=0;
		double finishTimeMax =0;
		double waitTimeMax =0;
		double wateTimeTotle =0;
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");
				if(cloudlet.getFinishTime()>finishTimeMax){
					finishTimeMax=cloudlet.getFinishTime();
				}
				if(cloudlet.getExecStartTime()>waitTimeMax){
					waitTimeMax=cloudlet.getExecStartTime();
				}
				actualCPUTimeTotal+=cloudlet.getActualCPUTime();
				wateTimeTotle+= cloudlet.getExecStartTime();
				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
		Log.printLine("Max of finishTime :"+dft.format(finishTimeMax));
		Log.printLine("==========total: "+dft.format(actualCPUTimeTotal));
		Log.printLine("Max of wait :"+dft.format(waitTimeMax));
		//Log.printLine("totle of wait :"+dft.format(wateTimeTotle));
		Log.printLine("avge of wait :"+dft.format(wateTimeTotle/size));
		Log.printLine("==========   ==========");
	}
	
}
