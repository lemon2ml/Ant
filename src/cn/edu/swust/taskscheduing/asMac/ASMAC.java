package cn.edu.swust.taskscheduing.asMac;

import cn.edu.swust.taskscheduing.ExpData;
import cn.edu.swust.taskscheduing.ExpDataTool;

public class ASMAC {
	private int GEN_MAX;//����
	private int clanNums;//������
	
	private AC[] antClans;//����
	
	private int taskNum; // ��������
	private int vmNum; //��������� 
	private int antNum; // ����������ÿ����Ⱥ��
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
	
	private float w=0.6f;//������Ȩ��
	public void solve(){
		for(int gen=0;gen<GEN_MAX;gen++){
			for(int index=0;index<clanNums;index++){
				antClans[index].nextGen();
				//�������Ϣ��ͨ��
				int beforeIndex=index-1;
				if(index==0){
					beforeIndex=clanNums-1;
				}
				for(int i=0;i<taskNum;i++){
					for(int j=0;j<vmNum;j++){
						antClans[index].getPheromone()[i][j]=w*antClans[index].getPheromone()[i][j]+(1-w)*antClans[beforeIndex].getPheromone()[i][j];
					}
				}
				System.out.println("��"+gen+"��"+"��"+index+"��"+antClans[index].getBestCost());
			}
		}
	}
	public static void main(String[] args) throws Exception {
		ExpData expData= ExpDataTool.get("E:/Cloudsimʹ��/�������/100task-5vm.json", 1); 
		ASMAC asmac = new ASMAC(500, 3, expData);
		asmac.init(expData.taskNum, 1.0f, 3.0f, 0.5f);
		asmac.solve();
	}
}
