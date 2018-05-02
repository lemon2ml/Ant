package cn.edu.swust.taskscheduing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import cn.edu.swust.taskscheduingmethed.CloudTool;
import cn.edu.swust.taskscheduingmethed.MCT;


public class MinMin {
	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;
 
	/** The vmlist. */
	private static List<Vm> vmlist;
	public static void main(String[] args) throws Exception {
		 
		//ExpData expData= ExpDataTool.get("E:/Cloudsim使用/任务调度/25task-5vm.json", 9);
		//ExpData expData= ExpDataTool.get("E:/Cloudsim使用/任务调度/50task-5vm.json", 9 );

		ExpData expData= ExpDataTool.get("E:/Cloudsim使用/任务调度/100task-5vm.json",9);
		int num_user = 1;   // number of cloud users
    	Calendar calendar = Calendar.getInstance();
    	boolean trace_flag = false;  // mean trace events 
    	CloudSim.init(num_user, calendar, trace_flag);
 
    	@SuppressWarnings("unused")
		Datacenter datacenter0 = createDatacenter("Datacenter_0");
 
    	DatacenterBroker broker = createBroker();
    	int brokerId = broker.getId();    	

    	vmlist=CloudTool.createVM(brokerId, expData.mip);
    	cloudletList=CloudTool.createCloudlet(brokerId, expData.mi);
    	 
    	List cloudlets2 =new ArrayList(); 
    	cloudlets2.addAll(cloudletList);
    	
    	
    	List<MCT> seqExcute= new ArrayList<MCT>();
    	//int vmCount = 0;
    	int cloudletsCont = expData.taskNum; 
    	float[] costVM = new float[expData.vmNum];
	//	List vmList=null;
    	while(cloudletsCont>0){ 
    		
    		float minCostVm = Float.MAX_VALUE;
			int minCostVmIndex =0;
			for(int k = 0; k<costVM.length;k++){
				if(costVM[k]<minCostVm){
					minCostVm = costVM[k];
					minCostVmIndex= k;
				}
			}     	
    		float min=Float.MAX_VALUE;
    		int taskIndex=0;
    		for(int i=0;i<expData.taskNum;i++){
    			if(cloudlets2.get(i)==null)continue;
    			if(expData.etc[i][minCostVmIndex]<min){
    				min =expData.etc[i][minCostVmIndex];
    				taskIndex=i;
    			} 
        	} 
    		costVM[minCostVmIndex]+=min;
    		cloudlets2.set(taskIndex, null);
    		cloudletsCont--;
    		seqExcute.add(new MCT(taskIndex,minCostVmIndex,min));
    	}
        broker.submitVmList(vmlist);
	 	broker.submitCloudletList(cloudletList);
    	for(MCT mct:seqExcute){
    		broker.bindCloudletToVm(cloudletList.get(mct.taskIndex).getCloudletId(), vmlist.get(mct.vmIndex).getId());
    		System.out.println(mct.taskIndex+"--"+mct.vmIndex);
    	} 
		
		CloudSim.startSimulation();

		// Final step: Print results when simulation is over
		List<Cloudlet> newList = broker.getCloudletReceivedList();

		CloudSim.stopSimulation();

		CloudTool.printCloudletList(newList);

		Log.printLine("Min-Min finished!");
    	
	}
	
	private static Datacenter createDatacenter(String name){ 
    	List<Host> hostList = new ArrayList<Host>(); 
    	List<Pe> peList = new ArrayList<Pe>(); 
    	int mips = 6000; 
    	peList.add(new Pe(0, new PeProvisionerSimple(mips)));  
        int hostId=0;
        int ram = 2048; //host memory (MB)
        long storage = 1000000; //host storage
        int bw = 10000;

        hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList,
    				new VmSchedulerTimeShared(peList)
    			)
    		); 
        String arch = "x86";      // system architecture
        String os = "Linux";          // operating system
        String vmm = "Xen";
        double time_zone = 10.0;         // time zone this resource located
        double cost = 3.0;              // the cost of using processing in this resource
        double costPerMem = 0.05;		// the cost of using memory in this resource
        double costPerStorage = 0.001;	// the cost of using storage in this resource
        double costPerBw = 0.0;			// the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);


        // 6. Finally, we need to create a PowerDatacenter object.
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }
	
	  private static DatacenterBroker createBroker(){

	    	DatacenterBroker broker = null;
	        try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	    	return broker;
	    }
		


		
		
}
