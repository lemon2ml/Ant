package cn.edu.swust.taskscheduing.minminant;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import cn.edu.swust.taskscheduingmethed.MCT;

/**
 * 
 * @author BIAO YU
 * 
 */
public class Ant implements Cloneable {

	private Vector<Integer> tabu; // ��������
	private Vector<Integer> allowedTask; // ��������������
	private float[] costVM; // ÿ��������Ļ���
	private float[][] delta; // ��Ϣ���仯����
	private Float[][] etc; // �������

	public Vector<Integer> getTabu() {
		return tabu;
	}

	public void setTabu(Vector<Integer> tabu) {
		this.tabu = tabu;
	}

	public float[][] getDelta() {
		return delta;
	}

	public void setDelta(float[][] delta) {
		this.delta = delta;
	}

	private float alpha;
	private float beta;

	private float timecostTotle; // ·������
	Vector<MCT> seqExcute;// ·��
	private int taskNum; // ��������
	private int vmNum; // ���������
	private MCT firstTask; // ��ʼ����
	private MCT currentTask; // ��ǰ����

	public Ant() {
		taskNum = 25;
		timecostTotle = 0; 
	}

	/**
	 * Constructor of Ant 
	 * @param num
	 *            ��������
	 */
	public Ant(int num, int vmNum) {
		taskNum = num;
		this.vmNum = vmNum;
		timecostTotle = 0; 
	}

	/**
	 * ��ʼ�����ϣ����ѡ����ʼλ��
	 * @param timeCost �������
	 * @param a    alpha
	 * @param b    beta
	 */
	public void init(Float[][] etc, float a, float b) {
		alpha = a;// ��Ϣ����Ҫ��
		beta = b; // �ɼ�����Ҫ��
		allowedTask = new Vector<Integer>();// ������ʵ������б�
		tabu = new Vector<Integer>(); // ���ɱ�
		costVM = new float[vmNum]; // ÿ���������ǰ�Ļ���
		for (int i = 0; i < vmNum; i++) { // ��ʼ������
			costVM[i] = 0;// ��ʼ��ÿ����������ѵ�ʱ��
		}
		seqExcute = new Vector<MCT>();
		this.etc = etc; // ��ʼ��ÿ��������ÿ���������ִ�е�ʱ�仨��
		delta = new float[taskNum][vmNum]; // �ͷ���Ϣ��
		for (int i = 0; i < taskNum; i++) {
			allowedTask.add(Integer.valueOf(i));
			for (int j = 0; j < vmNum; j++) {
				delta[i][j] = 0.f;
			}
		}
		int taskseq = ThreadLocalRandom.current().nextInt(taskNum);// ���ѡ��һ������
																	// //ѡ��һ�������
		int vmseq = ThreadLocalRandom.current().nextInt(vmNum);
		firstTask = new MCT(taskseq, vmseq, etc[taskseq][vmseq]);
		costVM[vmseq] += firstTask.time; // �����������ʱ������
		for (Integer i : allowedTask) { // ������м�������
			if (i.intValue() == firstTask.taskIndex) {
				allowedTask.remove(i);
				break;
			}
		}
		tabu.add(Integer.valueOf(firstTask.taskIndex));// ��ӵ����ɱ���
		currentTask = firstTask;
		seqExcute.add(currentTask);
	}

	/**
	 * ѡ����һ������
	 * 
	 * @param pheromone     ��Ϣ�ؾ���
	 */
	public void selectNextTask(float[][] pheromone) {
		float[][] p = new float[taskNum][vmNum];
		float sum = 0.0f;
		// �����ĸ����
		for (Integer i : allowedTask) {
			for (int j = 0; j < vmNum; j++) {
				sum += Math.pow(pheromone[i][j], alpha)
						* Math.pow(1.0 / (etc[i][j] + costVM[j]), beta);
			}
		}
		// ������ʾ���
		for (int i = 0; i < taskNum; i++) {
			boolean flag = false;
			for (Integer j : allowedTask) {
				if (i == j.intValue()) {
					for (int k = 0; k < vmNum; k++) {
						p[i][k] = (float) (Math.pow(pheromone[i][k], alpha) * Math
								.pow(1.0 / (etc[i][k] + costVM[k]), beta))
								/ sum;
					}
					flag = true;
					break;
				}
			}
			if (flag == false) {
				for (int k = 0; k < vmNum; k++) {
					p[i][k] = 0;
				}
			}
		}

		// ���̶�ѡ����һ�����������
		float sleectP = ThreadLocalRandom.current().nextFloat();
		int selectTask = 0, selectVm = 0;

		float sum1 = 0.f;
		a: for (int i = 0; i < taskNum; i++) {
			for (int j = 0; j < vmNum; j++) {
				sum1 += p[i][j];
				if (sum1 >= sleectP) {
					selectTask = i;
					selectVm = j;
					break a;
				}
			}
		}

		// ������ѡ���������ȥ��select task
		for (Integer i : allowedTask) {
			if (i.intValue() == selectTask) {
				allowedTask.remove(i);
				break;
			}
		}
		// �ڽ��ɱ������selectTask
		tabu.add(Integer.valueOf(selectTask));
		// ����ǰ�����Ϊѡ�������
		currentTask = new MCT(selectTask, selectVm, etc[selectTask][selectVm]);
		costVM[selectVm] += etc[selectTask][selectVm];
		seqExcute.add(currentTask);
	}

	/**
	 * ����·������
	 * 
	 * @return ·������
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

	public float getTimecostTotle() {
		timecostTotle = calculateTotalCost();
		return timecostTotle;
	}

	public void setTimecostTotle(float timecostTotle) {
		this.timecostTotle = timecostTotle;
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

	public MCT getFirstTask() {
		return firstTask;
	}

	public void setFirstTask(MCT firstTask) {
		this.firstTask = firstTask;
	}

	public MCT getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(MCT currentTask) {
		this.currentTask = currentTask;
	}

}