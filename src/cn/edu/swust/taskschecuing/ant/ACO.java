package cn.edu.swust.taskschecuing.ant;

import java.io.IOException;
import java.util.Vector;

import cn.edu.swust.taskscheduing.ExpData;
import cn.edu.swust.taskscheduing.ExpDataTool;
import cn.edu.swust.taskscheduingmethed.MCT;

/**
 * 基于资源状态
 * 基本蚁群算法
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
	private float t0=0.000001f;
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
	 * @param expData filename 数据文件名，该文件存储所有城市节点坐标数据
	 * @throws IOException
	 */
	public void init(ExpData expData) throws IOException { 

		etc = expData.etc;

		// 初始化信息素矩阵
		pheromone = new float[taskNum][vmNum];
		for (int i = 0; i < taskNum; i++) {
			for (int j = 0; j < vmNum; j++) {
				pheromone[i][j] = t0; // 初始化为0.1
			}
		}
		bestCost = Integer.MAX_VALUE;
		// 随机放置蚂蚁
		for (int i = 0; i < antNum; i++) {
			ants[i] = new Ant(taskNum,vmNum);
			ants[i].init(etc, alpha, beta);
		}
	}

	public void solve() {

		for (int g = 0; g < MAX_GEN; g++) {
			for (int i = 0; i < antNum; i++) {
				for (int j = 1; j < taskNum; j++) {
					ants[i].selectNextTask(pheromone);
				}
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

			// 更新信息素
			updatePheromone();

			// 重新初始化蚂蚁
			for (int i = 0; i < antNum; i++) {
				ants[i].init(etc, alpha, beta);
			}
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
	}

	private void printOptimal() {
		System.out.println("The optimal length is: " + bestCost);
		System.out.println("The optimal tour is: ");
		for (int i = 0; i < taskNum; i++) {
			System.out.println(seqExcute.get(i).taskIndex+"--"+seqExcute.get(i).vmIndex);
		}
	}

	public Ant[] getAnts() {
		return ants;
	}
	public void setAnts(Ant[] ants) {
		this.ants = ants;
	}
	public int getAntNum() {
		return antNum;
	}
	public void setAntNum(int m) {
		this.antNum = m;
	}
	public int getCityNum() {
		return taskNum;
	}
	public void setCityNum(int cityNum) {
		this.taskNum = cityNum;
	}
	public int getMAX_GEN() {
		return MAX_GEN;
	}
	public void setMAX_GEN(int mAX_GEN) {
		MAX_GEN = mAX_GEN;
	}
	public float[][] getPheromone() {
		return pheromone;
	}
	public void setPheromone(float[][] pheromone) {
		this.pheromone = pheromone;
	}
	public void setBestLength(int bestLength) {
		this.bestCost = bestLength;
	}
	public float getAlpha() {
		return alpha;
	}
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	public float getBeta() {
		return beta;
	}
	public void setBeta(float beta) {
		this.beta = beta;
	}
	public float getRho() {
		return rho;
	}
	public void setRho(float rho) {
		this.rho = rho;
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
