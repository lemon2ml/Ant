package cn.edu.swust.taskscheduing.asMac;

import cn.edu.swust.taskscheduing.ExpData;
import cn.edu.swust.taskscheduing.ExpDataTool;

public class ASMAC {
	private int GEN_MAX;//代数
	private int clanNums;//种族数
	
	private AC[] antClans;//种族
	
	private int taskNum; // 任务数量
	private int vmNum; //虚拟机数量 
	private int antNum; // 蚂蚁数量（每个种群）
	private float alpha;
	private float beta;
	private float rho;
	private float t0=0.001f;
	private ExpData expData;
	
	public ASMAC(int genMax, int clanNums,ExpData expData){
		this.GEN_MAX=genMax;
		this.clanNums=clanNums;
		this.expData=expData;
		this.taskNum=expData.taskNum;
		this.vmNum=expData.vmNum;
	}
	
	public void init( int antNum, float alf, float beta, float rho){
		this.antNum=antNum;
		this.alpha=alf;
		this.beta=beta;
		this.rho=rho;
		
		antClans=new AC[clanNums];
		for(int i=0;i<clanNums;i++){
			antClans[i]= new AC(taskNum, vmNum, antNum, alf, beta, rho, t0);
			antClans[i].init(expData);
		}
	}
	
	private float w=0.6f;//本种族权重
	public void solve(){
		for(int gen=0;gen<GEN_MAX;gen++){
			for(int index=0;index<clanNums;index++){
				antClans[index].nextGen();
				//种族间信息素通信
				int beforeIndex=index-1;
				if(index==0){
					beforeIndex=clanNums-1;
				}
				for(int i=0;i<taskNum;i++){
					for(int j=0;j<vmNum;j++){
						antClans[index].getPheromone()[i][j]=w*antClans[index].getPheromone()[i][j]+(1-w)*antClans[beforeIndex].getPheromone()[i][j];
					}
				}
				System.out.println("第"+gen+"代"+"第"+index+"族"+antClans[index].getBestCost());
			}
		}
	}
	public static void main(String[] args) throws Exception {
		ExpData expData= ExpDataTool.get("E:/Cloudsim使用/任务调度/100task-5vm.json", 1); 
		ASMAC asmac = new ASMAC(500, 3, expData);
		asmac.init(expData.taskNum, 1.0f, 3.0f, 0.5f);
		asmac.solve();
	}
}
