package cn.edu.swust.taskscheduing.asMac;

import java.io.IOException;
import java.util.Vector;

import cn.edu.swust.taskschecuing.ant.Ant;
import cn.edu.swust.taskscheduing.ExpData;
import cn.edu.swust.taskscheduingmethed.MCT;

 
/**
 * 基于资源状态
 * 多重蚁群算法
 * 此类表示一群
 */
public class AC {
	
	private Ant[] ants; // 蚂蚁
	private int antNum; // 蚂蚁数量
	private int taskNum; // 任务数量
	private int vmNum; //虚拟机数量 
	private float[][] pheromone; // 信息素矩阵
	private Float[][] etc; // 距离矩阵
	private float bestCost; // 最佳长度
	Vector<MCT> seqExcute; // 最佳路径
	
	// 三个参数
	private float alpha;
	private float beta;
	private float rho;
	private float t0;
	private float Q=1.0f;
	public AC() {

	}

	/**
	 * 
	 * @param taskNum
	 * @param vmNum
	 * @param antNum
	 * @param MAX_GEN
	 * @param alf
	 * @param beta
	 * @param rho
	 */
	public AC(int taskNum,int vmNum, int antNum, float alf, float beta, float rho,float t0) {
		this.taskNum = taskNum;
		this.vmNum=vmNum;
		this.antNum = antNum;
		this.ants = new Ant[antNum];
		this.alpha = alf;
		this.beta = beta;
		this.rho = rho;
		this.t0=t0;
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
	public void init(ExpData expData){ 

		etc = expData.etc;

		// 初始化信息素矩阵
		pheromone = new float[taskNum][vmNum];
		for (int i = 0; i < taskNum; i++) {
			for (int j = 0; j < vmNum; j++) {
				pheromone[i][j] = t0; // 初始化为信息素
			}
		}
		bestCost = Float.MAX_VALUE;
		
		for (int i = 0; i < antNum; i++) {
			ants[i] = new Ant(taskNum,vmNum); 
		}
	}

	public void nextGen() {
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

			// 2.对每只蚂蚁更新信息素（局部更新）
			updatePheromone();
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
	}

	private void printOptimal() {
		System.out.println("The optimal length is: " + bestCost);
		System.out.println("The optimal tour is: ");
		for (int i = 0; i < taskNum; i++) {
			System.out.print(seqExcute.get(i).taskIndex+","+seqExcute.get(i).vmIndex+"->");
		}
		System.out.println();
	}

	public float[][] getPheromone() {
		return pheromone;
	}

	public void setPheromone(float[][] pheromone) {
		this.pheromone = pheromone;
	}
	public float getBestCost(){
		return bestCost;
	}
	


}
