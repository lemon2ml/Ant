package cn.edu.swust.taskscheduing.minminant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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

import cn.edu.swust.taskscheduing.ExpData;
import cn.edu.swust.taskscheduing.ExpDataTool;
import cn.edu.swust.taskscheduingmethed.CloudTool;
import cn.edu.swust.taskscheduingmethed.MCT;


public class ACOschedue {
	/** The cloudlet list. */
	private  List<Cloudlet> cloudletList;
	private DatacenterBroker  broker;
	/** The vmlist. */
	private  List<Vm> vmlist;
	public static void main(String[] args) throws Exception {
		ACOschedue acoSchedue = new ACOschedue();
		//ExpData expData= ExpDataTool.get("E:/Cloudsim使用/任务调度/25task-5vm.json",9);
		//ExpData expData= ExpDataTool.get("E:/Cloudsim使用/任务调度/50task-5vm.json", 9);
		//ExpData expData= ExpDataTool.get("E:/Cloudsim使用/任务调度/75task-5vm.json", 9);
		ExpData expData= ExpDataTool.get("E:/Cloudsim使用/任务调度/100task-5vm.json", 3);
		//ACO aco = new ACO(100, 5, 100,500, 1.f, 5.f, 0.5f);  
		int num_user = 1;   // number of cloud users
    	Calendar calendar = Calendar.getInstance();
    	boolean trace_flag = false;  // mean trace events 
    	CloudSim.init(num_user, calendar, trace_flag);
 
    	@SuppressWarnings("unused")
		Datacenter datacenter0 = acoSchedue.createDatacenter("Datacenter_0");
 
    	acoSchedue.broker = acoSchedue.createBroker();
    	int brokerId = acoSchedue.broker.getId();    	

    	acoSchedue.vmlist=CloudTool.createVM(brokerId, expData.mip);
    	acoSchedue.cloudletList=CloudTool.createCloudlet(brokerId, expData.mi);
    	 
    	List cloudlets2 =new ArrayList(); 
    	cloudlets2.addAll(acoSchedue.cloudletList);
    	
    	ACO aco = new ACO(expData.taskNum, expData.vmNum, expData.taskNum,500, 1.f, 5.f, 0.5f);  
		aco.init(expData);
		aco.solve(acoSchedue);
		
		//Vector<MCT> seqExcute=aco.getSeqExcute();
    	 
		//acoSchedue.toSimulation(broker, seqExcute);
    	
	}

	public  void toSimulation(
			Vector<MCT> seqExcute) {
		broker.submitVmList(vmlist);
	 	broker.submitCloudletList(cloudletList);
    	for(MCT mct:seqExcute){
    		broker.bindCloudletToVm(cloudletList.get(mct.taskIndex).getCloudletId(), vmlist.get(mct.vmIndex).getId());
    		//System.out.println(mct.taskIndex+"--"+mct.vmIndex);
    	} 		
		CloudSim.startSimulation();
		List<Cloudlet> newList = broker.getCloudletReceivedList();
		CloudSim.stopSimulation();
		CloudTool.printCloudletList(newList);
		Log.printLine("ACO finished!");
	}
	
	private  Datacenter createDatacenter(String name){ 
    	List<Host> hostList = new ArrayList<Host>(); 
    	List<Pe> peList = new ArrayList<Pe>(); 
    	int mips = 6000; 
    	peList.add(new Pe(0, new PeProvisionerSimple(mips)));  
        int hostId=0;
        int ram = 4096; //host memory (MB)
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
	
	  private  DatacenterBroker createBroker(){

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
