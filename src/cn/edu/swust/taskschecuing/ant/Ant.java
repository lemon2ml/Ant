package cn.edu.swust.taskschecuing.ant; 

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import cn.edu.swust.taskscheduingmethed.MCT;

/**
 * 
 * 基于资源状态
 * 
 */
public class Ant implements Cloneable {
 
	private Vector<Integer> allowedTask; // 允许搜索的任务
	private float[] costVM; // 每个虚拟机的花费
	private float[][] delta; // 信息数变化矩阵
	private Float[][] etc; // 距离矩阵


	private float alpha;
	private float beta;

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

	public Ant() {
		taskNum = 25;
	}

	/**
	 * Constructor of Ant
	 * 
	 * @param num
	 *            蚂蚁数量
	 */
	public Ant(int num, int vmNum) {
		taskNum = num;
		this.vmNum = vmNum;
	}

	/**
	 * 初始化蚂蚁，随机选择起始位置
	 * 
	 * @param timeCost
	 *            距离矩阵
	 * @param a
	 *            alpha
	 * @param b
	 *            beta
	 */
	public void init(Float[][] etc, float a, float b) {
		alpha = a;// 信息素重要性
		beta = b; // 可见度重要性
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
	 * 
	 * @param pheromone
	 *            信息素矩阵
	 */
	public void selectNextTask(float[][] pheromone) {
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

		// 从允许选择的任务中去除selectTask
		allowedTask.remove(selectTask);  
		//更新虚拟机状态
		costVM[selectVm] += etc[selectTask][selectVm];
		//执行列表更新
		seqExcute.add(new MCT(selectTask, selectVm, etc[selectTask][selectVm]));
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
	public Vector<MCT> getSeqExcute() {
		return seqExcute;
	}
	public void setSeqExcute(Vector<MCT> seqExcute) {
		this.seqExcute = seqExcute;
	}
	public int getTaskNum() {
		return taskNum;
	}
	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}
	public int getVmNum() {
		return vmNum;
	}
	public void setVmNum(int vmNum) {
		this.vmNum = vmNum;
	}

}