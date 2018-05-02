package cn.edu.swust.taskscheduing.asElite;

import java.io.IOException;
import java.util.Vector;

import cn.edu.swust.taskschecuing.ant.Ant;
import cn.edu.swust.taskscheduing.ExpData;
import cn.edu.swust.taskscheduing.ExpDataTool;
import cn.edu.swust.taskscheduingmethed.MCT;

 
/**
 * 基于资源状态
 * 精英蚁群算法
 *  
 */
public class ACO {
	
	private Ant[] ants; // 蚂蚁
	private int antNum; // 蚂蚁数量
	private int taskNum; // 任务数量
	private int vmNum; //虚拟机数量
	private int MAX_GEN; // 运行代数
	private float[][] pheromone; // 信息素矩阵
	private Float[][] etc; // 距离矩阵
	private float bestCost; // 最佳长度
	Vector<MCT> seqExcute; // 最佳路径
	
	// 三个参数
	private float alpha;
	private float beta;
	private float rho;
	
	private float Q=1.0f;
	public ACO() {

	}

	/**
	 * 
	 * @param taskn
	 * @param vmNum
	 * @param antn
	 * @param MAX_GEN
	 * @param a
	 * @param b
	 * @param r
	 */
	public ACO(int taskn,int vmNum, int antn, int MAX_GEN, float a, float b, float r) {
		this.taskNum = taskn;
		this.vmNum=vmNum;
		this.antNum = antn;
		this.ants = new Ant[antNum];
		this.MAX_GEN = MAX_GEN;
		this.alpha = a;
		this.beta = b;
		this.rho = r;

	}

	public Vector<MCT> getSeqExcute() {
		return seqExcute;
	}

	public void setSeqExcute(Vector<MCT> seqExcute) {
		this.seqExcute = seqExcute;
	}

	/**
	 * 初始化ACO算法类
	 * @param filename 数据文件名，该文件存储所有城市节点坐标数据
	 * @throws IOException
	 */
	public void init(ExpData expData) throws IOException { 

		etc = expData.etc;

		// 初始化信息素矩阵
		pheromone = new float[taskNum][vmNum];
		for (int i = 0; i < taskNum; i++) {
			for (int j = 0; j < vmNum; j++) {
				pheromone[i][j] = 0.001f; // 初始化为0.001
			}
		}
		bestCost = Float.MAX_VALUE;
		
		for (int i = 0; i < antNum; i++) {
			ants[i] = new Ant(taskNum,vmNum); 
		}
	}

	public void solve() {

		for (int g = 0; g < MAX_GEN; g++) { 
			//1.一代蚂蚁，第只蚂蚁寻径
			for (int i = 0; i < antNum; i++) {
				ants[i].init(etc, alpha, beta);// 随机放置蚂蚁
				//1.1寻径
				for (int j = 1; j < taskNum; j++) {
					ants[i].selectNextTask(pheromone);
				}
				//1.2比较，取最优
				if (ants[i].getTimecostTotle() < bestCost) {
					bestCost = ants[i].getTimecostTotle();
					seqExcute = ants[i].getSeqExcute();
				}
				
				//1.3求一只蚂蚁在所走过的路径上释放信息素
				float delta=Q / ants[i].getTimecostTotle();
				for(MCT mct:ants[i].getSeqExcute()){
					ants[i].getDelta()[mct.taskIndex][mct.vmIndex] = delta;
				}
			}

			// 2.对每只蚂蚁更新信息素
			updatePheromone();

			
			System.out.println("第"+g+"代："+bestCost);
		}

		// 打印最佳结果
		printOptimal();
	}

	// 更新信息素
	private void updatePheromone() {
		// 信息素挥发
		for (int i = 0; i < taskNum; i++)
			for (int j = 0; j < vmNum; j++)
				pheromone[i][j] = pheromone[i][j] * (1 - rho);
		// 信息素更新
		for (int i = 0; i < taskNum; i++) {
			for (int j = 0; j < vmNum; j++) {
				for (int k = 0; k < antNum; k++) {
					pheromone[i][j] += ants[k].getDelta()[i][j];
				}
			}
		}
		System.out.println(pheromone);
		//精英更新
		float deltaTao=Q/bestCost;
		for(MCT mct:seqExcute){
			int i=mct.taskIndex;
			int j=mct.vmIndex;
			//经过最优解的蚂蚁数
			for(int k =0;k<ants.length;k++){
				if(ants[k].getDelta()[i][j]>0)
				pheromone[i][j] += deltaTao;	
			}
		}
		System.out.println(pheromone);
	}

	private void printOptimal() {
		System.out.println("The optimal length is: " + bestCost);
		System.out.println("The optimal tour is: ");
		for (int i = 0; i < taskNum; i++) {
			System.out.print(seqExcute.get(i).taskIndex+","+seqExcute.get(i).vmIndex+"->");
		}
		System.out.println();
	}

	

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		ExpData expData= ExpDataTool.get("E:/Cloudsim使用/任务调度/100task-5vm.json", 1); 
		ACO aco = new ACO(expData.taskNum, expData.vmNum, expData.taskNum,100, 1.f, 5.f, 0.5f); 
		aco.init(expData);
		aco.solve();
	}

}
