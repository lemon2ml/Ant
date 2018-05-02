package cn.edu.swust.taskschecuing.acs; 

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import cn.edu.swust.taskscheduingmethed.MCT;

/**
 * 
 * 基于资源状态
 * 
 */
public class ACS implements Cloneable {
 
	private Vector<Integer> allowedTask; // 允许搜索的任务
	private float[] costVM; // 每个虚拟机的花费
	private float[][] delta; // 信息数变化矩阵
	private Float[][] etc; // 距离矩阵

	private float t0;
	private float rho;
	private float alpha;
	private float beta;
	private float q0=0.5f;
	private Float timecostTotle; // 路径长度
	private Vector<MCT> seqExcute;// 路径
	private int taskNum; // 任务数量
	private int vmNum; // 虚拟机数量

	public float[][] getDelta() {
		return delta;
	}

	public void setDelta(float[][] delta) {
		this.delta = delta;
	}

	public ACS() {
		taskNum = 25;
	}

	/**
	 * Constructor of Ant
	 * 
	 * @param num
	 *            蚂蚁数量
	 */
	public ACS(int num, int vmNum) {
		taskNum = num;
		this.vmNum = vmNum;
	}

	/**
	 * 初始化蚂蚁，随机选择起始位置
	 * 
	 * @param etc   距离矩阵
	 * @param a   alpha
	 * @param b   beta
	 */
	public void init(Float[][] etc, float a, float b,float t0,float rho) {
		alpha = a;// 信息素重要性
		beta = b; // 可见度重要性
		this.t0=t0;
		this.rho=rho;
		timecostTotle = null;
		this.etc = etc; // 初始化每个任务在每个虚拟机上执行的时间花费
		// 初始化每个虚拟机当前的花费
		costVM = new float[vmNum]; 
		for (int i = 0; i < vmNum; i++) { // 初始化花费
			costVM[i] = 0;// 初始化每个虚拟机花费的时间
		}
		
		allowedTask = new Vector<Integer>();// 允许访问的任务列表
		delta = new float[taskNum][vmNum]; // 释放信息素
		for (int i = 0; i < taskNum; i++) {
			allowedTask.add(Integer.valueOf(i));
			for (int j = 0; j < vmNum; j++) {
				delta[i][j] = 0.f;
			}
		}
		
		int taskseq = ThreadLocalRandom.current().nextInt(taskNum);// 随机选择一个任务		
		int vmseq = ThreadLocalRandom.current().nextInt(vmNum);// 随机选定一个虚拟机 		
		costVM[vmseq] += etc[taskseq][vmseq]; // 更新虚拟机的时间消耗		
		
		allowedTask.remove(Integer.valueOf(taskseq));// 任务表中减少任务
		//执行队列
		seqExcute = new Vector<MCT>();
		seqExcute.add(new MCT(taskseq, vmseq, etc[taskseq][vmseq]));
	}

	/**
	 * 选择下一个<T,V> 
	 * @param pheromone     信息素矩阵
	 */
	public void selectNextTask(float[][] pheromone) {
		MCT mct=null;
		/**伪随机比例选择**/
		float q = ThreadLocalRandom.current().nextFloat();
		if(q<=q0){
			mct = nextByQ(pheromone);
		}else{			
			mct = nextByP(pheromone);
		}
		// 从允许选择的任务中去除selectTask
		allowedTask.remove(Integer.valueOf(mct.taskIndex));  
		//更新虚拟机状态
		costVM[mct.vmIndex] += etc[mct.taskIndex][mct.vmIndex];
		//执行列表更新
		seqExcute.add(mct);
		
		//局部信息素更新
		pheromone[mct.taskIndex][mct.vmIndex]=(1-rho)*pheromone[mct.taskIndex][mct.vmIndex]+rho*t0;
		
		/*System.out.println(mct.taskIndex+","+mct.vmIndex);
		for (int i = 0; i < taskNum; i++){
			for (int j = 0; j < vmNum; j++){
				System.out.print(pheromone[i][j]+" ");
			}
			System.out.println();
		}*/
	}
	/**
	 * q<=q0,求最大信息素
	 * @param pheromone
	 * @return
	 */
	private MCT nextByQ(float[][] pheromone) {
		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;
		int selectTask=0,selectVm=0;
		for (Integer j : allowedTask) {
			for (int k = 0; k < vmNum; k++) {
				float max2 = (float) (Math.pow(pheromone[j][k], alpha) * Math.pow(
						1.0 / (etc[j][k] + costVM[k]), beta));
				if(max2>max){
					max=max2;
					selectTask=j;
					selectVm=k;
				}
				if(min<max2){
					min=max2;
				}
			}
		}
		if(min==max){
			MCT mct=nextByP(pheromone);
			return mct;
		}
		MCT mct=new MCT(selectTask, selectVm, etc[selectTask][selectVm]);
		return mct;
	}
	/**
	 * 通过概率计算，并轮盘赌选得到下一节点
	 * @param pheromone
	 * @return
	 */
	private MCT nextByP(float[][] pheromone) {
		float[][] p = new float[taskNum][vmNum];
		float sum = 0.0f;
		// 计算分母部分
		for (Integer i : allowedTask) {
			for (int j = 0; j < vmNum; j++) {
				sum += Math.pow(pheromone[i][j], alpha)
						* Math.pow(1.0 / (etc[i][j] + costVM[j]), beta);
			}
		}
		// 计算概率矩阵
		for (Integer j : allowedTask) {
			for (int k = 0; k < vmNum; k++) {
				p[j][k] = (float) (Math.pow(pheromone[j][k], alpha) * Math.pow(
						1.0 / (etc[j][k] + costVM[k]), beta)) / sum;
			}
		}

		// 轮盘赌选择下一个任务，虚拟机
		float sleectP = ThreadLocalRandom.current().nextFloat();
		Integer selectTask = 0, selectVm = 0;

		float sum1 = 0.f;
		a: for (Integer i : allowedTask) {
			for (int j = 0; j < vmNum; j++) {
				sum1 += p[i][j];
				if (sum1 >= sleectP) {
					selectTask = i;
					selectVm = j;
					break a;
				}
			}
		}
		MCT mct=new MCT(selectTask, selectVm, etc[selectTask][selectVm]);
		return mct;
	}

	/**
	 * 计算路径长度
	 * 
	 * @return 路径长度
	 */
	private float calculateTotalCost() {
		float cost = 0;
		for (int i = 0; i < vmNum; i++) {
			if (costVM[i] > cost) {
				cost = costVM[i];
			}
		}
		return cost;
	}
	public Float getTimecostTotle() {
		if (timecostTotle == null)
			timecostTotle = calculateTotalCost();
		return timecostTotle;
	}
	public void setTimecostTotle(Float timecostTotle) {
		this.timecostTotle = timecostTotle;
	}
	public Vector<Integer> getAllowedTask() {
		return allowedTask;
	}
	public void setAllowedTask(Vector<Integer> allowedTask) {
		this.allowedTask = allowedTask;
	}
	public float[] getCostVM() {
		return costVM;
	}
	public void setCostVM(float[] costVM) {
		this.costVM = costVM;
	}
	public Float[][] getEtc() {
		return etc;
	}
	public void setEtc(Float[][] etc) {
		this.etc = etc;
	}
	public Vector<MCT> getSeqExcute() {
		return seqExcute;
	}
	public void setSeqExcute(Vector<MCT> seqExcute) {
		this.seqExcute = seqExcute;
	}


}